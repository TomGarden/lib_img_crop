package io.github.tomgarden.lib.img.img_crop

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.yalantis.ucrop.UCrop
import io.github.tomgarden.lib.img.img_crop.copy_from_ucrom.ResultActivity
import io.github.tomgarden.lib.log.Logger

/**
 * describe : 透明 , 中转页面
 *
 * author : tom
 *
 * time : 2021-11-03
 */
class ImgPickActivity : ComponentActivity() {

    /*用于打开图片选择页面 , 并监听结果*/
    private val imgPickResultLauncher by lazy {
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult: ActivityResult ->
            if (activityResult.resultCode != Activity.RESULT_OK) {
                toast(R.string.lib_img_crop_img_pick_cancel)
                this@ImgPickActivity.selfFinish()
                return@registerForActivityResult
            }

            val srcUri = activityResult.data?.data ?: let {
                toast(R.string.lib_img_crop_img_pick_invalid)
                this@ImgPickActivity.selfFinish()
                return@registerForActivityResult
            }

            val cropIntent = ImgCrop.getInstance().getCropIntent(this, srcUri)
            imgCropResultLauncher.launch(cropIntent)
        }
    }

    /*用于打开图片裁切页面 , 并监听结果*/
    private val imgCropResultLauncher by lazy {
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult: ActivityResult ->
            if (activityResult.resultCode != Activity.RESULT_OK) {
                toast(R.string.lib_img_crop_img_crop_cancle)
                this@ImgPickActivity.selfFinish()
                return@registerForActivityResult
            }
            val resultIntent = activityResult.data ?: let {
                toast(R.string.lib_img_crop_img_crop_invalid)
                this@ImgPickActivity.selfFinish()
                return@registerForActivityResult
            }
            val resultUri: Uri = UCrop.getOutput(resultIntent) ?: let {
                toast(R.string.lib_img_crop_img_crop_invalid)
                this@ImgPickActivity.selfFinish()
                return@registerForActivityResult
            }

            val intent = ResultActivity.getIntentWithUri(this, resultUri)
            imgCropConfirmLauncher.launch(intent)
        }
    }

    /*用于打开剪裁确认页面 , 并监听结果*/
    private val imgCropConfirmLauncher by lazy {
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult: ActivityResult ->
            if (activityResult.resultCode != Activity.RESULT_OK) {
                toast(R.string.lib_img_crop_img_crop_confirm_abandon)
                selfFinish()
                return@registerForActivityResult
            }
            Logger.d("裁切后的文件位置" + activityResult.data?.data)
            activityResult.data?.data?.let { uri ->
                ImgCrop.getInstance().pickCropDone(uri)
            } ?: toast(R.string.lib_img_crop_img_crop_get_crop_result_failed)
            this@ImgPickActivity.selfFinish()
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

    private fun toast(strId: Int) = Toast.makeText(this, strId, Toast.LENGTH_SHORT).show()

    private fun selfFinish() {
        ImgCrop.clearInstance()
        finish()
    }
}