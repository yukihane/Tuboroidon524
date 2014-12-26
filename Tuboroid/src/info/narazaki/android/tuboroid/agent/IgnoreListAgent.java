package info.narazaki.android.tuboroid.agent;

import info.narazaki.android.lib.agent.db.SQLiteAgentBase;
import info.narazaki.android.tuboroid.data.IgnoreData;
import info.narazaki.android.tuboroid.data.ThreadEntryData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import android.database.Cursor;

public class IgnoreListAgent {
    private static final String TAG = "IgnoreListAgent";
    private static final int MAX_NGID_LIST = 30;
    private static final int MAX_NGWORD_LIST = 30;

    private final TuboroidAgentManager agent_manager_;

    public ArrayList<IgnoreData> ngid_list_;
    public HashMap<String, IgnoreData> ngid_map_;
    public ArrayList<IgnoreData> ngword_list_;
    public Pattern ngwords_pattern_;

    public IgnoreListAgent(final TuboroidAgentManager agent_manager) {
        super();
        agent_manager_ = agent_manager;

        ngid_list_ = new ArrayList<IgnoreData>();
        ngid_map_ = new HashMap<String, IgnoreData>();
        ngword_list_ = new ArrayList<IgnoreData>();
        ngwords_pattern_ = null;
        reloadIgnoreList();
    }

    private void reloadIgnoreList() {
        // DBを引いて初期化する
        agent_manager_.getDBAgent().getIgnoreList(new SQLiteAgentBase.DbResultReceiver() {
            @Override
            public void onQuery(final Cursor cursor) {
                final ArrayList<IgnoreData> ngid_list = new ArrayList<IgnoreData>();
                final ArrayList<IgnoreData> ngword_list = new ArrayList<IgnoreData>();

                if (cursor.getCount() != 0) {
                    cursor.moveToFirst();
                    while (true) {
                        final IgnoreData data = new IgnoreData(cursor);
                        if (data.type_ == IgnoreData.TYPE.NGID || data.type_ == IgnoreData.TYPE.NGID_GONE) {
                            ngid_list.add(data);
                        } else if (data.type_ == IgnoreData.TYPE.NGWORD || data.type_ == IgnoreData.TYPE.NGWORD_GONE) {
                            ngword_list.add(data);
                        }
                        if (cursor.moveToNext() == false)
                            break;
                    }
                }
                cursor.close();
                setIgnoreList(ngid_list, ngword_list);
            }

            @Override
            public void onError() {
            }
        });
    }

    private synchronized void setIgnoreList(final ArrayList<IgnoreData> ngid_list, final ArrayList<IgnoreData> ngword_list) {
        ngid_list_ = ngid_list;
        ngword_list_ = ngword_list;
        updateNGSet();
    }

    public synchronized int checkNG(final ThreadEntryData entry_data) {
        final IgnoreData ngid_data = ngid_map_.get(entry_data.getAuthorId());
        if (ngid_data != null)
            return ngid_data.type_;

        if (ngwords_pattern_ != null) {
            if (ngwords_pattern_.matcher(entry_data.getAuthorName()).find()) {
                for (final IgnoreData data : ngword_list_) {
                    if (entry_data.getAuthorName().indexOf(data.token_) != -1) {
                        return data.type_;
                    }
                }
            }
            if (ngwords_pattern_.matcher(entry_data.getEntryBody()).find()) {
                for (final IgnoreData data : ngword_list_) {
                    if (entry_data.getEntryBody().indexOf(data.token_) != -1) {
                        return data.type_;
                    }
                }
            }
        }
        return IgnoreData.TYPE.NONE;
    }

    private void updateNGSet() {
        final HashMap<String, IgnoreData> result_set = new HashMap<String, IgnoreData>();
        for (final IgnoreData data : ngid_list_) {
            result_set.put(data.token_, data);
        }
        ngid_map_ = result_set;

        if (ngword_list_.size() == 0) {
            ngwords_pattern_ = null;
        } else {
            final StringBuilder buf = new StringBuilder();
            for (final IgnoreData data : ngword_list_) {
                if (buf.length() != 0)
                    buf.append("|");
                buf.append(Pattern.quote(data.token_));
            }

            ngwords_pattern_ = Pattern.compile(buf.toString(), Pattern.MULTILINE);
        }
    }

    public synchronized void addNGID(final String author_id, final int type) {
        if (author_id.length() == 0)
            return;
        for (final IgnoreData data : ngid_list_) {
            if (data.token_.equals(author_id))
                return;
        }

        final IgnoreData data = new IgnoreData(type, author_id);
        ngid_list_.add(data);
        agent_manager_.getDBAgent().addIgnoreData(data);

        if (ngid_list_.size() > MAX_NGID_LIST) {
            final IgnoreData old_data = ngid_list_.get(0);
            ngid_list_.remove(0);
            agent_manager_.getDBAgent().deleteIgnoreData(old_data);
        }
        updateNGSet();
    }

    public synchronized void addNGWord(final String word, final int type) {
        if (word.length() == 0)
            return;
        for (final IgnoreData data : ngword_list_) {
            if (data.token_.equals(word))
                return;
        }

        final IgnoreData data = new IgnoreData(type, word);
        ngword_list_.add(data);
        agent_manager_.getDBAgent().addIgnoreData(data);

        if (ngword_list_.size() > MAX_NGWORD_LIST) {
            final IgnoreData old_data = ngword_list_.get(0);
            ngword_list_.remove(0);
            agent_manager_.getDBAgent().deleteIgnoreData(old_data);
        }
        updateNGSet();
    }

    public synchronized void deleteNG(final ThreadEntryData entry_data) {
        deleteNGID(entry_data);
        deleteNGWord(entry_data.getAuthorName());
        deleteNGWord(entry_data.getEntryBody());
        updateNGSet();
    }

    private void deleteNGID(final ThreadEntryData entry_data) {
        final String author_id = entry_data.getAuthorId();
        final IgnoreData data = ngid_map_.get(author_id);
        if (data == null)
            return;

        ngid_list_.remove(data);
        agent_manager_.getDBAgent().deleteIgnoreData(data);
    }

    private void deleteNGWord(final String target) {
        final ArrayList<IgnoreData> found_items = new ArrayList<IgnoreData>();
        for (final IgnoreData data : ngword_list_) {
            if (target.indexOf(data.token_) != -1) {
                found_items.add(data);
                agent_manager_.getDBAgent().deleteIgnoreData(data);
            }
        }
        for (final IgnoreData data : found_items) {
            ngword_list_.remove(data);
        }
    }

    public synchronized void clearNG() {
        ngid_list_.clear();
        ngword_list_.clear();
        agent_manager_.getDBAgent().clearIgnoreData();
        updateNGSet();
    }

}
