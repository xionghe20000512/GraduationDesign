package com.example.himalaya.utils;

import android.util.Log;

public class LogUtil {

    public static String sTAG = "LogUtil";

    //控制是否要输出log
    public static boolean sIsRelease;

    /**
     * 如果是要发布了，可以在application里面把这里release一下，这样就没有log输出了
     */
    public static void init(String baseTag, boolean isRelease) {
        sTAG = baseTag;
        sIsRelease = isRelease;
    }

    public static void d(String TAG, String content) {
        if (!sIsRelease) {
            Log.d("[" + sTAG + "]" + TAG, content);//包名+分类，如：[com.example.himalaya]MainActivity: category-->党员学习园地
        }
    }

    public static void v(String TAG, String content) {
        if (!sIsRelease) {
            Log.d("[" + sTAG + "]" + TAG, content);
        }
    }

    public static void i(String TAG, String content) {
        if (!sIsRelease) {
            Log.d("[" + sTAG + "]" + TAG, content);
        }
    }

    public static void w(String TAG, String content) {
        if (!sIsRelease) {
            Log.d("[" + sTAG + "]" + TAG, content);
        }
    }

    public static void e(String TAG, String content) {
        if (!sIsRelease) {
            Log.d("[" + sTAG + "]" + TAG, content);
        }
    }
}
