package info.narazaki.android.tuboroid.activity;

import info.narazaki.android.tuboroid.R;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CreateShortcutActivity extends ListActivity {
    private final int menus[] = new int[] { R.string.title_favorite, R.string.title_recents };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.create_shortcut_dialog);

        final ArrayList<String> values = new ArrayList<String>();
        for (int j = 0; j < menus.length; j++) {
            values.add(getString(menus[j], getString(R.string.app_name)));
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
        createShortcut(menus[position]);
        finish();
    }

    private void createShortcut(final int id) {

        final Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);

        if (id == R.string.title_favorite) {
            shortcutIntent.setClassName(this, FavoriteListActivity.class.getName());
        } else {
            shortcutIntent.setClassName(this, RecentListActivity.class.getName());
        }

        final Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(id, getString(R.string.app_name)));
        final Parcelable iconResource = Intent.ShortcutIconResource.fromContext(this, R.drawable.icon);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
        setResult(RESULT_OK, intent);
    }
}
