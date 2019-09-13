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

package com.sernic.uninstallsystemapps.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.sernic.uninstallsystemapps.R;

public class CustomAlertDialog {

    public static void showAlertDialogWithOneButton(Context context,
                                                    String title,
                                                    String message,
                                                    String textButton,
                                                    DialogInterface.OnClickListener buttonListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(textButton, buttonListener);
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.alert_dialog_message_size));
    }

    public static void showAlertDialogWithTwoButton(Context context,
                                                    String title,
                                                    String message,
                                                    String textPositiveButton,
                                                    DialogInterface.OnClickListener positveButtonListener,
                                                    String textNegativeButton,
                                                    DialogInterface.OnClickListener negativeButtonListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(textPositiveButton, positveButtonListener);
        builder.setNegativeButton(textNegativeButton, negativeButtonListener);
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.alert_dialog_message_size));
    }

    public static AlertDialog showProgressDialog(Context context, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setView(R.layout.dialog_progress_circular);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public static void stopProgressDialog(AlertDialog dialog) {
        if(dialog == null)
            return;
        dialog.hide();
    }
}
