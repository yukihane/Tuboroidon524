package info.narazaki.android.lib.memory;

public class SimpleFloatArrayPool {
    float[][] pool_;

    public SimpleFloatArrayPool(final int max_pool) {
        pool_ = new float[max_pool][];
    }

    public synchronized float[] obtain(final int size) {
        int min_index = -1;
        int min_length = Integer.MAX_VALUE;
        for (int i = 0; i < pool_.length; i++) {
            if (pool_[i] != null) {
                final int len = pool_[i].length;
                if (len > size && (min_length > len)) {
                    min_index = i;
                    min_length = len;
                }
            }
        }

        if (min_index != -1) {
            final float[] result = pool_[min_index];
            pool_[min_index] = null;
            return result;
        }
        return new float[size];
    }

    public synchronized float[] recycle(final float[] ptr) {
        int min_index = -1;
        int min_length = Integer.MAX_VALUE;
        final int ptr_len = ptr.length;
        for (int i = 0; i < pool_.length; i++) {
            if (pool_[i] == null) {
                pool_[i] = ptr;
                return null;
            }
            final int len = pool_[i].length;
            if (ptr_len > len && min_length > len) {
                min_index = i;
                min_length = len;
            }
        }

        if (min_index != -1) {
            pool_[min_index] = ptr;
        }
        return null;
    }
}