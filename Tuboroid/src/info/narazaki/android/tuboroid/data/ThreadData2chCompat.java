package info.narazaki.android.tuboroid.data;

import info.narazaki.android.tuboroid.TuboroidApplication.AccountPref;

import java.util.List;

import android.database.Cursor;
import android.net.Uri;

public class ThreadData2chCompat extends ThreadData2ch {
    private static final String TAG = "ThreadData2chCompat";

    public static boolean is2chCompat(final Uri uri) {
        try {
            final List<String> segments = uri.getPathSegments();
            for (int i = 0; i < segments.size(); i++) {
                if (segments.get(i).equals("test") && segments.get(i + 1).equals("read.cgi")) {
                    final long thread_id = Long.parseLong(segments.get(i + 3));
                    if (thread_id > 0)
                        return true;
                }
            }

        } catch (final IndexOutOfBoundsException e) {
        } catch (final NumberFormatException e) {
        }
        return false;
    }

    /**
     * Copy Constructor
     */
    public ThreadData2chCompat(final ThreadData threadData) {
        super(threadData);
    }

    public ThreadData2chCompat(final BoardData boardData, final int sortOrder, final long threadId, final String threadName, final int onlineCount,
            final int online_speed_x10) {
        super(boardData, sortOrder, threadId, threadName, onlineCount, online_speed_x10);
    }

    public ThreadData2chCompat(final String boardName, final BoardIdentifier server_def, final int sortOrder, final long threadId,
            final String threadName, final int onlineCount, final int online_speed_x10) {
        super(boardName, server_def, sortOrder, threadId, threadName, onlineCount, online_speed_x10);
    }

    public ThreadData2chCompat(final Cursor cursor) {
        super(cursor);
    }

    static public ThreadData factory(final Uri uri) {
        final BoardIdentifier board_server = BoardData2chCompat.createBoardIdentifier(uri);
        if (board_server.board_server_.length() > 0 && board_server.board_tag_.length() > 0) {
            return new ThreadData2chCompat("", board_server, 0, board_server.thread_id_, "", 0, 0);
        }
        return null;
    }

    @Override
    public boolean canRetryWithMaru(final AccountPref account_pref) {
        return false;
    }

    @Override
    public boolean canSpecialPost(final AccountPref account_pref) {
        return account_pref.use_p2_;
    }

    @Override
    public int getJumpEntryNum(final Uri uri) {
        final BoardIdentifier board_server = BoardData2chCompat.createBoardIdentifier(uri);
        return board_server.entry_id_;
    }
}
