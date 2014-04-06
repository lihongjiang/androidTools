package com.bslee.MeSport.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-10-22
 * Time: 下午1:46
 * To change this template use File | Settings | File Templates.
 */
public class PixUtils {
    /**
     * 判断屏幕分辨率
     * @param activity
     * @return
     */
    public static int Dpi(Activity activity) {
        SharedPreferences sh = activity.getSharedPreferences("dpi", 0);
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenHeight = dm.heightPixels;
        int screenWidth = dm.widthPixels;
        // 判断高分辨率低分辨率
        if (sh.getInt("dpi_type", -1) == -1) {
            if (screenHeight >= 960 && screenWidth >= 720) {
                sh.edit().putInt("dpi_type", 1).commit();
                Log.i("dpi_type", "超高分辨率");
            } else if (screenHeight >= 640 && screenWidth >= 480) {
                sh.edit().putInt("dpi_type", 2).commit();
                Log.i("dpi_type", "高分辨率");
            } else if (screenHeight >= 470 && screenWidth >= 320) {
                sh.edit().putInt("dpi_type", 3).commit();
                Log.i("dpi_type", "中分辨率");
            } else if (screenHeight >= 426 && screenWidth >= 320) {
                sh.edit().putInt("dpi_type", 4).commit();
                Log.i("dpi_type", "低分辨率");
            }  else if (screenHeight >= 426 && screenWidth >= 320) {
                sh.edit().putInt("dpi_type", 4).commit();
                Log.i("dpi_type", "低分辨率");
            }
        }
        return sh.getInt("dpi_type", -1);

    }
}
