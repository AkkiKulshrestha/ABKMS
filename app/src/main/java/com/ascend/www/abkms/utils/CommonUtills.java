package com.ascend.www.abkms.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by sspluser7 on 15-02-2018.
 */

public class CommonUtills {
    public static float getScreenWidth(Context context) {
        float width = (float) 360.0;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels / displayMetrics.density;
        return width;
    }





}
