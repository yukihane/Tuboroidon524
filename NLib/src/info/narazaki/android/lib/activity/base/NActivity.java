package info.narazaki.android.lib.activity.base;

import info.narazaki.android.lib.aplication.NApplication;
import android.app.Activity;
import android.os.Bundle;

public class NActivity extends Activity {
    private boolean on_first_shown_ = true;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        on_first_shown_ = true;
        super.onCreate(savedInstanceState);
    }

    protected NApplication getNApplication() {
        return (NApplication) getApplication();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getNApplication().onActivityResume(this);
        if (on_first_shown_) {
            onFirstResume();
            on_first_shown_ = false;
        } else {
            onSecondResume();
        }
    }

    protected void onFirstResume() {
    }

    protected void onSecondResume() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getNApplication().onActivityDestroy(this);
    }
}
