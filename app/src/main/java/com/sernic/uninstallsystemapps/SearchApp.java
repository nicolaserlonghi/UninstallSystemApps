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

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.style.RotatingCircle;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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
    private ProgressBar progressBar;
    private TextView textSearchAnimation;
    private RelativeLayout infoLayout;
    private AppBarLayout appBarLayout;

    public SearchApp(MainActivity mActivity) {
        this.mActivity = mActivity;
        mPackageManager = this.mActivity.getPackageManager();
        mApps = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        fab = (FloatingActionButton) mActivity.findViewById(R.id.fab);
        mRecyclerView = (RecyclerView) mActivity.findViewById(R.id.my_recycler_view);
        infoLayout = (RelativeLayout) mActivity.findViewById(R.id.info_layout);
        appBarLayout = (AppBarLayout) mActivity.findViewById(R.id.layout_toolbar);

        //ProgressBar animation
        textSearchAnimation = (TextView) mActivity.findViewById(R.id.text_search_animation);
        progressBar = (ProgressBar) mActivity.findViewById(R.id.progress);
        RotatingCircle rotatingCircle = new RotatingCircle();
        rotatingCircle.setBounds(0, 0, 100, 100);
        rotatingCircle.setColor(mActivity.getResources().getColor(R.color.primary));
        progressBar.setIndeterminateDrawable(rotatingCircle);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // Firebase monitoring performance
        Trace myTrace = FirebasePerformance.getInstance().newTrace("Search_app");
        myTrace.start();

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

        myTrace.stop();

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
        textSearchAnimation.setVisibility(View.GONE);
        infoLayout.setVisibility(View.VISIBLE);
        appBarLayout.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);

        // Add divider between two element of recyclerView
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(mActivity.getApplicationContext(), mActivity.getResources().getColor(R.color.divider), 3));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(new MyAdapter(mApps, mActivity));

        // Notify activity
        mActivity.setApplicationList(mApps);
    }
}
