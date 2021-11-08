package io.github.tomgarden.lib.img.img_crop.copy_from_ucrom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.yalantis.ucrop.view.UCropView;

import java.io.File;

import io.github.tomgarden.lib.img.img_crop.R;

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 */
public class ResultActivity extends AppCompatActivity {

    private static final String TAG = "ResultActivity";
    private File preViewImg = null;

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
            setResult(Activity.RESULT_OK, getIntent());
            finish();
        } else if (item.getItemId() == android.R.id.home) {
            preViewImg.delete();
            setResult(Activity.RESULT_CANCELED);
            finish();
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
