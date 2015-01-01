package info.narazaki.android.lib.system;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NAndroidSystem {

    public static boolean isOnline(final Context context) {
        try {
            final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null) {
                return manager.getActiveNetworkInfo().isConnected();
            }
            return false;
        } catch (final Exception e) {
        }
        return true;
    }

}
