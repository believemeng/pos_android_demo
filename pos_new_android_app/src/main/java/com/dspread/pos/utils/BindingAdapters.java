package com.dspread.pos.utils;

import android.widget.ImageView;
import androidx.databinding.BindingAdapter;

public class BindingAdapters {
    @BindingAdapter("app:tint")
    public static void setImageTint(ImageView view, int color) {
        view.setImageTintList(android.content.res.ColorStateList.valueOf(color));
    }
}