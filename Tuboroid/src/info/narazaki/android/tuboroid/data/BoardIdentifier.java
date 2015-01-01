package info.narazaki.android.tuboroid.data;

public class BoardIdentifier {
    public final String board_server_;
    public final String board_tag_;
    public final long thread_id_;
    public final int entry_id_;

    public BoardIdentifier(final String board_server, final String board_tag, final long thread_id, final int entry_id) {
        super();
        board_server_ = board_server;
        board_tag_ = board_tag;
        thread_id_ = thread_id;
        entry_id_ = entry_id;
    }

    public String getBoardServerBareName() {
        if (!board_server_.contains("@")) {
            return board_server_.substring(board_server_.indexOf("@") + 1);
        }
        return board_server_;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof BoardIdentifier) {
            final BoardIdentifier target = (BoardIdentifier) o;
            if (isSameBoard(target) && thread_id_ == target.thread_id_ && entry_id_ == target.entry_id_) {
                return true;
            }
            return false;
        }
        return super.equals(o);
    }

    public boolean isSameBoard(final BoardIdentifier target) {
        if (board_server_.equals(target.board_server_) && board_tag_.equals(target.board_tag_)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return board_server_.hashCode() + board_tag_.hashCode();
    }

    @Override
    public String toString() {
        return board_server_ + ":" + board_tag_;
    }

}
