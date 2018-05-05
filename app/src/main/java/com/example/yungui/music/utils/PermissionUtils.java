package com.example.yungui.music.utils;

import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Process;

import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;
import static android.support.v4.app.AppOpsManagerCompat.MODE_ALLOWED;

/**
 * Created by 22892 on 2018/1/14.
 */

public class PermissionUtils {
    public static boolean checkForUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
    }

    public static ComponentName getTopApp(Context context) {
        try {
            String packageName = "";
            String className = "";
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 10000;
            UsageStatsManager sUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime);
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    packageName = event.getPackageName();
                    className = event.getClassName();
                }
            }

            if (!android.text.TextUtils.isEmpty(packageName)) {
                return new ComponentName(packageName, className);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
