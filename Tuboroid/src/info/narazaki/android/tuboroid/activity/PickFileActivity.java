package info.narazaki.android.tuboroid.activity;

import info.narazaki.android.tuboroid.TuboroidApplication;
import info.narazaki.android.tuboroid.TuboroidApplication.SettingInvalidateChecker;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class PickFileActivity extends info.narazaki.android.lib.pick_file.view.PickFileActivity {

    private SettingInvalidateChecker setting_invalidate_checker_;

    protected TuboroidApplication getTuboroidApplication() {
        return ((TuboroidApplication) getApplication());
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_PROGRESS);

        if (getTuboroidApplication().isFullScreenMode()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setRequestedOrientation(getTuboroidApplication().getCurrentScreenOrientation());

        getTuboroidApplication().applyTheme(this);
        setting_invalidate_checker_ = getTuboroidApplication().getSettingInvalidateChecker();

        super.onCreate(savedInstanceState);

        // FIXME 最終的に, Configのフィールド値を変更すればよい
        // setLightTheme(getTuboroidApplication().isLightTheme());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (setting_invalidate_checker_.isInvalidated()) {
            finish();
            return;
        }
    }
}
