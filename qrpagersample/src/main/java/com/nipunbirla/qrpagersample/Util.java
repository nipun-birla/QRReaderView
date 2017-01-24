package com.nipunbirla.qrpagersample;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Nipun on 1/24/2017.
 */

public class Util {

    private static Handler mainHandler = new Handler(Looper.getMainLooper());

    public static void postOnUiThread(Runnable runnable) {
        mainHandler.post(runnable);
    }

    public static void postDelayedOnUIThread(Runnable runnable, int delay) {
        mainHandler.postDelayed(runnable, delay);
    }
}
