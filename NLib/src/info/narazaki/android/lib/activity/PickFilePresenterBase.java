package info.narazaki.android.lib.activity;

public class PickFilePresenterBase implements IPickFilePresenterBase {

    private PickFileViewBase view;

    public PickFilePresenterBase(final PickFileViewBase view) {
        this.view = view;
    }
}
