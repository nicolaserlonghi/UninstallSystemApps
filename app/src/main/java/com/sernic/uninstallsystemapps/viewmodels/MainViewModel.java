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

package com.sernic.uninstallsystemapps.viewmodels;

import android.app.Application;
import android.content.Context;

import com.sernic.uninstallsystemapps.AppExecutors;
import com.sernic.uninstallsystemapps.SearchApps;
import com.sernic.uninstallsystemapps.UninstallSystemApps;
import com.sernic.uninstallsystemapps.models.App;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MainViewModel extends BaseViewModel {

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final Application application;

        public Factory(Application application) {
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new MainViewModel(application);
        }
    }

    public LiveData<List<App>> getInstalledApps(Context context) {
        AppExecutors appExecutors = getAppExecutors();
        SearchApps searchApps = new SearchApps(context, appExecutors);
        LiveData<List<App>> installedApps = searchApps.getInstalledApps();
        return installedApps;
    }

    private AppExecutors getAppExecutors() {
        UninstallSystemApps uninstallSystemApps = getApplication();
        AppExecutors appExecutors = uninstallSystemApps.getAppExecutors();
        return appExecutors;
    }
}
