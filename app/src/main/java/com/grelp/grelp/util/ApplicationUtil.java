package com.grelp.grelp.util;

import android.content.pm.PackageManager;

public class ApplicationUtil {
    public static boolean isAppInstalled(PackageManager pm, String packageName) {
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
