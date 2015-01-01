package info.narazaki.android.tuboroid.data;

import info.narazaki.android.tuboroid.TuboroidApplication.AccountPref;
import info.narazaki.android.tuboroid.agent.PostEntryTask;
import info.narazaki.android.tuboroid.agent.PostEntryTaskShitaraba;
import info.narazaki.android.tuboroid.agent.ThreadEntryListTask;
import info.narazaki.android.tuboroid.agent.ThreadEntryListTaskShitaraba;
import info.narazaki.android.tuboroid.agent.TuboroidAgentManager;
import info.narazaki.android.tuboroid.agent.task.HttpGetThreadEntryListTask;
import info.narazaki.android.tuboroid.agent.task.HttpGetThreadEntryListTaskShitaraba;

import java.util.List;

import android.database.Cursor;
import android.net.Uri;

public class ThreadDataShitaraba extends ThreadData {
    private static final String TAG = "ThreadDataShitaraba";

    private String thread_uri_ = null;

    public static boolean isShitaraba(final Uri uri) {
        final String board_server = uri.getHost();
        if (!BoardDataShitaraba.isShitaraba(board_server))
            return false;
        try {
            final List<String> segments = uri.getPathSegments();
            if (segments.size() >= 4 && segments.get(0).equals("bbs") && segments.get(1).equals("read.cgi")) {
                return true;
            }

            // 過去ログ書庫
            if (segments.size() >= 4 && segments.get(2).equals("storage") && segments.get(3).endsWith(".html")) {
                return true;
            }
        } catch (final IndexOutOfBoundsException e) {
        } catch (final NumberFormatException e) {
        }
        return false;
    }

    /**
     * Copy Constructor
     */
    public ThreadDataShitaraba(final ThreadData threadData) {
        super(threadData);
    }

    @Override
    public ThreadData clone() {
        return new ThreadDataShitaraba(this);
    }

    public ThreadDataShitaraba(final BoardData boardData, final int sortOrder, final long threadId, final String threadName, final int onlineCount,
            final int online_speed_x10) {
        super(boardData, sortOrder, threadId, threadName, onlineCount, online_speed_x10);
    }

    public ThreadDataShitaraba(final String boardName, final BoardIdentifier server_def, final int sortOrder, final long threadId,
            final String threadName, final int onlineCount, final int online_speed_x10) {
        super(boardName, server_def, sortOrder, threadId, threadName, onlineCount, online_speed_x10);
    }

    public ThreadDataShitaraba(final Cursor cursor) {
        super(cursor);
    }

    @Override
    public HttpGetThreadEntryListTask factoryGetThreadHttpGetThreadEntryListTask(final String session_key,
            final HttpGetThreadEntryListTask.Callback callback) {
        return new HttpGetThreadEntryListTaskShitaraba(this, null, callback);
    }

    @Override
    public PostEntryTask factoryPostEntryTask(final TuboroidAgentManager agent_manager) {
        return new PostEntryTaskShitaraba(agent_manager);
    }

    @Override
    public ThreadEntryListTask factoryThreadEntryListTask(final TuboroidAgentManager agent_manager) {
        return new ThreadEntryListTaskShitaraba(agent_manager);
    }

    @Override
    public synchronized String getThreadURI() {
        if (thread_uri_ == null) {
            thread_uri_ = "http://" + server_def_.board_server_ + "/bbs/read.cgi/" + server_def_.board_tag_ + "/"
                    + thread_id_ + "/";
        }
        return thread_uri_;
    }

    @Override
    public String getDatFileURI() {
        return "http://" + server_def_.board_server_ + "/bbs/rawmode.cgi/" + server_def_.board_tag_ + "/" + thread_id_
                + "/";
    }

    @Override
    public String getSpecialDatFileURI(final String session_key) {
        return "http://" + server_def_.board_server_ + "/" + server_def_.board_tag_ + "/storage/" + thread_id_
                + ".html";
    }

    @Override
    public String getBoardSubjectsURI() {
        return "http://" + server_def_.board_server_ + "/" + server_def_.board_tag_ + "/subject.txt";
    }

    @Override
    public String getBoardIndexURI() {
        return "http://" + server_def_.board_server_ + "/" + server_def_.board_tag_ + "/";
    }

    @Override
    public String getPostEntryURI() {
        return "http://" + server_def_.board_server_ + "/bbs/write.cgi";
    }

    @Override
    public String getPostEntryRefererURI() {
        return getThreadURI();
    }

    @Override
    public boolean isFilled() {
        return is_dropped_;
    }

    @Override
    public boolean canRetryWithoutMaru() {
        return true;
    }

    @Override
    public boolean canRetryWithMaru(final AccountPref account_pref) {
        return false;
    }

    @Override
    public boolean canSpecialPost(final AccountPref account_pref) {
        return false;
    }

    static public ThreadData factory(final Uri uri) {
        final BoardIdentifier board_server = BoardDataShitaraba.createBoardIdentifier(uri);
        if (board_server.board_server_.length() > 0 && board_server.board_tag_.length() > 0) {
            return new ThreadDataShitaraba("", board_server, 0, board_server.thread_id_, "", 0, 0);
        }
        return null;
    }

    @Override
    public int getJumpEntryNum(final Uri uri) {
        final BoardIdentifier board_server = BoardDataShitaraba.createBoardIdentifier(uri);
        return board_server.entry_id_;
    }
}
