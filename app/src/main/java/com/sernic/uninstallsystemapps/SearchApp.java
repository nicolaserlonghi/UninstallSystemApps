package com.sernic.uninstallsystemapps;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.grabner.circleprogress.AnimationState;
import at.grabner.circleprogress.AnimationStateChangedListener;
import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

/**
 * Created by nicola on 11/12/17.
 */

public class SearchApp extends AsyncTask<Void, Integer, Void> {
    private MainActivity mActivity;
    private PackageManager mPackageManager;
    private RecyclerView mRecyclerView;
    private CircleProgressView mCircleProgressView;
    private ArrayList<App> mApps;

    public SearchApp(MainActivity mActivity) {
        this.mActivity = mActivity;
        mPackageManager = this.mActivity.getPackageManager();
        mApps = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mRecyclerView = (RecyclerView)mActivity.findViewById(R.id.my_recycler_view);
        mCircleProgressView = (CircleProgressView)mActivity.findViewById(R.id.circleView);
        mCircleProgressView.setSeekModeEnabled(false);
        mCircleProgressView.setSpinningBarLength(80);
        mCircleProgressView.setSpinSpeed(2);
        mCircleProgressView.setShowTextWhileSpinning(true);

        mCircleProgressView.setText("Loading...");
        mCircleProgressView.setTextMode(TextMode.TEXT);
        mCircleProgressView.setUnitVisible(false);
        mCircleProgressView.spin();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        List<ApplicationInfo> apps = mPackageManager.getInstalledApplications(0);
        Collections.sort(apps, new ApplicationInfo.DisplayNameComparator(mPackageManager));   // Pone in ordine alfabetico le app
        publishProgress(0);

        float count = 0;
        for (ApplicationInfo applicationInfo : apps) {
            App app = new App(
                    applicationInfo.name,
                    applicationInfo.publicSourceDir
            );
            try {
                app.setIcon(mPackageManager.getApplicationIcon(applicationInfo.processName));
            } catch (PackageManager.NameNotFoundException e) {
                app.setIcon(mPackageManager.getDefaultActivityIcon());
                e.printStackTrace();
            }
            count++;
            System.out.println((int)(count/apps.size()*100));
            publishProgress((int)(count/apps.size()*100));
            mApps.add(app);
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if(values[0] == 0) {
            mCircleProgressView.stopSpinning();
            mCircleProgressView.setTextMode(TextMode.PERCENT);
            mCircleProgressView.setUnitVisible(true);
            mCircleProgressView.setValue(0);
        } else {
            mCircleProgressView.setValueAnimated(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mCircleProgressView.setOnAnimationStateChangedListener(
                new AnimationStateChangedListener() {
                    @Override
                    public void onAnimationStateChanged(AnimationState _animationState) {
                        if(_animationState == AnimationState.IDLE) {
                            mCircleProgressView.setVisibility(View.INVISIBLE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(new MyAdapter(mApps));

        // Notify activity
        mActivity.setApplicationList(mApps);
    }
}
