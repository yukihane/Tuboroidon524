package info.narazaki.android.lib.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;

public class SimpleEditTextDialog {

    public static interface OnSubmitListener {
        public void onSubmitted(String data);

        public void onCanceled();
    }

    public static void show(final Activity activity, final String default_data, final int title_id, final int submit_id,
            final OnSubmitListener listener) {
        // ビュー作成
        final EditText edit_text = new EditText(activity);
        edit_text.setText(default_data);

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title_id);
        builder.setView(edit_text);
        builder.setPositiveButton(submit_id, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int whichButton) {
                listener.onSubmitted(edit_text.getText().toString());
            }
        });
        builder.setCancelable(true);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                listener.onCanceled();
            }
        });
        builder.create().show();
    }
}
