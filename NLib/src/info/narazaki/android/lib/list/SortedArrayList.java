package info.narazaki.android.lib.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class SortedArrayList<E> extends ArrayList<E> {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Comparator<? super E> comparer_;

    public SortedArrayList() {
        super();
        comparer_ = null;
    }

    public SortedArrayList(final Collection<? extends E> collection) {
        super(collection);
        comparer_ = null;
    }

    public SortedArrayList(final int capacity) {
        super(capacity);
        comparer_ = null;
    }

    public SortedArrayList(final Comparator<? super E> comparer) {
        super();
        comparer_ = comparer;
    }

    public SortedArrayList(final Collection<? extends E> collection, final Comparator<? super E> comparer) {
        super(collection);
        comparer_ = comparer;
        applySort();
    }

    public void setComparator(final Comparator<? super E> comparer) {
        comparer_ = comparer;
        applySort();
    }

    private void applySort() {
        if (comparer_ != null)
            Collections.sort(this, comparer_);
    }

    @Override
    public boolean add(final E object) {
        if (comparer_ == null) {
            return super.add(object);
        }
        final int index = Collections.binarySearch(this, object, comparer_);

        if (index >= 0) {
            super.add(index + 1, object);
        } else {
            super.add(-index - 1, object);
        }
        return true;
    }

    @Override
    public boolean addAll(final Collection<? extends E> collection) {
        super.addAll(collection);
        applySort();
        return true;
    }

}
