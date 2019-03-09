/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Nicola Serlonghi <nicolaserlonghi@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
