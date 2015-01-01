package info.narazaki.android.tuboroid.dialog;

import info.narazaki.android.tuboroid.R;
import info.narazaki.android.tuboroid.TuboroidApplication;
import info.narazaki.android.tuboroid.TuboroidApplication.ViewConfig;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

public class ThreadEntryListConfigDialog extends Dialog {

    OnChangedListener changedListener;

    public ThreadEntryListConfigDialog(final Context context, final OnCancelListener cancelListener,
            final OnChangedListener changedListener) {
        super(context, true, cancelListener);
        this.changedListener = changedListener;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thread_entry_config_dialog);

        final TuboroidApplication app = (TuboroidApplication) getContext().getApplicationContext();
        final ViewConfig view_config = new ViewConfig(app.view_config_);

        setTitle(R.string.dialog_thread_entry_config_title);

        final Resources res = getContext().getResources();

        // スクロールボタンの位置の設定
        final int[] scrollButtonPosValues = res.getIntArray(R.array.scroll_button_position_value);
        final Spinner scrollButtonPos = (Spinner) findViewById(R.id.scroll_button_position);
        int idx = 0;
        for (int j = 0; j < scrollButtonPosValues.length; j++) {
            if (scrollButtonPosValues[j] == view_config.scroll_button_position) {
                idx = j;
                break;
            }
        }
        scrollButtonPos.setSelection(idx);
        scrollButtonPos.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> arg0, final View arg1, final int position, final long id) {
                view_config.scroll_button_position = scrollButtonPosValues[position];
                changedListener.onChanged(view_config);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> arg0) {
            }
        });

        final TextView bodySizeText = (TextView) findViewById(R.id.body_size_text);
        final TextView headerSizeText = (TextView) findViewById(R.id.header_size_text);
        final TextView aaSizeText = (TextView) findViewById(R.id.aa_size_text);
        final String sizeFormat = res.getString(R.string.dialog_thread_entry_config_size_format);

        // ボタンが押されたときに設定を変える
        final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (v != null) {
                    final int fontSizeMin = 4;

                    final int id = v.getId();
                    if (id == R.id.body_inc) {
                        view_config.entry_body_++;
                    } else if (id == R.id.body_dec) {
                        if (view_config.entry_body_ > fontSizeMin)
                            view_config.entry_body_--;
                    } else if (id == R.id.header_inc) {
                        view_config.entry_header_++;
                    } else if (id == R.id.header_dec) {
                        if (view_config.entry_header_ > fontSizeMin)
                            view_config.entry_header_--;
                    } else if (id == R.id.aa_inc) {
                        view_config.entry_aa_body_++;
                    } else if (id == R.id.aa_dec) {
                        if (view_config.entry_aa_body_ > fontSizeMin)
                            view_config.entry_aa_body_--;
                    } else if (id == R.id.entry_divider) {
                        view_config.entry_divider = ((CheckBox) v).isChecked() ? 1 : 0;
                    }

                }
                bodySizeText.setText(String.format(sizeFormat, view_config.entry_body_));
                headerSizeText.setText(String.format(sizeFormat, view_config.entry_header_));
                aaSizeText.setText(String.format(sizeFormat, view_config.entry_aa_body_));

                changedListener.onChanged(view_config);
            }
        };
        onClickListener.onClick(null);

        final int[] btns = new int[] { R.id.body_inc, R.id.body_dec, R.id.header_inc, R.id.header_dec, R.id.aa_inc,
                R.id.aa_dec, R.id.entry_divider, };
        for (final int id : btns) {
            findViewById(id).setOnClickListener(onClickListener);
        }

        // レス間の区切り
        final CheckBox entryDivider = (CheckBox) findViewById(R.id.entry_divider);
        entryDivider.setChecked(view_config.entry_divider != 0);

        // AA モード
        final Spinner aaMode = (Spinner) findViewById(R.id.aa_mode);
        // final int [] aaModeValues = res.getIntArray(R.array.aa_mode);

        aaMode.setSelection(view_config.aa_mode);
        aaMode.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> arg0, final View arg1, final int position, final long id) {
                view_config.aa_mode = position;
                changedListener.onChanged(view_config);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> arg0) {
            }
        });

        // ダイアログが閉じられるときに設定を保存
        setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                // int bodySize =
                // Integer.parseInt(pref.getString("pref_font_size_entry_body",
                // "13"));
                final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(app);
                final Editor editor = pref.edit();
                editor.putString("pref_font_size_entry_body", String.valueOf(view_config.entry_body_));
                editor.putString("pref_font_size_entry_header", String.valueOf(view_config.entry_header_));
                editor.putString("pref_font_size_entry_aa_body", String.valueOf(view_config.entry_aa_body_));
                editor.putInt(ViewConfig.PREF_ENTRY_DIVIDER, view_config.entry_divider);
                editor.putInt(ViewConfig.PREF_SCROLL_BUTTON_POSITION, view_config.scroll_button_position);
                editor.putInt(ViewConfig.PREF_AA_MODE, view_config.aa_mode);
                editor.commit();
                app.reloadPreferences(true);
            }
        });

        entryDivider.post(new Runnable() {
            @Override
            public void run() {
                final Window w = getWindow();
                final WindowManager.LayoutParams lp = w.getAttributes();
                lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                w.setAttributes(lp);
            }
        });
    }

    public static interface OnChangedListener {
        public void onChanged(ViewConfig config);
    }
}
