package info.narazaki.android.tuboroid.view;

import info.narazaki.android.lib.system.MotionEventWrapper;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.Scroller;

public class ScrollImageView extends ImageView implements OnTouchListener {

    public interface OnMoveImageListner {
        abstract void onMoveImage(boolean is_next);

    }

    public ScrollImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        setOnTouchListener(this);

        try {
            MotionEventWrapper.checkAvailable();
            event_wrapper = new MotionEventWrapper();
        } catch (final Throwable e) {

        }

        setHorizontalFadingEdgeEnabled(true);
        setVerticalFadingEdgeEnabled(true);
        setHorizontalScrollBarEnabled(true);
        setVerticalScrollBarEnabled(true);

        scroller = new Scroller(getContext());

        gesture_detecter = new GestureDetector(new OnGestureListener() {

            @Override
            public boolean onSingleTapUp(final MotionEvent e) {
                return ScrollImageView.this.onDoubleTap(e);
            }

            @Override
            public void onShowPress(final MotionEvent e) {
            }

            @Override
            public boolean onScroll(final MotionEvent e1, final MotionEvent e2, final float distanceX, final float distanceY) {
                scrollBy((int) distanceX, (int) distanceY);
                scroller.abortAnimation();
                return false;
            }

            @Override
            public void onLongPress(final MotionEvent e) {
            }

            @Override
            public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX, final float velocityY) {
                scroller.fling(getScrollX(), getScrollY(), -(int) velocityX * 2, -(int) velocityY * 2, getScrollMinX(),
                        getScrollMaxX(), getScrollMinY(), getScrollMaxY());

                handler.postDelayed(runnable, REPEAT_INTERVAL);
                return false;
            }

            @Override
            public boolean onDown(final MotionEvent e) {
                scroller.abortAnimation();
                return false;
            }
        });

        gesture_detecter.setOnDoubleTapListener(new OnDoubleTapListener() {

            @Override
            public boolean onSingleTapConfirmed(final MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(final MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTap(final MotionEvent e) {
                return ScrollImageView.this.onDoubleTap(e);
            }
        });

        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {

                // computeScrollOffsetはフリングによるスクロールが終了していたらfalseを返す
                if (scroller.computeScrollOffset()) {
                    scrollTo(scroller.getCurrX(), scroller.getCurrY());

                    handler.postDelayed(this, REPEAT_INTERVAL);
                }
            }
        };

        onUserAction();
    }

    final int REPEAT_INTERVAL = 20;
    private Runnable runnable;
    private Handler handler;
    private GestureDetector gesture_detecter;
    private Scroller scroller;
    private ImageViewerFooter footer;
    private OnMoveImageListner on_move_image = null;

    public void setFooter(final ImageViewerFooter footer) {
        this.footer = footer;
        this.footer.setScale(getScale());
    }

    public void setOnMoveImageListner(final OnMoveImageListner on_move_image) {
        this.on_move_image = on_move_image;
    }

    @Override
    public void setImageBitmap(final Bitmap bm) {
        super.setImageBitmap(bm);

        scrollTo(0, 0);
        onUserAction();
        if (bm == null) {
            footer.setImageSize(0, 0);
            return;
        }

        image_x = bm.getWidth();
        image_y = bm.getHeight();
        footer.setImageSize(image_x, image_y);

        zoomForWholeImageView();
    }

    @Override
    public void scrollBy(final int x, final int y) {
        int a_x = getScrollX() + x;
        int a_y = getScrollY() + y;

        a_x = Math.max(a_x, getScrollMinX());
        a_y = Math.max(a_y, getScrollMinY());
        a_x = Math.min(a_x, getScrollMaxX());
        a_y = Math.min(a_y, getScrollMaxY());

        super.scrollTo(a_x, a_y);
    }

    @Override
    protected float getTopFadingEdgeStrength() {
        return computeVerticalScrollOffset() > 0 ? 1.0f : 0.0f;
    }

    @Override
    protected float getBottomFadingEdgeStrength() {
        return (int) (getScale() * image_y - getHeight()) > computeVerticalScrollOffset() ? 1.0f : 0.0f;
    }

    @Override
    protected float getLeftFadingEdgeStrength() {
        return computeHorizontalScrollOffset() > 0 ? 1.0f : 0.0f;
    }

    @Override
    protected float getRightFadingEdgeStrength() {
        return (int) (getScale() * image_x - getWidth()) > computeHorizontalScrollOffset() ? 1.0f : 0.0f;
    }

    final int STATE_NONE = 0;
    final int STATE_ZOOMING = 1;
    private int touch_state = 0;
    private final PointF[] pointers = new PointF[] { new PointF(), new PointF() };
    private final int[] zoom_ids = new int[] { 0, 0 };
    private MotionEventWrapper event_wrapper = null;
    private int image_x, image_y;
    private Runnable prev_vanish_ani = null;

    private int getPointerIndex(final MotionEvent event, final int id) {
        event_wrapper.set(event);
        for (int i = 0; i < event_wrapper.getPointerCount(); i++) {
            if (event_wrapper.getPointerId(i) == id) {
                return i;
            }
        }
        return -1;
    }

    private PointF getPointerPosById(final MotionEvent event, final int id) {
        event_wrapper.set(event);
        final int i = getPointerIndex(event, id);
        return new PointF(event_wrapper.getX(i), event_wrapper.getY(i));
    }

    private float calcDiffLen(final float x1, final float y1, final float x2, final float y2) {
        return (float) Math.sqrt((Math.pow(x1 - x2, 2.0) + Math.pow(y1 - y2, 2.0)));
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        gesture_detecter.onTouchEvent(event);
        onUserAction();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            break;
        case MotionEvent.ACTION_POINTER_DOWN:
            event_wrapper.set(event);
            if (event_wrapper.getPointerCount() == 2) {
                startZoom(event, new int[] { 0, 1 });
            }
            break;
        case MotionEvent.ACTION_MOVE:
            if (touch_state == STATE_ZOOMING) {
                event_wrapper.set(event);

                final PointF[] new_pt = new PointF[2];
                final float[] move = new float[2];
                for (int i = 0; i < 2; i++) {
                    new_pt[i] = getPointerPosById(event, zoom_ids[i]);
                    move[i] = calcDiffLen(new_pt[i].x, new_pt[i].y, pointers[i].x, pointers[i].y);
                }

                final float before_pinch = calcDiffLen(pointers[0].x, pointers[0].y, pointers[1].x, pointers[1].y);
                final float after_pinch = calcDiffLen(new_pt[0].x, new_pt[0].y, new_pt[1].x, new_pt[1].y);

                final float scale = after_pinch / before_pinch;

                // あまり動かしていない方を偏倍の中心とする
                // int center_index = move[0] > move[1] ? 1 : 0;
                // zoom(false, scale, new_pt[center_index].x,
                // new_pt[center_index].y);
                zoom(false, scale, (new_pt[0].x + new_pt[1].x) / 2, (new_pt[0].y + new_pt[1].y) / 2);

                for (int i = 0; i < 2; i++) {
                    pointers[i] = new_pt[i];
                }
            }

            break;
        case MotionEvent.ACTION_UP:
            break;
        case MotionEvent.ACTION_POINTER_UP:
            event_wrapper.set(event);
            if (event_wrapper.getPointerCount() == 2) {
                touch_state = STATE_NONE;
            } else if (event_wrapper.getPointerCount() == 3) {
                final int i0 = (3 - event_wrapper.getActionIndex()) % 2;
                startZoom(event, new int[] { i0, 3 - event_wrapper.getActionIndex() - i0 });
            }
            break;
        }

        return true;
    }

    public void onZoomIn() {
        onUserAction();
        zoom(false, 1.2f, getWidth() / 2, getHeight() / 2);
    }

    public void onZoomOut() {
        onUserAction();
        zoom(false, 0.8f, getWidth() / 2, getHeight() / 2);
    }

    public void onLoadFinish() {
        onUserAction();
    }

    public boolean onDoubleTap(final MotionEvent e) {
        // 左1/3で前の画像、右1/3で次の画像、真ん中で原寸大or全体表示
        if (e.getX() < getWidth() / 3) {
            if (on_move_image != null) {
                on_move_image.onMoveImage(false);
            }
        } else if (e.getX() > getWidth() / 3 * 2) {
            if (on_move_image != null) {
                on_move_image.onMoveImage(true);
            }
        } else {
            if (getScale() == 1.0f) {
                zoomForWholeImageView();
            } else {
                zoom(true, 1, -getScrollX(), -getScrollY());
            }
        }
        return false;
    }

    void startZoom(final MotionEvent event, final int[] ii) {
        event_wrapper.set(event);
        for (int i = 0; i < 2; i++) {
            zoom_ids[i] = event_wrapper.getPointerId(ii[i]);
            pointers[i].set(event_wrapper.getX(ii[i]), event_wrapper.getY(ii[i]));
        }
        touch_state = STATE_ZOOMING;
    }

    private void zoom(final boolean is_abs, final float scale, final float center_x, final float center_y) {
        final Matrix before_mat = new Matrix(getImageMatrix());
        final Matrix new_mat = new Matrix(before_mat);

        if (is_abs) {
            new_mat.setScale(scale, scale);
        } else {
            new_mat.postScale(scale, scale);
        }
        setImageMatrix(new_mat);

        final float[] pt = new float[] { center_x + getScrollX(), center_y + getScrollY() };
        final Matrix inverse = new Matrix();
        if (before_mat.invert(inverse)) {
            inverse.mapPoints(pt);
        } else {
        }

        // postScaleに偏倍中心を設定してもよいがそれだと画像の左上がスクロール位置の原点でなくなって
        // ちょっとめんどくさくなる
        new_mat.mapPoints(pt);
        scrollBy((int) (pt[0] - (center_x + getScrollX())), (int) (pt[1] - (center_y + getScrollY())));

        footer.setScale(getScale());
    }

    private float getScale() {
        final Matrix mat = new Matrix(getImageMatrix());
        final float[] values = new float[9];
        mat.getValues(values);
        return values[0];
    }

    private int getScrollMinX() {
        return (int) Math.min(getScale() * image_x - getWidth(), 0);
    }

    private int getScrollMinY() {
        return (int) Math.min(getScale() * image_y - getHeight(), 0);
    }

    private int getScrollMaxX() {
        return (int) Math.max(getScale() * image_x - getWidth(), 0);
    }

    private int getScrollMaxY() {
        return (int) Math.max(getScale() * image_y - getHeight(), 0);
    }

    void zoomForWholeImageView() {
        // 画像が画面に収まるようにする
        final float x_scale = (float) getWidth() / image_x;
        final float y_scale = (float) getHeight() / image_y;
        float scale = Math.min(x_scale, y_scale);
        if (scale >= 1.0f) {
            scale = 1.0f;
        }
        zoom(true, scale, 0, 0);
    }

    private void onUserAction() {
        if (footer == null) {
            return;
        }
        footer.setVisibility(View.VISIBLE);

        if (prev_vanish_ani != null) {
            handler.removeCallbacks(prev_vanish_ani);
            footer.clearAnimation();
        }
        if (footer.hasErrorMessage()) {
            return;
        }

        handler.postDelayed(prev_vanish_ani = new Runnable() {
            @Override
            public void run() {
                final AlphaAnimation alpha = new AlphaAnimation(1, 0);
                // 変化時間
                alpha.setDuration(1000);
                alpha.setAnimationListener(new AnimationListener() {

                    @Override
                    public void onAnimationStart(final Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(final Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(final Animation animation) {
                        footer.setVisibility(View.GONE);
                    }
                });
                footer.startAnimation(alpha);

            }
        }, 1000);
    }

}
