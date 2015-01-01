package info.narazaki.android.tuboroid;

import android.app.Activity;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public abstract class FlickDetector {
    // フリックで戻る
    // # もっとマシなやりかたがありそう
    public abstract static class OnFlickListener implements OnGestureListener {
        private boolean handled;
        private final Activity activity;
        private final static int touchSlop = ViewConfiguration.getTouchSlop();

        public OnFlickListener(final Activity a) {
            activity = a;
        }

        public abstract boolean onFlickLeft();

        public abstract boolean onFlickRight();

        @Override
        public boolean onDown(final MotionEvent e) {
            handled = false;
            return false;
        }

        @Override
        public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX, final float velocityY) {
            if (e1 == null || e2 == null)
                return false;
            if (Math.abs(e2.getX() - e1.getX()) > touchSlop) {
                final float x = Math.abs(velocityX);
                final float y = Math.abs(velocityY);
                if (x > y * 3 && x > activity.getResources().getDisplayMetrics().widthPixels) {
                    if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY() - e2.getY())) {
                        handled = (velocityX > 0 ? onFlickRight() : onFlickLeft());
                        if (handled) {
                            e2.setAction(MotionEvent.ACTION_CANCEL);
                        }
                        return handled;
                    }
                }
            }
            return false;
        }

        @Override
        public void onLongPress(final MotionEvent e) {
        }

        @Override
        public boolean onScroll(final MotionEvent e1, final MotionEvent e2, final float distanceX, final float distanceY) {
            return false;
        }

        @Override
        public void onShowPress(final MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(final MotionEvent e) {
            return handled;
        }

    }
}
