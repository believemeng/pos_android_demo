package com.dspread.pos.ui.printer;

import android.app.Activity;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

public class PrinterItemViewModel {
    @StringRes
    public final int titleId;
    @DrawableRes
    public final int iconResId;
    public final Class<? extends Activity> activityClass;

    public PrinterItemViewModel(int titleId, int iconResId, Class<? extends Activity> activityClass) {
        this.titleId = titleId;
        this.iconResId = iconResId;
        this.activityClass = activityClass;
    }
}