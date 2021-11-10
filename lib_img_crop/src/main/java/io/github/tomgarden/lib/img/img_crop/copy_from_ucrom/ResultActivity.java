package io.github.tomgarden.lib.img.img_crop.copy_from_ucrom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.yalantis.ucrop.model.AspectRatio;
import com.yalantis.ucrop.view.UCropView;

import java.io.File;
import java.io.FileOutputStream;

import io.github.tomgarden.lib.img.img_crop.ImgCrop;
import io.github.tomgarden.lib.img.img_crop.R;
import io.github.tomgarden.lib.img.img_crop.util.FileUtil;
import io.github.tomgarden.lib.img.img_crop.util.GlideUtil;
import io.github.tomgarden.lib.log.Logger;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 */
public class ResultActivity extends AppCompatActivity {

    private static final String TAG = "ResultActivity";
    private File preViewImg = null;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    /* 获取打开当前页的 Intent */
    public static Intent getIntentWithUri(@NonNull Context context, @NonNull Uri uri) {
        Intent intent = new Intent(context, ResultActivity.class);
        intent.setData(uri);
        return intent;
    }

    public static void startWithUri(@NonNull Context context, @NonNull Uri uri) {
        Intent intent = getIntentWithUri(context, uri);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_img_crop_activity_result);
        Uri uri = getIntent().getData();
        if (uri != null) {
            try {
                UCropView uCropView = findViewById(R.id.ucrop);
                uCropView.getCropImageView().setImageUri(uri, null);
                uCropView.getOverlayView().setShowCropFrame(false);
                uCropView.getOverlayView().setShowCropGrid(false);
                uCropView.getOverlayView().setDimmedColor(Color.TRANSPARENT);
            } catch (Exception e) {
                Log.e(TAG, "setImageUri", e);
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        preViewImg = new File(getIntent().getData().getPath());
        BitmapFactory.decodeFile(preViewImg.getAbsolutePath(), options);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.lib_img_crop_format_crop_result_d_d, options.outWidth, options.outHeight));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.lib_img_crop_menu_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.lib_img_crop_menu_save) {

            File destFile = ImgCrop.Companion.getInstance().getDestFile();

            Disposable disposable = Completable
                    .create(emitter -> {
                        FileOutputStream outStream = new FileOutputStream(destFile);
                        try {
                            AspectRatio aspectRatio = ImgCrop.Companion.getInstance().getDefaultAspectRatio();
                            String md5 = FileUtil.INSTANCE.md5(preViewImg);/*同名文件 glide 读取缓存无法识别到最新文件*/
                            Bitmap bitmap = GlideUtil.INSTANCE.getBitmapPX(ResultActivity.this, preViewImg, md5, aspectRatio.getAspectRatioX(), aspectRatio.getAspectRatioY());
                            boolean compressResult = bitmap.compress(ImgCrop.Companion.getInstance().getDefaultCompressFormat(), 100, outStream);
                            Logger.INSTANCE.e("compressResult = " + compressResult);

                            emitter.onComplete();
                        } catch (Throwable throwable) {
                            emitter.onError(throwable);
                        } finally {
                            outStream.flush();
                            outStream.close();
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            () -> {
                                preViewImg.delete();

                                Intent resultIntent = new Intent();
                                Uri resultUri = Uri.fromFile(destFile);
                                resultIntent.setData(resultUri);
                                setResult(Activity.RESULT_OK, resultIntent);

                                finish();
                            }, throwable -> {
                                throwable.printStackTrace();

                                preViewImg.delete();
                                setResult(Activity.RESULT_CANCELED);
                                finish();
                            }
                    );
            compositeDisposable.add(disposable);

        } else if (item.getItemId() == android.R.id.home) {
            preViewImg.delete();
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }
}
