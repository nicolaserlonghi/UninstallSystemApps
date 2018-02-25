package com.sernic.uninstallsystemapps;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.RotatingCircle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.grabner.circleprogress.AnimationState;
import at.grabner.circleprogress.AnimationStateChangedListener;
import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

import static android.widget.GridLayout.VERTICAL;

/**
 * Created by nicola on 11/12/17.
 */

public class SearchApp extends AsyncTask<Void, Integer, Void> {
    private MainActivity mActivity;
    private PackageManager mPackageManager;
    private RecyclerView mRecyclerView;
    private ArrayList<App> mApps;
    private FloatingActionButton fab;
    private TextView numApps;
    private ImageButton selectAll;
    private ProgressBar progressBar;

    public SearchApp(MainActivity mActivity) {
        this.mActivity = mActivity;
        mPackageManager = this.mActivity.getPackageManager();
        mApps = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        selectAll = (ImageButton) mActivity.findViewById(R.id.select_all);
        numApps = (TextView) mActivity.findViewById(R.id.num_apps);
        fab = (FloatingActionButton) mActivity.findViewById(R.id.fab);
        mRecyclerView = (RecyclerView)mActivity.findViewById(R.id.my_recycler_view);

        //ProgressBar animation
        progressBar = (ProgressBar) mActivity.findViewById(R.id.progress);
        RotatingCircle rotatingCircle = new RotatingCircle();
        rotatingCircle.setBounds(0, 0, 100, 100);
        rotatingCircle.setColor(mActivity.getResources().getColor(R.color.primary));
        progressBar.setIndeterminateDrawable(rotatingCircle);

        numApps.setText(mActivity.getResources().getString(R.string.loading_animation));
    }

    @Override
    protected Void doInBackground(Void... voids) {
        List<ApplicationInfo> apps = mPackageManager.getInstalledApplications(0);
        Collections.sort(apps, new ApplicationInfo.DisplayNameComparator(mPackageManager));   // Sort apps in alphabetical order
        // I look for the installed apps and extrapolate the data I need
        for (ApplicationInfo applicationInfo : apps) {
            Boolean systemApp;
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                // System apps
                systemApp = true;
            } else {
                // User apps
                systemApp = false;
            }
                App app = new App(

                        (String) mPackageManager.getApplicationLabel(applicationInfo),
                        applicationInfo.sourceDir,
                        applicationInfo.packageName,
                        systemApp
                );
                try {
                    app.setIcon(mPackageManager.getApplicationIcon(applicationInfo.processName));
                } catch (PackageManager.NameNotFoundException e) {
                    app.setIcon(mPackageManager.getDefaultActivityIcon());
                    e.printStackTrace();
                }
                mApps.add(app);
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        progressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
        selectAll.setVisibility(View.VISIBLE);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity.getApplicationContext(), VERTICAL)); // Metto una riga tra due elementi della lista
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(new MyAdapter(mApps, mActivity));

        // Notify activity
        mActivity.setApplicationList(mApps);
    }
}
