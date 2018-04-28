/*
 * MIT License
 *
 * Copyright (c) 2018 Nicola Serlonghi
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

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.chrisplus.rootmanager.RootManager;

/**
 * Created by nicola on 25/12/17.
 */

public class ObtainRoot extends AsyncTask<Void, Void, Boolean> {

    private MainActivity mActivity;
    private Boolean busybox = true;

    public ObtainRoot(MainActivity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        if(RootManager.getInstance().obtainPermission()) {
            if (checkBusybox()) {
                return true;
            } else {
                busybox = false;
            }
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        View view = mActivity.findViewById(R.id.coordinatorLayout);
        if (aBoolean) {
            Snackbar.make(view, mActivity.getResources().getString(R.string.snackBar_yes_root), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            mActivity.setBusyBox(true);
        } else if(busybox) {
            Snackbar.make(view, mActivity.getResources().getString(R.string.snackBar_no_root), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            mActivity.setBusyBox(false);
        } else {
            Snackbar.make(view, mActivity.getResources().getString(R.string.snackBar_no_busyBox), Snackbar.LENGTH_LONG).setAction(mActivity.getResources().getString(R.string.button_install), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=stericson.busybox"));
                    mActivity.startActivity(intent);
                }
            }).show();
            aBoolean = true;
            mActivity.setBusyBox(false);
        }


        mActivity.setRootAccess(aBoolean);
    }

    public Boolean checkBusybox() {
        if(RootManager.getInstance().runCommand("/system/xbin/busybox ls").getMessage().length() < 100)
            if (RootManager.getInstance().runCommand("/system/bin/busybox ls").getMessage().length() < 100)
                if (RootManager.getInstance().runCommand("/sbin/busybox ls").getMessage().length() < 100)
                    return false;
        return true;
    }
}
