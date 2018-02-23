package com.sernic.uninstallsystemapps;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chrisplus.rootmanager.RootManager;
import com.chrisplus.rootmanager.container.Result;

import java.util.ArrayList;

import at.grabner.circleprogress.AnimationState;
import at.grabner.circleprogress.AnimationStateChangedListener;
import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

/**
 * Created by nicola on 25/12/17.
 */

public class RemoveApps extends AsyncTask <Void, Integer, Void> {
    private MainActivity mActivity;
    private RecyclerView mRecyclerView;
    private ArrayList<App> mApps;
    private ArrayList<App> temp;

    public RemoveApps(MainActivity mActivity) {
        this.mActivity = mActivity;
        mApps = mActivity.getApplicationList();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
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
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

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