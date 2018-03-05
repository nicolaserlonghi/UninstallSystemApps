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

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import com.chrisplus.rootmanager.RootManager;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


/**
 * Created by nicola on 25/12/17.
 */

public class RemoveApps extends AsyncTask <Void, Integer, Void> {
    private MainActivity mActivity;
    private RecyclerView mRecyclerView;
    private ArrayList<App> mApps;
    private ArrayList<App> temp;

    private AlphaAnimation inAnimation;
    private AlphaAnimation outAnimation;

    private FrameLayout progressBarHolder;
    private FloatingActionButton floatingActionButton;

    public RemoveApps(MainActivity mActivity) {
        this.mActivity = mActivity;
        mApps = mActivity.getApplicationList();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressBarHolder = (FrameLayout) mActivity.findViewById(R.id.progressBarHolder);
        floatingActionButton = (FloatingActionButton) mActivity.findViewById(R.id.fab);

        // Animation start
        floatingActionButton.setEnabled(false);
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);

        temp = new ArrayList<>();
        mRecyclerView = (RecyclerView)mActivity.findViewById(R.id.my_recycler_view);

    }

    @Override
    protected Void doInBackground(Void... voids) {
        for(App app : mApps) {
            if(app.isSelected()) {
                if (app.isSystemApp()) {
                    RootManager.getInstance().uninstallSystemApp(app.getPath());
                } else {
                    RootManager.getInstance().uninstallPackage(app.getPackageName());
                }
            } else {
                temp.add(app);
            }
        }
        // Wait to animation
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        // Animation stop
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
        floatingActionButton.setEnabled(true);

        // Update recyclerView
        ((MyAdapter)mRecyclerView.getAdapter()).updateList(temp);
        // Notify activity
        mActivity.setApplicationList(temp);
        View view = mActivity.findViewById(R.id.coordinatorLayout);
        Snackbar.make(view, mActivity.getResources().getString(R.string.snackBar_ok_remove_apps), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        // Create allert to reboot the phone
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(mActivity.getResources().getString(R.string.reboot_now))
                .setPositiveButton(mActivity.getResources().getString(R.string.button_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with reboot
                        RootManager.getInstance().restartDevice();
                    }
                })
                .setNegativeButton(mActivity.getResources().getString(R.string.button_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }
}