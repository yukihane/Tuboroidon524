package info.narazaki.android.lib.activity;

import info.narazaki.android.lib.R;

import java.io.File;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class PickFileActivityBase extends ListActivity implements PickFileViewBase {
    private static final String TAG = "FilePckerBase";

    private IPickFilePresenterBase presenter;

    // ////////////////////////////////////////////////////////////
    // 設定系
    // ////////////////////////////////////////////////////////////
    protected int getLayoutViewID() {
        return R.layout.file_pikcer_base;
    }

    // ////////////////////////////////////////////////////////////
    // ステート管理系
    // ////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutViewID());

        final Bundle bundle = getIntent().getExtras();

        presenter = new PickFilePresenterBase(this);
        presenter.onCreate(savedInstanceState, bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        presenter.onPause();
        super.onPause();
    }

    @Override
    protected void onRestoreInstanceState(final Bundle state) {
        super.onRestoreInstanceState(state);

        presenter.onRestoreInstanceState(state);
    }

    // ////////////////////////////////////////////////////////////
    // 選択
    // ////////////////////////////////////////////////////////////

    @Override
    protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
        presenter.onListItemClick(l, v, position, id);
    }

    // ////////////////////////////////////////////////////////////
    // アダプタ
    // ////////////////////////////////////////////////////////////

    static class FileData {
        final int type_;
        final File file_;

        public FileData(final int type, final File file) {
            type_ = type;
            file_ = file;
        }

        public File getFile() {
            return file_;
        }

        public int getFileType() {
            return type_;
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setPathText(final String path, final float size) {
        final TextView view = (TextView) findViewById(android.R.id.text1);
        view.setText(path);
        view.setTextSize(size);
    }

    @Override
    public void onDirSelected(final Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onFileSelected(final Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
    }
}
