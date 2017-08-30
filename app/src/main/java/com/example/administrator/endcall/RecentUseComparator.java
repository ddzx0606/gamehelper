package com.example.administrator.endcall;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jiamiaohe on 2017/7/31.
 */
class RecentUseComparator implements Comparator<UsageStats>

{

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int compare(UsageStats lhs,UsageStats rhs) {

        return (lhs.getLastTimeUsed() > rhs.getLastTimeUsed()) ? -1 : (lhs.getLastTimeUsed()== rhs.getLastTimeUsed()) ? 0 : 1;
    }

}