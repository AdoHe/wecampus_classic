package com.westudio.wecampus.ui.base;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.westudio.wecampus.R;
import com.westudio.wecampus.net.WeCampusApi;
import com.westudio.wecampus.util.Constants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by martian on 13-11-19.
 */
public class ImageDetailActivity extends SherlockFragmentActivity {

    public static final String KEY_IMAGE_URL = "image_url";
    public static final String KEY_EXTRA_INFO = "extra_info";
    public static final String KEY_EXTRA_SEX = "extra_sex";

    private ImageView image;
    private PhotoViewAttacher attacher;

    private String url;
    private String extraInfo;
    private String extraSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        url = getIntent().getStringExtra(KEY_IMAGE_URL);
        extraInfo = getIntent().getStringExtra(KEY_EXTRA_INFO);
        extraSex = getIntent().getStringExtra(KEY_EXTRA_SEX);

        updateActionBar();

        image = (ImageView) findViewById(R.id.image);
        attacher = new PhotoViewAttacher(image);

        final Drawable defaultDrawable = new ColorDrawable(Color.rgb(229, 255, 255));
        final Drawable defaultMaleDrawable = getResources().getDrawable(R.drawable.ic_default_male);
        final Drawable defaultFeMaleDrawable = getResources().getDrawable(R.drawable.ic_default_female);

        if(Constants.IMAGE_NOT_FOUND.equals(url)) {
            if(extraSex != null && Constants.MALE.equals(extraSex)) {
                image.setImageDrawable(defaultMaleDrawable);
                attacher.update();
            } else if(extraSex != null & Constants.FEMALE.equals(extraSex)) {
                image.setImageDrawable(defaultFeMaleDrawable);
                attacher.update();
            }
        } else {
            WeCampusApi.requestImage(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    if (imageContainer.getBitmap() != null) {
                        image.setImageBitmap(imageContainer.getBitmap());
                        attacher.update();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    image.setImageDrawable(defaultDrawable);
                    attacher.update();
                }
            });
        }
    }

    private void updateActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_image_bg));
        String title = "<font color=\"#999999\">" + extraInfo + "</font>";
        getSupportActionBar().setTitle(Html.fromHtml(title));
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.image_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                break;
            case R.id.detail_image_save: {
                WeCampusApi.requestImage(url, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                        Bitmap bm = imageContainer.getBitmap();
                        if (bm != null) {
                            saveBmToFile(bm);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), R.string.msg_img_save_fail, Toast.LENGTH_SHORT).show();
                    }
                });


                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public static void copyFile(File sourceFile,File targetFile)
            throws IOException {
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff=new BufferedInputStream(input);

        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff=new BufferedOutputStream(output);

        byte[] b = new byte[1024 * 5];
        int len;
        while ((len =inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        outBuff.flush();

        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }

    private void saveBmToFile(Bitmap bitmap) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File ext = Environment.getExternalStorageDirectory();
            File downloadFileDir = new File(ext, "wecampus");
            if(!downloadFileDir.exists()) {
                downloadFileDir.mkdir();
            }
            File dstFile = new File(downloadFileDir, "p" + System.currentTimeMillis() + ".jpg");
            try {
                FileOutputStream fos = new FileOutputStream(dstFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
                Toast.makeText(getApplicationContext(), R.string.msg_img_save_success, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.msg_img_save_fail, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.text_no_sdcard, Toast.LENGTH_SHORT).show();
        }
    }
}
