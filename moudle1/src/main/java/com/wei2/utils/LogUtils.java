package com.wei2.utils;

import android.util.Log;

public class LogUtils {
    public static boolean isDebug = true;

    public static void e(String tag, String meg) {
        if (isDebug) {
            Log.e(tag,meg);
        }
    }
}
