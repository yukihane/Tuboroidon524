package jp.syoboi.android;

import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ListView;

public class ListViewScroller {

    public static final String TAG = ListViewScroller.class.getSimpleName();

    public static final int ANCHOR_CENTER = 0;
    public static final int ANCHOR_TOP = 1;
    public static final int ANCHOR_BOTTOM = 2;
    private MyScroller scroller;

    private final AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();

    public void cancel() {
        if (scroller != null) {
            scroller.cancel();
            scroller = null;
        }
    }

    public boolean isScrolling() {
        return scroller != null;
    }

    public boolean scroll(final ListView list, final float page, final int millis) {
        cancel();

        final int childCount = list.getChildCount();
        if (childCount == 0)
            return false;

        int position = list.getFirstVisiblePosition();

        int from = list.getChildAt(0).getTop();
        int to = from;
        final int range = (int) (list.getMeasuredHeight() * page);
        if (page > 0) {
            for (int j = 0; j < childCount; j++) {
                View child = list.getChildAt(j);
                if (range <= child.getBottom()) {
                    if (j + 1 < childCount) {
                        child = list.getChildAt(j + 1);
                        j++;
                    }
                    position += j;
                    from = child.getTop();
                    to = child.getTop() - range;
                    break;
                }
            }
        } else {
            to = from - range;
        }

        if (from != to) {
            if (millis == 0) {
                list.setSelectionFromTop(position, to);
            } else {
                scroller = new MyScroller(millis, 1000 / 60, list, position, from, to);
                scroller.start();
            }
            return true;
        }
        return false;
    }

    public boolean scrollMargin(final ListView list, final int position, final int millis, final int fps, final int margin) {
        if (scroller != null)
            scroller.cancel();

        final int idx = position - list.getFirstVisiblePosition();
        if (idx < list.getChildCount()) {
            final View v = list.getChildAt(idx);
            final int from = v.getTop();
            final int listHeight = list.getMeasuredHeight();

            int to = from;

            if (from < margin) {
                to = margin;
            } else if (v.getBottom() > listHeight - margin) {
                to = listHeight - margin - v.getHeight();
            }
            if (from != to) {
                if (millis == 0) {
                    list.setSelectionFromTop(position, to);
                } else {
                    scroller = new MyScroller(millis, 1000 / fps, list, position, from, to);
                    scroller.start();
                }
                return true;
            }
        }
        return false;
    }

    private class MyScroller extends CountDownTimer {
        private final ListView list;
        private final int position;
        private final int from;
        private final int to;
        private final long millis;

        public MyScroller(final long millisInFuture, final long countDownInterval, final ListView lv, final int position, final int from, final int to) {
            super(millisInFuture, countDownInterval);
            this.list = lv;
            this.position = position;
            this.from = from;
            this.to = to;
            this.millis = millisInFuture;
        }

        @Override
        public void onFinish() {
            list.setSelectionFromTop(position, to);
            scroller = null;
        }

        @Override
        public void onTick(final long millisUntilFinished) {
            final int y = from
                    + (int) ((to - from) * interpolator.getInterpolation((float) (this.millis - millisUntilFinished)
                            / this.millis));
            // Log.d(TAG, "position:"+position+" from:"+from+" to:"+to+" y:"+y);
            list.setSelectionFromTop(position, y);
        }
    }
}
