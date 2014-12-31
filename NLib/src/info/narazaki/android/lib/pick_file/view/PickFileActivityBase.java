package info.narazaki.android.lib.pick_file.view;

import info.narazaki.android.lib.R;
import info.narazaki.android.lib.pick_file.presenter.IPickFilePresenterBase;
import info.narazaki.android.lib.pick_file.presenter.PickFilePresenterBase;
import info.narazaki.android.lib.pick_file.presenter.PickFileViewBase;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class PickFileActivityBase extends ListActivity implements PickFileViewBase {

    private IPickFilePresenterBase presenter;

    /**
     * @return 本Activityの content view id.
     */
    protected int getLayoutViewID() {
        return R.layout.file_pikcer_base;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutViewID());

        final Bundle bundle = getIntent().getExtras();

        presenter = new PickFilePresenterBase(this, this);
        presenter.initialize(savedInstanceState, bundle);
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

    @Override
    protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
        presenter.onListItemClick(l, v, position, id);
    }

    @Override
    public void setPathText(final String path, final float size) {
        final TextView view = (TextView) findViewById(android.R.id.text1);
        view.setText(path);
        view.setTextSize(size);
    }

    @Override
    public void finishSuccessfully(final Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
    }
}
