package com.sernic.uninstallsystemapps;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.chrisplus.rootmanager.RootManager;
import com.chrisplus.rootmanager.container.Result;

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
            if (checkBusibox()) {
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
        } else if(busybox) {
            Snackbar.make(view, mActivity.getResources().getString(R.string.snackBar_no_root), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } else {
            Snackbar.make(view, mActivity.getResources().getString(R.string.snanckBar_no_busyBox), Snackbar.LENGTH_LONG).setAction(mActivity.getResources().getString(R.string.button_install), new View.OnClickListener() {
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

    private Boolean checkBusibox() {
        if(RootManager.getInstance().runCommand("/system/xbin/busybox ls").getMessage().length() < 100)
            if (RootManager.getInstance().runCommand("/system/bin/busybox ls").getMessage().length() < 100)
                return false;
        return true;
    }
}
