package info.narazaki.android.tuboroid.contents.thread_entry_list.presenter;

import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;

public interface ThreadEntryListView {

    /**
     * @param menu
     *            メニューインスタンス.
     * @return 「書き込み」メニューアイテム.
     */
    MenuItem getMenuItemCompose(Menu menu);

    /**
     * 書き込み編集画面を表示します.
     * 
     * @param threadUri
     *            書き込み対象スレッドのURL.
     */
    void showEntryEditor(Uri threadUri);

}
