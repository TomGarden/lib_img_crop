package io.github.tomgarden.lib.img.img_crop

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import com.yalantis.ucrop.view.CropImageView
import io.github.tomgarden.lib.img.img_crop.copy_from_ucrom.UCropActivity
import io.github.tomgarden.lib.log.Logger
import java.io.File

/**
 * describe :
 *
 * author : tom
 *
 * time : 2021-11-03
 */
class ImgCrop private constructor() {
    private val tempCroppedImageName = "TempCropImage"
    private var pickCropResult: Function1<Uri, Unit>? = null
    val defaultCompressFormat = Bitmap.CompressFormat.JPEG

    /*指定文件路径和文件名会造成新文件覆盖旧文件的效果 , 可以节省存储空间*/
    private var destPath: String? = null
    private var destFileName: String? = null
    var defaultAspectRatio: AspectRatio = AspectRatio("1:1", 1f, 1f)
        private set

    companion object {
        private var INSTANCE: ImgCrop? = null

        fun getInstance(): ImgCrop {
            return INSTANCE ?: let {
                synchronized(ImgCrop::class.java) {
                    INSTANCE ?: let {
                        ImgCrop().also { INSTANCE = it }
                    }
                }
            }
        }
    }

    fun pickImg(context: Context): ImgCrop {
        ImgPickActivity.start(context)
        return this
    }

    /** 获取裁切视图 intent ; 用于打开裁切页面 */
    internal fun getCropIntent(context: Context, srcUri: Uri): Intent {
        val path = destPath ?: context.cacheDir.name
        return getCropIntent(context, srcUri, path)
    }

    private fun getCropIntent(context: Context, srcUri: Uri, destPath: String): Intent {
        val destinationFileName: String = "$tempCroppedImageName.jpg"

        var uCrop: UCrop = UCrop.of(srcUri, Uri.fromFile(File(destPath, destinationFileName)))
        uCrop = advancedConfig(context, uCrop)

        val intent = uCrop.getIntent(context)
        intent.setClass(context, UCropActivity::class.java)
        return intent
    }

    /**
     * Sometimes you want to adjust more options, it's done via [com.yalantis.ucrop.UCrop.Options] class.
     *
     * @param uCrop - ucrop builder instance
     * @return - ucrop builder instance
     */
    private fun advancedConfig(context: Context, uCrop: UCrop): UCrop {
        val options = UCrop.Options()

        options.setCompressionFormat(defaultCompressFormat)     /*压缩后的文件格式 , 默认 jpg*/
        options.setCompressionQuality(90)                       /*压缩后的文件质量 , 默认 90*/
        options.setHideBottomControls(false)                    /*隐藏裁剪页面底部面板 , 默认 false*/
        options.setFreeStyleCropEnabled(false)                  /*裁切框尺寸是否随意调整 , 默认 false , 支持调整则手势事件有冲突 , 需要摸索才能学会用法*/

        /*
        If you want to configure how gestures work for all UCropActivity tabs

        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        * */

        /*
        This sets max size for bitmap that will be decoded from source Uri.
        More size - more memory allocation, default implementation uses screen diagonal.

        options.setMaxBitmapSize(640);
        * */

        /*

        Tune everything (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧

        options.setMaxScaleMultiplier(5);
        options.setImageToCropBoundsAnimDuration(666);
        options.setDimmedLayerColor(Color.CYAN);
        options.setCircleDimmedLayer(true);
        options.setShowCropFrame(false);
        options.setCropGridStrokeWidth(20);
        options.setCropGridColor(Color.GREEN);
        options.setCropGridColumnCount(2);
        options.setCropGridRowCount(1);
        options.setToolbarCropDrawable(R.drawable.your_crop_icon);
        options.setToolbarCancelDrawable(R.drawable.your_cancel_icon);

        // Color palette
        options.setToolbarColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.your_color_res));
       */

        // Aspect ratio options
        val array: MutableList<AspectRatio> = mutableListOf(
            AspectRatio("3:4", 3f, 4f),
            AspectRatio(context.getString(R.string.lib_img_crop_label_original), CropImageView.DEFAULT_ASPECT_RATIO, CropImageView.DEFAULT_ASPECT_RATIO),
            AspectRatio("3:2", 3f, 2f),
            AspectRatio("16:9", 16f, 9f),
        )
        if (defaultAspectRatio.aspectRatioX == defaultAspectRatio.aspectRatioY) {
            array.add(0, defaultAspectRatio)
        } else {
            array.add(0, AspectRatio("1:1", 1f, 1f))
            array.add(0, defaultAspectRatio)
        }
        options.setAspectRatioOptions(0, *array.toTypedArray())
        return uCrop.withOptions(options)
    }

    fun pickCropResult(pickCropResult: Function1<Uri, Unit>?): ImgCrop {
        this.pickCropResult = pickCropResult
        return this
    }

    /**选择并且裁剪完成*/
    internal fun pickCropDone(resultUri: Uri) {
        Logger.e("剪裁结果 : $resultUri")
        pickCropResult?.invoke(resultUri)
    }

    fun defaultAspectRatio(context: Context, pair: Pair<Float, Float>?): ImgCrop {
        pair?.let { defaultAspectRatio(context, it.first, it.second) }
        return this
    }

    fun defaultAspectRatio(context: Context, aspectRatioX: Float, aspectRatioY: Float): ImgCrop {
        defaultAspectRatio = AspectRatio(context.getString(R.string.lib_img_crop_label_default_aspect_ratio), aspectRatioX, aspectRatioY)
        return this
    }

    fun destPath(destPath: String): ImgCrop {
        this.destPath = destPath
        return this
    }

    fun destFileName(destFileName: String): ImgCrop {
        this.destFileName = destFileName
        return this
    }

    fun getDestFile(): File = File(getUnNullDestPath(), getUnNullDestName())
    fun getDestFilePath() = getDestFile().path
    fun getDestUri() = Uri.fromFile(getDestFile())

    private fun getUnNullDestPath(): String = destPath.let { destPath ->
        when {
            destPath.isNullOrEmpty() -> throw RuntimeException("destPath isNullOrEmpty()")
            else -> destPath
        }
    }

    private fun getUnNullDestName(): String = destFileName.let { destFileName ->
        when {
            destFileName.isNullOrEmpty() -> throw RuntimeException("destFileName isNullOrEmpty()")
            else -> "$destFileName.jpg"
        }
    }
}