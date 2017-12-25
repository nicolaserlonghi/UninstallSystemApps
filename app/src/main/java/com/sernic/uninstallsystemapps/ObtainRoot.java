package com.sernic.uninstallsystemapps;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.chrisplus.rootmanager.RootManager;

/**
 * Created by nicola on 25/12/17.
 */

public class ObtainRoot extends AsyncTask<Void, Void, Boolean> {

    private MainActivity mActivity;

    public ObtainRoot(MainActivity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return RootManager.getInstance().obtainPermission();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        View view = mActivity.findViewById(R.id.coordinatorLayout);
        if (aBoolean) {
            Snackbar.make(view, "Root ottenuto!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } else
            Snackbar.make(view, "Non ho ottenuto i privilegi di root!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

        mActivity.setRootAccess(aBoolean);
    }

}
