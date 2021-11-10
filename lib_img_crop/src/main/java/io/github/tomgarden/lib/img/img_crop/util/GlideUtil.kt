package io.github.tomgarden.lib.img.img_crop.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.TypedValue
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import io.github.tomgarden.lib.log.Logger
import java.io.File

/**
 * describe :
 *
 * author : tom
 *
 * time : 2021-11-08
 */
object GlideUtil {

    fun dp2px(context: Context, dp: Float): Float {
        val r: Resources = context.resources
        val px: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.displayMetrics)
        return px
    }

    /*fun showUrlImg(imageView: ImageView, url: Uri, w_dp: Float, h_dp: Float) {
        val w: Float = dp2px(imageView.context, w_dp)
        val h: Float = dp2px(imageView.context, h_dp)
        val placeHolder = R.drawable.lib_beaker_ic_outline_image_24
        val requestOptions: RequestOptions = RequestOptions()
            .error(placeHolder)
            .placeholder(placeHolder)
            .override(w.toInt(), h.toInt())
        Glide.with(imageView.context).load(url).apply(requestOptions).into(imageView)
    }*/

    fun showImgById(imageView: ImageView, id: Int) = Glide.with(imageView).load(id).into(imageView)
    fun showImgByUri(imageView: ImageView, uri: Uri, md5: String?) {
        Logger.e("MD5 == $md5")

        var builder: RequestBuilder<*> = Glide.with(imageView).load(uri)
        builder = if (!md5.isNullOrEmpty()) {
            builder.signature(ObjectKey(md5))
        } else {
            builder
        }
        builder.into(imageView)
    }

    fun getBitmapDP(context: Context, imgId: Int, w_dp: Float? = null, h_dp: Float? = null): Bitmap? = when {
        (imgId == -1) -> null
        else -> getBitmapDP(context, { rb: RequestBuilder<Bitmap> -> rb.load(imgId) }, w_dp, h_dp)
    }

    fun getBitmapDP(context: Context, filePath: String?, w_dp: Float? = null, h_dp: Float? = null): Bitmap? = when {
        filePath.isNullOrEmpty() -> null
        else -> getBitmapDP(context, { rb: RequestBuilder<Bitmap> -> rb.load(filePath) }, w_dp, h_dp)
    }

    fun getBitmapDP(context: Context, file: File?, w_dp: Float? = null, h_dp: Float? = null): Bitmap? = when {
        file == null -> null
        else -> getBitmapDP(context, { rb: RequestBuilder<Bitmap> -> rb.load(file) }, w_dp, h_dp)
    }

    fun getDrawableDP(context: Context, imgId: Int, w_dp: Float? = null, h_dp: Float? = null): Drawable? = when {
        (imgId == -1) -> null
        else -> getDrawableDP(context, { rb: RequestBuilder<Drawable> -> rb.load(imgId) }, w_dp, h_dp)
    }

    fun getDrawableDP(context: Context, filePath: String?, w_dp: Float? = null, h_dp: Float? = null): Drawable? = when {
        filePath.isNullOrEmpty() -> null
        else -> getDrawableDP(context, { rb: RequestBuilder<Drawable> -> rb.load(filePath) }, w_dp, h_dp)
    }

    private fun getDrawableDP(context: Context, from: Function1<RequestBuilder<Drawable>, RequestBuilder<Drawable>>, w_dp: Float? = null, h_dp: Float? = null): Drawable? =
        getDP<Drawable>(context, { rm: RequestManager -> rm.asDrawable().let(from) }, w_dp, h_dp)

    private fun getBitmapDP(context: Context, from: Function1<RequestBuilder<Bitmap>, RequestBuilder<Bitmap>>, w_dp: Float? = null, h_dp: Float? = null): Bitmap? =
        getDP<Bitmap>(context, { rm: RequestManager -> rm.asBitmap().let(from) }, w_dp, h_dp)

    fun getBitmapPX(context: Context, imgId: Int, w_px: Float? = null, h_px: Float? = null): Bitmap? = when {
        (imgId == -1) -> null
        else -> getBitmapPX(context, { rb: RequestBuilder<Bitmap> -> rb.load(imgId) }, w_px, h_px)
    }

    fun getBitmapPX(context: Context, filePath: String?, w_px: Float? = null, h_px: Float? = null): Bitmap? = when {
        filePath.isNullOrEmpty() -> null
        else -> getBitmapPX(context, { rb: RequestBuilder<Bitmap> -> rb.load(filePath) }, w_px, h_px)
    }

    fun getBitmapPX(context: Context, file: File?, w_px: Float? = null, h_px: Float? = null): Bitmap? =
        getBitmapPX(context, file, null, w_px, h_px)

    fun getBitmapPX(context: Context, file: File?, signKey: Any? = null, w_px: Float? = null, h_px: Float? = null): Bitmap? = when {
        file == null -> null
        else -> getBitmapPX(context, { rb: RequestBuilder<Bitmap> ->
            val requestBuilder: RequestBuilder<Bitmap> = rb.load(file)
            signKey?.let { requestBuilder.signature(ObjectKey(signKey)) }
            requestBuilder
        }, w_px, h_px)
    }

    fun getDrawablePX(context: Context, imgId: Int, w_px: Float? = null, h_px: Float? = null): Drawable? = when {
        (imgId == -1) -> null
        else -> getDrawablePX(context, { rb: RequestBuilder<Drawable> -> rb.load(imgId) }, w_px, h_px)
    }

    fun getDrawablePX(context: Context, filePath: String?, w_px: Float? = null, h_px: Float? = null): Drawable? = when {
        filePath.isNullOrEmpty() -> null
        else -> getDrawablePX(context, { rb: RequestBuilder<Drawable> -> rb.load(filePath) }, w_px, h_px)
    }

    private fun getDrawablePX(context: Context, from: Function1<RequestBuilder<Drawable>, RequestBuilder<Drawable>>, w_px: Float? = null, h_px: Float? = null): Drawable? =
        getPX<Drawable>(context, { rm: RequestManager -> rm.asDrawable().let(from) }, w_px, h_px)

    private fun getBitmapPX(context: Context, from: Function1<RequestBuilder<Bitmap>, RequestBuilder<Bitmap>>, w_px: Float? = null, h_px: Float? = null): Bitmap? =
        getPX<Bitmap>(context, { rm: RequestManager -> rm.asBitmap().let(from) }, w_px, h_px)

    private fun <T> getDP(context: Context, from: Function1<RequestManager, RequestBuilder<T>>, w_dp: Float? = null, h_dp: Float? = null): T? {
        return if (w_dp != null && h_dp != null) {
            val w_px: Float = dp2px(context, w_dp)
            val h_px: Float = dp2px(context, h_dp)
            getPX(context, from, w_px, h_px)
        } else {
            getPX(context, from, w_px = null, h_px = null)
        }
    }

    private fun <T> getPX(context: Context, from: Function1<RequestManager, RequestBuilder<T>>, w_px: Float? = null, h_px: Float? = null): T? {
        val requestOptions: RequestOptions? = if (w_px != null && h_px != null) {
            RequestOptions().override(w_px.toInt(), h_px.toInt())
        } else {
            null
        }

        return try {
            Glide.with(context)
                .let(from)
                .let { requestBuilder -> requestOptions?.let { requestBuilder.apply(it) } ?: requestBuilder }
                .submit()
                .get()
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            null
        }
    }

}