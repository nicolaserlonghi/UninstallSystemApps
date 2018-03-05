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

package com.chrisplus.rootmanager.utils;

import com.chrisplus.rootmanager.container.Command;
import com.chrisplus.rootmanager.container.Mount;
import com.chrisplus.rootmanager.container.Shell;
import com.chrisplus.rootmanager.exception.PermissionException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Remounter {

    private static final String MOUNT_FILE = "/proc/mounts";

    public static boolean remount(String file, String mountType) {

        if (file.endsWith("/") && !file.equals("/")) {
            file = file.substring(0, file.lastIndexOf("/"));
        }

        boolean foundMount = false;

        List<Mount> mounts = getMounts();
        if (mounts == null || mounts.isEmpty()) {
            return false;
        }

        while (!foundMount) {
            for (Mount mount : mounts) {
                if (file.equals(mount.getMountPoint().toString())) {
                    foundMount = true;
                    break;
                }
            }
            if (!foundMount) {
                file = (new File(file).getParent()).toString();
            }
        }

        Mount mountPoint = getMountPoint(file);

        final boolean isMountMode = mountPoint.getFlags().contains(mountType.toLowerCase());

        if (!isMountMode) {

            Command command = new Command("busybox mount -o remount," + mountType.toLowerCase() + " " + mountPoint.getDevice().getAbsolutePath() + " " + mountPoint.getMountPoint().getAbsolutePath(),
                    "toolbox mount -o remount," + mountType.toLowerCase() + " " + mountPoint.getDevice().getAbsolutePath() + " " + mountPoint.getMountPoint().getAbsolutePath(),
                    "mount -o remount," + mountType.toLowerCase() + " " + mountPoint.getDevice().getAbsolutePath() + " " + mountPoint.getMountPoint().getAbsolutePath(),
                    "/system/bin/toolbox mount -o remount," + mountType.toLowerCase() + " " + mountPoint.getDevice().getAbsolutePath() + " " + mountPoint.getMountPoint().getAbsolutePath(),
                    "toybox mount -o remount," + mountType.toLowerCase() + " " + mountPoint.getDevice().getAbsolutePath() + " " + mountPoint.getMountPoint().getAbsolutePath()) {

                @Override
                public void onUpdate(int id, String message) {

                }

                @Override
                public void onFinished(int id) {

                }
            };

            try {
                Shell.startRootShell().add(command).waitForFinish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (PermissionException e) {
                e.printStackTrace();
            }

        }

        mountPoint = getMountPoint(file);

        if (mountPoint.getFlags().contains(mountType.toLowerCase())) {
            return true;
        } else {
            return false;
        }
    }

    private static Mount getMountPoint(String file) {

        List<Mount> mounts = getMounts();
        if (mounts == null || mounts.isEmpty()) {
            return null;
        }

        for (File path = new File(file); path != null; ) {
            for (Mount mount : mounts) {
                if (mount.getMountPoint().equals(path)) {
                    return mount;
                }
            }
        }

        return null;
    }

    private static List<Mount> getMounts() {

        LineNumberReader lnr = null;

        try {
            lnr = new LineNumberReader(new FileReader(MOUNT_FILE));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return null;
        }

        String line;
        ArrayList<Mount> mounts = new ArrayList<Mount>();
        try {
            while ((line = lnr.readLine()) != null) {
                String[] fields = line.split(" ");
                mounts.add(
                        new Mount(new File(fields[0]), new File(fields[1]), fields[2], fields[3]));
            }
            lnr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mounts;
    }
}
