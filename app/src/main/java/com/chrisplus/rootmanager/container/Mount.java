package com.chrisplus.rootmanager.container;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Mount {

    final File mDevice;

    final File mMountPoint;

    final String mType;

    final Set<String> mFlags;

    public Mount(File device, File path, String type, String flagsStr) {
        mDevice = device;
        mMountPoint = path;
        mType = type;
        mFlags = new HashSet<>(Arrays.asList(flagsStr.split(",")));
    }

    public File getDevice() {
        return mDevice;
    }

    public File getMountPoint() {
        return mMountPoint;
    }

    public String getType() {
        return mType;
    }

    public Set<String> getFlags() {
        return mFlags;
    }

    @Override
    public String toString() {
        return String.format("%s on %s type %s %s", mDevice, mMountPoint, mType, mFlags);
    }
}
