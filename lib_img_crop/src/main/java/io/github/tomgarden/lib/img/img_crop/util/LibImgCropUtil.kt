package io.github.tomgarden.lib.img.img_crop.util

import android.content.Context

/**
 * describe :
 *
 * author : tom
 *
 * time : 2021-11-10
 */
object LibImgCropUtil {
    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

}