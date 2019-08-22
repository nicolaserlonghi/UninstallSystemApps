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

package com.sernic.uninstallsystemapps.viewmodels;

import android.app.Application;
import android.os.Build;

import com.sernic.uninstallsystemapps.DataRepository;
import com.sernic.uninstallsystemapps.services.LoadApps;
import com.sernic.uninstallsystemapps.UninstallSystemApps;
import com.sernic.uninstallsystemapps.models.App;
import com.sernic.uninstallsystemapps.services.RootManager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MainViewModel extends BaseViewModel {

    private final RootManager rootManager;

    public MainViewModel(@NonNull Application application, RootManager rootManager) {
        super(application);
        this.rootManager = rootManager;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final Application application;
        private final RootManager rootManager;

        public Factory(Application application, RootManager rootManager) {
            this.application = application;
            this.rootManager = rootManager;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new MainViewModel(application, rootManager);
        }
    }

    public LiveData<List<App>> getInstalledApps() {
        LoadApps loadApps = getLoadApps();
        LiveData<List<App>> installedApps = loadApps.getInstalledApps();
        return installedApps;
    }

    private LoadApps getLoadApps() {
        UninstallSystemApps uninstallSystemApps = getApplication();
        DataRepository dataRepository = uninstallSystemApps.getDataRepository();
        LoadApps loadApps = dataRepository.getLoadApps();
        return loadApps;
    }

    public List<App> orderAppForInstallationDateDesc(List<App> installedApps) {
        // TODO: Check if I can remove this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            installedApps.sort(((o1, o2) -> o2.getInstalledDate().compareTo(o1.getInstalledDate())));
        }
        return installedApps;
    }

    public List<App> orderAppInAlfabeticalOrder(List<App> installedApps) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            installedApps.sort(((o1, o2) -> o1.getName().compareTo(o2.getName())));
        }
        return installedApps;
    }

    public List<App> hideSystemApps(List<App> installedApps) {
        for(App app : installedApps) {
            boolean isUserApp = !app.isSystemApp();
            if(isUserApp)
                app.setVisible(true);
            else
                app.setVisible(false);
        }
        return installedApps;
    }

    public List<App> hideUserApps(List<App> installedApps) {
        for(App app : installedApps) {
            boolean isSystemApp = app.isSystemApp();
            if(isSystemApp)
                app.setVisible(true);
            else
                app.setVisible(false);
        }
        return installedApps;
    }

    public List<App> showAllApps(List<App> installedApps) {
        for(App app : installedApps) {
            app.setVisible(true);
        }
        return installedApps;
    }

    public List<App> uncheckedAllApps(List<App> installedApps) {
        for (App app : installedApps) {
            app.setSelected(false);
        }
        return installedApps;
    }

    public List<App> filterApps(String query, List<App> installedApp) {
        List<App> filteredApps = new ArrayList<>();
        if(query.isEmpty())
            return installedApp;
        else {
            for(App app : installedApp) {
                if(app.getName().toLowerCase().contains(query))
                    filteredApps.add(app);
            }
            return filteredApps;
        }
    }

    public void removeApps(List<App> installedApps) {
        List<App> selectedApps = getSelectedApps(installedApps);
        rootManager.hasRootedPermision();
        rootManager.removeApps(selectedApps);
    }

    private List<App> getSelectedApps(List<App> installedApps) {
        List<App> selectedApps = new ArrayList<>();
        for (App app : installedApps) {
            if (app.isVisible())
                if (app.isSelected())
                    selectedApps.add(app);
        }
        return selectedApps;
    }

    public boolean atLeastAnAppIsSelected(List<App> installedApps) {
        for(App app : installedApps) {
            if(app.isVisible())
                if(app.isSelected())
                    return true;
        }
        return false;
    }
}
