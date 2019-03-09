package com.sernic.uninstallsystemapps;

import android.util.Log;

/**
 * Android Log wrapper class that can use {@link String#format(String, Object...)} in logging message
 * https://gist.github.com/tlync/1868304
 */
public class Logger {
    /**
     * Sends a VERBOSE log message and logs the exception.
     * @param tag
     * @param msg
     * @return
     */
    public static int v(String tag, String msg) {
        return Log.v(tag, msg);
    }

    /**
     * Sends a VERBOSE log message and logs the exception.
     * @param tag
     * @param msg
     * @param e
     * @return
     */
    public static int v(String tag, String msg, Throwable e) {
        return Log.v(tag, msg, e);
    }

    /**
     * Sends a DEBUG log message and logs the exception.
     * @param tag
     * @param msg
     * @return
     */
    public static int d(String tag, String msg) {
        return Log.d(tag, msg);
    }

    /**
     * Sends a DEBUG log message and logs the exception.
     * @param tag
     * @param msg
     * @param e
     * @return
     */
    public static int d(String tag, String msg, Throwable e) {
        return Log.d(tag, msg, e);
    }

    /**
     * Sends a WARN log message and logs the exception.
     * @param tag
     * @param msg
     * @return
     */
    public static int w(String tag, String msg) {
        return Log.w(tag, msg);
    }

    /**
     * Sends a WARN log message and logs the exception.
     * @param tag
     * @param msg
     * @param e
     * @return
     */
    public static int w(String tag, String msg, Throwable e) {
        return Log.w(tag, msg, e);
    }

    /**
     * Sends a INFO log message and logs the exception.
     * @param tag
     * @param msg
     * @return
     */
    public static int i(String tag, String msg) {
        return Log.i(tag, msg);
    }

    /**
     * Sends a INFO log message and logs the exception.
     * @param tag
     * @param msg
     * @param e
     * @return
     */
    public static int i(String tag, String msg, Throwable e) {
        return Log.i(tag, msg, e);
    }

    /**
     * Sends a ERROR log message and logs the exception.
     * @param tag
     * @param msg
     * @return
     */
    public static int e(String tag, String msg) {
        return Log.e(tag, msg);
    }

    /**
     * Sends a ERROR log message and logs the exception.
     * @param tag
     * @param msg
     * @param e
     * @return
     */
    public static int e(String tag, String msg, Throwable e) {
        return Log.e(tag, msg, e);
    }
}
