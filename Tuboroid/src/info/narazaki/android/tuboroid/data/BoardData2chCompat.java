package info.narazaki.android.tuboroid.data;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.net.Uri;
import android.text.TextUtils;

public class BoardData2chCompat extends BoardData2ch {
    static private final String CATEGORY_NAME = "2ch互換";

    public static boolean is2chCompat(final String board_server) {
        return false;
    }

    @Override
    public int getSortOrder() {
        return 10000;
    }

    protected BoardData2chCompat(final BoardData2chCompat boardData) {
        super(boardData);
        board_category_ = CATEGORY_NAME;
    }

    protected BoardData2chCompat(final long id, final boolean isFavorite, final boolean is_external, final String boardName,
            final BoardIdentifier server_def) {
        super(id, isFavorite, is_external, boardName, server_def);
        board_category_ = CATEGORY_NAME;
    }

    protected BoardData2chCompat(final long orderId, final String boardName, final String boardCategory, final BoardIdentifier server_def) {
        super(orderId, boardName, CATEGORY_NAME, server_def);
    }

    static public BoardIdentifier createBoardIdentifier(final Uri uri) {
        final LinkedList<String> board_server_list = new LinkedList<String>();
        String board_tag = null;
        long thread_id = 0;
        int entry_id = 0;
        try {
            // String host_name = uri.getHost();
            // if (uri.getUserInfo() != null) host_name = uri.getUserInfo() +
            // "@" + host_name;
            final String host_name = uri.getEncodedAuthority();
            board_server_list.add(host_name);
            final List<String> segments = uri.getPathSegments();
            for (int i = 0; i < segments.size(); i++) {
                final String token = segments.get(i);
                if (token.equals("test") && segments.get(i + 1).equals("read.cgi")) {
                    board_tag = segments.get(i + 2);
                    // スレは .../test/read.cgi/[板タグ]/[スレID]/[レス番指定] になる
                    if (segments.size() > i + 3 && segments.get(i + 3).length() > 0) {
                        thread_id = info.narazaki.android.lib.text.TextUtils.parseLong(segments.get(i + 3));
                    }
                    if (segments.size() > i + 4 && segments.get(i + 4).length() > 0) {
                        // l : 指定件数の最新レスを表示する（レス１も表示）→ めんどくさいのでデフォルト表示
                        // n : １を除外する → 無視して良い
                        final String opt = segments.get(i + 4);
                        final Pattern pattern = Pattern.compile("^([n|l]*)(\\d+)?(-)?(\\d+)?$");
                        final Matcher matcher = pattern.matcher(opt);
                        if (matcher.find() && matcher.group(1) != null && matcher.group(1).indexOf('l') == -1) {
                            final String target = matcher.group(2);
                            if (target != null && target.length() > 0) {
                                entry_id = info.narazaki.android.lib.text.TextUtils.parseInt(target);
                            }
                        }
                    }
                    break;
                }
                if (token.equals("subject.txt")) {
                    board_tag = board_server_list.getLast();
                    board_server_list.removeLast();
                    break;
                }
                board_server_list.add(token);
            }
            if (board_tag == null) {
                board_tag = board_server_list.getLast();
                board_server_list.removeLast();
            }
            return new BoardIdentifier(TextUtils.join("/", board_server_list), board_tag, thread_id, entry_id);
        } catch (final IndexOutOfBoundsException e) {
        }
        return new BoardIdentifier("", "", 0, 0);
    }
}
