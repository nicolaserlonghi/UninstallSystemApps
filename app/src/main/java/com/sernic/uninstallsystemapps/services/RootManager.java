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

package com.sernic.uninstallsystemapps.services;

import androidx.annotation.Nullable;

import com.sernic.uninstallsystemapps.AppExecutors;
import com.sernic.uninstallsystemapps.helpers.SingleLiveEvent;
import com.sernic.uninstallsystemapps.models.App;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class RootManager {

    private final String[] SU_BINARY_DIRS = {
            "/system/bin",
            "/system/sbin",
            "/system/xbin",
            "/vendor/bin",
            "/sbin",
    };
    private AppExecutors appExecutors;
    SingleLiveEvent<Boolean> uninstallResult = new SingleLiveEvent<>();

    public RootManager(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public boolean hasRootedPermision() {
        return Shell.SU.available();
    }

    public boolean wasRooted() {
        boolean hasRooted = false;
        for (String path : SU_BINARY_DIRS) {
            File su = new File(path + "/su");
            if (su.exists()) {
                hasRooted = true;
                break;
            } else {
                hasRooted = false;
            }
        }
        return hasRooted;
    }

    public void removeApps(List<App> appsToRemove) {
        appExecutors.diskIO().execute(() -> {
            boolean uninstalledNoProblems = true;
            for(App app : appsToRemove) {
                boolean result = true;
                if(app.isSystemApp()) {
                    result = uninstallSystemApp(app.getPath());
                    if(!result)
                        result = uninstallSystemAppAlternativeMethod(app.getPackageName());
                }
                else
                    result = uninstallUserApp(app.getPackageName());
                if(!result)
                    uninstalledNoProblems = false;
            }
            uninstallResult.postValue(uninstalledNoProblems);
        });
    }

    private boolean uninstallSystemApp(String appApk) {
        executeCommandSU("mount -o rw,remount /system");
        executeCommandSU("rm " + appApk);
        executeCommandSU("mount -o ro,remount /system");
        boolean result = checkUninstallSuccessful(appApk);
        return result;
    }

    private boolean uninstallSystemAppAlternativeMethod(String packageName) {
        String commandOutput = executeCommandSU("pm uninstall --user 0 " + packageName);
        boolean result = checkPMCommandSuccesfull(commandOutput);
        return result;
    }

    private boolean uninstallUserApp(String packageName) {
        String commandOutput = executeCommandSU("pm uninstall " + packageName);
        boolean result = checkPMCommandSuccesfull(commandOutput);
        return result;
    }

    @Nullable
    private String executeCommandSU(String command) {
        List<String> stdout = new ArrayList<>();
        List<String> stderr = new ArrayList<>();
        try {
            Shell.Pool.SU.run(command, stdout, stderr, true);
        } catch (Shell.ShellDiedException e) {
            e.printStackTrace();
        }
        if (stdout == null)
            return null;
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : stdout) {
            stringBuilder.append(line).append("\n");
        }
        return stringBuilder.toString();
    }

    @Nullable
    private String executeCommandSH(String command) {
        List<String> stdout = new ArrayList<>();
        List<String> stderr = new ArrayList<>();
        try {
            Shell.Pool.SH.run(command, stdout, stderr, true);
        } catch (Shell.ShellDiedException e) {
            e.printStackTrace();
        }
        if (stdout == null)
            return null;
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : stdout) {
            stringBuilder.append(line).append("\n");
        }
        return stringBuilder.toString();
    }

    private boolean checkUninstallSuccessful(String appApk) {
        String output = executeCommandSH("ls " + appApk);
        return output != null && output.trim().isEmpty();
    }

    private boolean checkPMCommandSuccesfull(String commandOutput) {
        boolean result = commandOutput != null && commandOutput.toLowerCase().contains("success");
        return result;
    }

    public SingleLiveEvent<Boolean> getUninstallResult() {
        return uninstallResult;
    }

    public String rebootDevice() {
        String result = executeCommandSU("reboot");
        return result;
    }
}
