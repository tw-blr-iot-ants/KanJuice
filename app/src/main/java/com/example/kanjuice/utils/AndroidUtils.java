package com.example.kanjuice.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

public class AndroidUtils {

    public static void disableRecentAppsClick(Activity activity) {
        ActivityManager activityManager = (ActivityManager) activity.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(activity.getTaskId(), 0);
    }

    public static boolean isMyLauncherDefault(Context context) {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);

        List<IntentFilter> filters = new ArrayList<IntentFilter>();
        filters.add(filter);

        final String myPackageName = context.getPackageName();
        List<ComponentName> activities = new ArrayList<>();
        PackageManager packageManager = (PackageManager) context.getPackageManager();

        // You can use name of your package here as third argument
        packageManager.getPreferredActivities(filters, activities, null);

        if(activities.size() == 0) //no default
            return true;

        for (ComponentName activity : activities) {
            if (myPackageName.equals(activity.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static void clearKanJuiceAsDefaultApp(Context context) {
        if (AndroidUtils.isMyLauncherDefault(context)){
            context.getPackageManager().clearPackagePreferredActivities(context.getPackageName());
            AndroidUtils.chooseHomeApp(context);
        }
    }

    public static void chooseHomeApp(Context context) {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startMain);
    }
}
