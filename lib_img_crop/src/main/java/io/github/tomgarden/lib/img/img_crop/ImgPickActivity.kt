package io.github.tomgarden.lib.img.img_crop

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.yalantis.ucrop.UCrop
import io.github.tomgarden.lib.img.img_crop.copy_from_ucrom.ResultActivity
import io.github.tomgarden.lib.log.Logger

/**
 * describe :
 *
 * author : tom
 *
 * time : 2021-11-03
 */
class ImgPickActivity : ComponentActivity() {

    private val imgPickResultLauncher by lazy {
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult: ActivityResult ->
            if (activityResult.resultCode != Activity.RESULT_OK) {
                finish()
                return@registerForActivityResult
            }

            val srcUri = activityResult.data?.data ?: let {
                finish()
                return@registerForActivityResult
            }

            val cropIntent = ImgCrop.getInstance().getCropIntent(this, srcUri)
            imgCropResultLauncher.launch(cropIntent)
        }
    }
    private val imgCropResultLauncher by lazy {
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult: ActivityResult ->
            if (activityResult.resultCode != Activity.RESULT_OK) {
                finish()
                return@registerForActivityResult
            }
            val resultIntent = activityResult.data ?: let {
                finish()
                return@registerForActivityResult
            }
            val resultUri: Uri = UCrop.getOutput(resultIntent) ?: let {
                finish()
                return@registerForActivityResult
            }

            imgCropConfirmLauncher.launch(ResultActivity.getIntentWithUri(this, resultUri))
        }
    }
    private val imgCropConfirmLauncher by lazy {
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult: ActivityResult ->
            if (activityResult.resultCode != Activity.RESULT_OK) {
                finish()
                return@registerForActivityResult
            }
            Logger.d("裁切后的文件位置" + activityResult.data?.data)
            ImgCrop.getInstance().pickCropDone(activityResult.data?.data)
        }
    }


    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ImgPickActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //设置 本 Activity 为 透明
        window.addFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        window.attributes.alpha = 0f

        //启动图片选择组件
        imgPickResultLauncher.launch(getImgPickIntent())
        //注册裁剪组件
        imgCropResultLauncher
        //确认剪裁后的图片结果
        imgCropConfirmLauncher
    }

    private fun getImgPickIntent(): Intent = Intent(Intent.ACTION_GET_CONTENT).also { intent ->
        intent.setType("image/*")
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        imgPickResultLauncher.unregister()
        imgCropResultLauncher.unregister()
    }

}