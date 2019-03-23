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
 * furnished to do so, SUBJECT to the following conditions:
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

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.sernic.uninstallsystemapps.models.App;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import androidx.lifecycle.MutableLiveData;

public class LoadApps {

    private Context context;
    private AppExecutors appExecutors;
    private MutableLiveData<List<App>> installedApps = new MutableLiveData<>();
    private List<App> installedAppList = new ArrayList<>();
    private PackageManager packageManager;

    public LoadApps(Context context, AppExecutors appExecutors) {
        this.context = context;
        this.appExecutors = appExecutors;
    }

    public void searchInstalledApps() {
        appExecutors.diskIO().execute(() -> {
            List<ApplicationInfo> installedApplicationsInfo = getInstalledApplication(context);
            appDetails(installedApplicationsInfo);
            updateInstalledApps();
        });
    }

    private List<ApplicationInfo> getInstalledApplication(Context context) {
        getPackageManager(context);
        List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(0);
        return installedApps;
    }

    private void getPackageManager(Context context) {
        packageManager = context.getPackageManager();
    }

    private void appDetails(List<ApplicationInfo> installedApplicationsInfo) {
        for (ApplicationInfo applicationInfo : installedApplicationsInfo) {
            createApp(applicationInfo);
        }
    }

    private void createApp(ApplicationInfo applicationInfo) {
        boolean systemApp = isSystemApps(applicationInfo);
        String label = getApplicationLabel(applicationInfo);
        String sourceDir = getApplicationSourceDir(applicationInfo);
        String packageName = getApplicationPackageName(applicationInfo);
        Drawable icon = getAppliactionIcon(applicationInfo);
        Date installedDate = getInstalledDate(packageName);
        App app = new App(
                label,
                sourceDir,
                packageName,
                icon,
                systemApp,
                installedDate
        );
        addAppToArrayList(app);
    }

    private boolean isSystemApps(ApplicationInfo applicationInfo) {
        boolean systemApp;
        if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1)
            systemApp = true;
        else
            systemApp = false;
        return systemApp;
    }

    private String getApplicationLabel(ApplicationInfo applicationInfo) {
        String applicationLabel = (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationLabel;
    }

    private String getApplicationSourceDir(ApplicationInfo applicationInfo) {
        String applicationSourceDir = applicationInfo.sourceDir;
        return applicationSourceDir;
    }

    private String getApplicationPackageName(ApplicationInfo applicationInfo) {
        String applicationPackageName = applicationInfo.packageName;
        return applicationPackageName;
    }

    private Drawable getAppliactionIcon(ApplicationInfo applicationInfo) {
        Drawable applicationIcon;
        try {
            applicationIcon = packageManager.getApplicationIcon(applicationInfo.processName);
        } catch (PackageManager.NameNotFoundException e) {
            applicationIcon = getDefaultApplicationIcon();
        }
        return applicationIcon;
    }

    private Drawable getDefaultApplicationIcon() {
        Drawable defaultApplicationIcon = packageManager.getDefaultActivityIcon();
        return defaultApplicationIcon;
    }

    private Date getInstalledDate(String packageName) {
        Long installDate = null;
        try {
            installDate = packageManager.getPackageInfo(packageName, 0).firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            installDate = Calendar.getInstance().getTimeInMillis();
        }
        Date date = new Date(installDate);
        return date;
    }

    private void addAppToArrayList(App app) {
        installedAppList.add(app);
    }

    private void updateInstalledApps() {
        installedApps.postValue(installedAppList);
    }

    public MutableLiveData<List<App>> getInstalledApps() {
        return installedApps;
    }
}
