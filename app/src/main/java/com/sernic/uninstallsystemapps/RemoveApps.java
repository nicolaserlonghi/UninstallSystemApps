package com.sernic.uninstallsystemapps;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chrisplus.rootmanager.RootManager;
import com.chrisplus.rootmanager.container.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private CircleProgressView mCircleProgressView;
    private ArrayList<App> mApps;
    private ArrayList<App> temp;
    private FloatingActionButton fab;
    private int totalAppSelected;

    public RemoveApps(MainActivity mActivity) {
        this.mActivity = mActivity;
        mApps = mActivity.getApplicationList();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        temp = new ArrayList<>();
        //totalAppSelected = ((MyAdapter)mRecyclerView.getAdapter()).getCheckOneApp();
        fab = (FloatingActionButton) mActivity.findViewById(R.id.fab);
        mRecyclerView = (RecyclerView)mActivity.findViewById(R.id.my_recycler_view);
        // Imposto i parametri dell'animazione iniziale
        mCircleProgressView = (CircleProgressView)mActivity.findViewById(R.id.circleView);
        mCircleProgressView.setSeekModeEnabled(false);
        mCircleProgressView.setSpinningBarLength(80);
        mCircleProgressView.setSpinSpeed(2);
        mCircleProgressView.setShowTextWhileSpinning(true);
        // Imposto l'animazione percent
        mCircleProgressView.setTextMode(TextMode.PERCENT);
        mCircleProgressView.setUnitVisible(true);
        mCircleProgressView.setValue(0);
        // Nascondo quello che devo e rendo visbile l'animazione

        mRecyclerView.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.INVISIBLE);
        mCircleProgressView.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        float count = 0;
        for(App app : mApps) {
            if(app.isSelected()) {
                if (app.isSystemApp()) {
                    Result result = RootManager.getInstance().uninstallSystemApp(app.getPath());
                    System.out.println("result: " + result.getResult());
                    System.out.println("result: " + result.getMessage());
                } else {
                    Result result = RootManager.getInstance().uninstallPackage(app.getPackageName());
                    System.out.println("Result 2: " + result.getResult());
                }
                count++;
                publishProgress((int) (count / mApps.size() * 100));
            } else {
                temp.add(app);
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        //Incremento l'animazione
        mCircleProgressView.setValueAnimated(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Controllo che l'animazione abbbia finito prima di nasconderla
        mCircleProgressView.setOnAnimationStateChangedListener(
                new AnimationStateChangedListener() {
                    @Override
                    public void onAnimationStateChanged(AnimationState _animationState) {
                        if(_animationState == AnimationState.IDLE) {
                            mCircleProgressView.setVisibility(View.INVISIBLE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            fab.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );

        // Update recyclerView
        ((MyAdapter)mRecyclerView.getAdapter()).updateList(temp);
        // Notify activity
        mActivity.setApplicationList(temp);
        View view = mActivity.findViewById(R.id.coordinatorLayout);
        Snackbar.make(view, "Le app selezionate sono state rimosse!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }
}