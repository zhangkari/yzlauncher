package rmkj.lib.read.util;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

/**
 * 日志处理类
 *
 * @author zsx
 * @date 2013-12-18
 * @description
 */
public class LogUtil {
    public static boolean DEBUG = true;
    private final static String TAG = "LogUtil:";

    public static void e(Object cls, String message) {
        if (DEBUG) {
            Log.e(TAG + cls.getClass().getSimpleName(), message);
        }
    }

    public static void e(String tag, String message) {
        if (DEBUG) {
            Log.e(TAG + tag, message);
        }
    }

    public static void e(Throwable tr) {
        if (DEBUG) {
            if (tr.getMessage() != null) {
                Log.e(TAG, tr.getMessage().substring(0, 100));
            }
        }
    }

    public static void d(String tag, String message) {
        if (DEBUG) {
            Log.d(TAG + tag, message);
        }
    }

    public static void d(Object cls, String message) {
        if (DEBUG) {
            Log.d(TAG + cls.getClass().getSimpleName(), message);
        }
    }

    /**
     * 判断当前的屏幕是否是竖屏   此地借用一下嘿嘿
     */
    public static boolean isPort(Context context) {
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏
            return false;

        } else if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 竖屏
            return true;
        }
        return true;
    }
}
