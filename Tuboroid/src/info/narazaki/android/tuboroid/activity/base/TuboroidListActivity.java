package info.narazaki.android.tuboroid.activity.base;

import info.narazaki.android.lib.activity.base.NSimpleListActivity;
import info.narazaki.android.lib.system.MigrationSDK5;
import info.narazaki.android.tuboroid.R;
import info.narazaki.android.tuboroid.TuboroidApplication;
import info.narazaki.android.tuboroid.TuboroidApplication.SettingInvalidateChecker;
import info.narazaki.android.tuboroid.TuboroidApplication.ViewConfig;
import info.narazaki.android.tuboroid.activity.ForwardableActivityUtil;
import info.narazaki.android.tuboroid.agent.TuboroidAgent;

import java.lang.reflect.Method;

import jp.syoboi.android.ListViewScrollButton;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

abstract public class TuboroidListActivity extends NSimpleListActivity {
    private static final String TAG = "TuboroidListActivity";

    private SettingInvalidateChecker setting_invalidate_checker_;

    private boolean indeterminate_progress_bar_visible_ = false;
    private boolean progress_bar_visible_ = false;
    protected ListViewScrollButton btnListScroll;

    public TuboroidApplication getTuboroidApplication() {
        return ((TuboroidApplication) getApplication());
    }

    public TuboroidAgent getAgent() {
        return getTuboroidApplication().getAgent();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_PROGRESS);

        if (getTuboroidApplication().isFullScreenMode()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }

        setRequestedOrientation(getTuboroidApplication().getCurrentScreenOrientation());

        getTuboroidApplication().applyTheme(this);
        setting_invalidate_checker_ = getTuboroidApplication().getSettingInvalidateChecker();

