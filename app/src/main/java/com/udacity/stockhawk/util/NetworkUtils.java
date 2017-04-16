package com.udacity.stockhawk.util;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by flo on 16.04.17.
 */

public class NetworkUtils {
    public static boolean networkUp(ConnectivityManager connectivityManager) {
        //ConnectivityManager cm =
        //        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
