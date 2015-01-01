package info.narazaki.android.tuboroid.activity;

import static info.narazaki.android.lib.pick_file.model.Constants.INTENT_KEY_ALLOW_NEW_DIR;
import static info.narazaki.android.lib.pick_file.model.Constants.INTENT_KEY_CHECK_WRITABLE;
import static info.narazaki.android.lib.pick_file.model.Constants.INTENT_KEY_CURRENT;
import static info.narazaki.android.lib.pick_file.model.Constants.INTENT_KEY_FONT_SIZE;
import static info.narazaki.android.lib.pick_file.model.Constants.INTENT_KEY_PICK_DIRECTORY;
import static info.narazaki.android.lib.pick_file.model.Constants.INTENT_KEY_ROOT;
import static info.narazaki.android.lib.pick_file.model.Constants.INTENT_KEY_TITLE;
import info.narazaki.android.lib.activity.base.NPreferenceActivity;
import info.narazaki.android.lib.dialog.SimpleDialog;
import info.narazaki.android.lib.system.MigrationSDK4;
import info.narazaki.android.lib.system.MigrationSDK5;
import info.narazaki.android.lib.toast.ManagedToast;
import info.narazaki.android.tuboroid.R;
import info.narazaki.android.tuboroid.TuboroidApplication;
import info.narazaki.android.tuboroid.agent.TuboroidAgent;

import java.io.File;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.WindowManager;

public class SettingsActivity extends NPreferenceActivity {
    public static final int INTENT_RESULT_EXT_STORAGE = 1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        if (getTuboroidApplication().isFullScreenMode()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }

        setRequestedOrientation(getTuboroidApplication().getCurrentScreenOrientation());

        getTuboroidApplication().applyTheme(this);

        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.setting);

        // SDカードの有無
        final CheckBoxPreference use_external = (CheckBoxPreference) findPreference("pref_use_external_storage");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            use_external.setEnabled(true);
        } else {
            use_external.setEnabled(false);
        }

        final Preference pref_external_storage_path = findPreference("pref_external_storage_path");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            pref_external_storage_path.setEnabled(true);
            pref_external_storage_path.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    onPrefExternalStoragePathClicked();
                    return true;
                }
            });
        } else {
            pref_external_storage_path.setEnabled(false);
        }

        final Preference manage_clear_ignores = findPreference("manage_clear_ignores");
        manage_clear_ignores.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                SimpleDialog.showYesNo(SettingsActivity.this, R.string.dialog_clear_ignores_title,
                        R.string.dialog_clear_ignores, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                getAgent().clearNG();
                                ManagedToast.raiseToast(SettingsActivity.this, R.string.toast_clear_ignores);
                            }
                        }, new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(final DialogInterface dialog) {
                            }
                        });
                return true;
            }
        });

        final Preference manage_clear_cookie = findPreference("manage_clear_cookie");
        manage_clear_cookie.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                SimpleDialog.showYesNo(SettingsActivity.this, R.string.dialog_clear_cookie_title,
                        R.string.dialog_clear_cookie, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                getAgent().clearCookie();
                                ManagedToast.raiseToast(SettingsActivity.this, R.string.toast_clear_cookie);
                            }
                        }, new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(final DialogInterface dialog) {
                            }
                        });
                return true;
            }
        });

        final Preference pref_external_aa_font = findPreference("pref_external_aa_font");
        if (MigrationSDK4.supported()) {
            pref_external_aa_font.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    onExternalAAFontPrefClicked();
                    return true;
                }
            });
        } else {
            pref_external_aa_font.setEnabled(false);
            pref_external_aa_font.setSummary(R.string.pref_summary_use_external_storage_not_available);
        }

    }

    @Override
    protected void onPause() {
        getTuboroidApplication().reloadPreferences(true);
        super.onPause();
    }

    protected TuboroidApplication getTuboroidApplication() {
        return ((TuboroidApplication) getApplication());
    }

    public TuboroidAgent getAgent() {
        return getTuboroidApplication().getAgent();
    }

    private void onExternalAAFontPrefClicked() {
        final Intent intent = new Intent(this, SettingAAFontActivity.class);
        MigrationSDK5.Intent_addFlagNoAnimation(intent);
        startActivity(intent);
    }

    private void onPrefExternalStoragePathClicked() {
        final File ext_storage_base = Environment.getExternalStorageDirectory();
        final File ext_storage = new File(ext_storage_base,
                TuboroidApplication.getExternalStoragePathName(getApplicationContext()));
        ext_storage.mkdirs();
        final String path = ext_storage.getAbsolutePath();

        final Intent intent = new Intent(this, PickFileActivity.class);
        intent.putExtra(INTENT_KEY_ALLOW_NEW_DIR, true);
        intent.putExtra(INTENT_KEY_CHECK_WRITABLE, true);
        intent.putExtra(INTENT_KEY_ROOT, ext_storage_base.getAbsolutePath());
        intent.putExtra(INTENT_KEY_CURRENT, path);
        intent.putExtra(INTENT_KEY_FONT_SIZE, getTuboroidApplication().view_config_.entry_body_ * 3 / 2);
        intent.putExtra(INTENT_KEY_TITLE, getString(R.string.pref_title_external_storage_path));
        intent.putExtra(INTENT_KEY_PICK_DIRECTORY, true);

        MigrationSDK5.Intent_addFlagNoAnimation(intent);
        startActivityForResult(intent, INTENT_RESULT_EXT_STORAGE);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == INTENT_RESULT_EXT_STORAGE) {
            if (resultCode == RESULT_OK) {
                final Uri uri = data.getData();
                if (uri == null)
                    return;
                final String target_path = uri.getPath();
                if (target_path == null)
                    return;
                final File ext_storage = Environment.getExternalStorageDirectory();
                final String local_path = target_path.substring(ext_storage.getAbsolutePath().length());

                final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
                final Editor editor = pref.edit();
                editor.putString("pref_external_storage_path", local_path);
                editor.commit();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