        super.onCreate(savedInstanceState);
        indeterminate_progress_bar_visible_ = false;
        progress_bar_visible_ = false;

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(TuboroidApplication.INTENT_KEY_CURRENT_HOME_ACTIVITY_ID)) {
                final int home_activity_id = savedInstanceState
                        .getInt(TuboroidApplication.INTENT_KEY_CURRENT_HOME_ACTIVITY_ID);
                getTuboroidApplication().setHomeTabActivity(home_activity_id);
            }
        }
        ForwardableActivityUtil.onCreate(this);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        btnListScroll = createScrollButton(this, getListView());
        if (btnListScroll != null) {
            btnListScroll.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    setListPageDown();
                }
            });
            TuboroidListActivity.setScrollButtonPosition(btnListScroll,
                    getTuboroidApplication().view_config_.scroll_button_position);
        }
    }

    public static ListViewScrollButton createScrollButton(final Activity activity, final ListView lv) {
        final Context context = activity;
        final ListViewScrollButton btn = new ListViewScrollButton(context, null);
        btn.setFocusable(false);
        btn.setBackgroundResource(R.drawable.ic_btn_scroll);

        final ViewGroup parent = (ViewGroup) lv.getParent();

        final FrameLayout fl = new FrameLayout(context);

        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(lv.getLayoutParams());
        lp.weight = 1f;
        parent.addView(fl, parent.indexOfChild(lv), lp);
        parent.removeView(lv);
        fl.addView(lv, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        fl.addView(btn, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        if (btn != null) {
            btn.setListView(activity, lv);
        }
        return btn;
    }

    @Override
    public void startActivity(final Intent intent) {
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        ForwardableActivityUtil.onActivityResult(this, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        final GestureDetector gd = ForwardableActivityUtil.createFlickGestureDetector(this);

        getListView().setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                return gd.onTouchEvent(event);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TuboroidApplication.INTENT_KEY_CURRENT_HOME_ACTIVITY_ID, getTuboroidApplication()
                .getHomeTabActivityID());
    }

    @Override
    protected void onStart() {
        super.onStart();
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean use_fast_scroll = pref.getBoolean("pref_use_fast_scroll", true);
        final ListView list_view = getListView();
        if (list_view != null) {
            list_view.setFastScrollEnabled(use_fast_scroll);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (setting_invalidate_checker_.isInvalidated()) {
            final Intent intent = new Intent(this, this.getClass());
            MigrationSDK5.Intent_addFlagNoAnimation(intent);
            startActivity(intent);
            finish();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // ランドスケープモードのときはIS01のサイドバーを消す
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            IS01.setFullScreen();
        }

        updateToolbarButtons();
    }

    @Override
    protected void onFirstResume() {
        super.onFirstResume();
        // ツールバーボタンの初期化
        createToolbarButtons();
    }

    public static class IS01 {
        public static void setFullScreen() {
            // http://blog.kcrt.net/2010/08/17/014820
            Method setFullScreenMode;
            try {
                final Class<?> sgManager = Class.forName("jp.co.sharp.android.softguide.SoftGuideManager");
                final Class<?> paramstype[] = { boolean.class };
                setFullScreenMode = sgManager.getMethod("setFullScreenMode", paramstype);
                setFullScreenMode.invoke(null, true);
            } catch (final Exception o) {
                // Log.d("is01fullscreen", "failed" + o.getMessage() + ":" +
                // o.getClass().toString());
            }
        }
    }

    protected void showToolBar(final boolean show) {
        final LinearLayout toolbar = (LinearLayout) findViewById(R.id.toolbar);
        if (toolbar == null)
            return;
        if (show) {
            toolbar.setVisibility(View.VISIBLE);
            getTuboroidApplication().tool_bar_visible_ = true;
        } else {
            toolbar.setVisibility(View.GONE);
            getTuboroidApplication().tool_bar_visible_ = false;
        }
    }

    protected void createToolbarButtons() {
        updateToolbarButtonReload();

        updateToolbarButtonUp();

        updateToolbarButtonDown();

        updateToolbarButtonSwitchFavorite();

        updateToolbarButtons();
    }

    protected void updateToolbarButtons() {
        updateToolbarButtonHome();
        onFavoriteUpdated();
    }

    protected void updateToolbarButtonHome() {
        final ImageButton button_home = (ImageButton) findViewById(R.id.button_toolbar_home);
        if (button_home != null) {
            button_home.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    getTuboroidApplication().jumpHomeTabActivity(TuboroidListActivity.this);
                }
            });
            button_home.setImageResource(getTuboroidApplication().getHomeTabActivityIcon());
        }
    }

    protected void updateToolbarButtonReload() {
        final ImageButton button_reload = (ImageButton) findViewById(R.id.button_toolbar_reload);
        if (button_reload != null)
            button_reload.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (hasInitialData())
                        reloadList(true);
                }
            });
    }

    protected void updateToolbarButtonUp() {
        final ImageButton button_up = (ImageButton) findViewById(R.id.button_toolbar_up);
        if (button_up != null) {
            button_up.setImageResource(R.drawable.toolbar_btn_jump_top);
            button_up.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (hasInitialData())
                        onToolbarButtonUpClicked();
                }
            });
        }
    }

    protected void onToolbarButtonUpClicked() {
        setListPositionTop(null);
    }

    protected void updateToolbarButtonDown() {
        final ImageButton button_down = (ImageButton) findViewById(R.id.button_toolbar_down);
        if (button_down != null) {
            button_down.setImageResource(R.drawable.toolbar_btn_jump_bottom);
            button_down.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (hasInitialData())
                        onToolbarButtonDownClicked();
                }
            });
        }
    }

    protected void onToolbarButtonDownClicked() {
        setListPositionBottom(null);
    }

    protected void updateToolbarButtonSwitchFavorite() {
        final ImageButton button_favorite = (ImageButton) findViewById(R.id.button_toolbar_favorite);
        if (button_favorite != null)
            button_favorite.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    setFavorite(!isFavorite());
                }
            });
    }

    protected void onFavoriteUpdated() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final ImageButton button_favorite = (ImageButton) findViewById(R.id.button_toolbar_favorite);
                if (button_favorite != null) {
                    if (isFavorite()) {
                        button_favorite.setImageResource(R.drawable.toolbar_btn_favorite_on);
                    } else {
                        button_favorite.setImageResource(R.drawable.toolbar_btn_favorite_off);
                    }
                }
            }
        });
    }

    protected void setFavorite(final boolean is_favorite) {
        if (!getIsActive())
            return;
        if (isFavorite() != is_favorite) {
            if (is_favorite) {
                addFavorite();
            } else {
                deleteFavorite();
            }
        }
    }

    protected boolean isFavorite() {
        return false;
    }

    protected void addFavorite() {
    }

    protected void deleteFavorite() {
    }

    protected void showIndeterminateProgressBar(final boolean visible) {
        if (indeterminate_progress_bar_visible_ == visible)
            return;
        indeterminate_progress_bar_visible_ = visible;
        setProgressBarIndeterminateVisibility(visible);
    }

    protected void showProgressBar(final boolean visible) {
        if (progress_bar_visible_ == visible)
            return;
        progress_bar_visible_ = visible;
        if (visible) {
            setProgress(0);
            setSecondaryProgress(0);
        } else {
            setProgress(10000);
            setSecondaryProgress(10000);
        }
        setProgressBarVisibility(visible);
    }

    protected void setProgressBar(final int value, final int max) {
        if (!progress_bar_visible_)
            return;
        if (value >= max || max == 0) {
            setProgress(9999);
        } else {
            setProgress(value * 9999 / max);
        }
    }

    protected void setSecondaryProgressBar(final int value, final int max) {
        if (!progress_bar_visible_)
            return;
        if (value >= max || max == 0) {
            setSecondaryProgress(9999);
        } else {
            setSecondaryProgress(value * 9999 / max);
        }
    }

    protected void reloadList(final boolean force_reload) {
    }

    public static void setScrollButtonPosition(final ListViewScrollButton sb, final int button_position) {
        final Resources res = sb.getResources();

        if (sb != null) {
            final FrameLayout.LayoutParams lp = (LayoutParams) sb.getLayoutParams();
            lp.bottomMargin = res.getDimensionPixelSize(R.dimen.scrollButtonMargin);
            lp.leftMargin = lp.rightMargin = lp.bottomMargin;
            boolean visibility = true;
            boolean reverse = true;
            switch (button_position) {
            case ViewConfig.SCROLL_BUTTON_CENTER:
                lp.bottomMargin = res.getDimensionPixelSize(R.dimen.scrollButtonBottomMargin);
                lp.gravity = Gravity.CENTER | Gravity.BOTTOM;
                reverse = false;
                break;
            case ViewConfig.SCROLL_BUTTON_BOTTOM:
                lp.gravity = Gravity.CENTER | Gravity.BOTTOM;
                break;
            case ViewConfig.SCROLL_BUTTON_LB:
                lp.gravity = Gravity.LEFT | Gravity.BOTTOM;
                break;
            case ViewConfig.SCROLL_BUTTON_RB:
                lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
                break;
            case ViewConfig.SCROLL_BUTTON_NONE:
                visibility = false;
                break;
            }
            sb.setReverse(reverse);
            sb.setLayoutParams(lp);
            sb.setVisibility(visibility ? View.VISIBLE : View.GONE);
        }
    }

}
