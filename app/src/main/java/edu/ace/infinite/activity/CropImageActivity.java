package edu.ace.infinite.activity;

import static edu.ace.infinite.fragment.PersonalFragment.RESIZE_REQUEST_CODE;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.edmodo.cropper.CropImageView;

import edu.ace.infinite.R;
import edu.ace.infinite.utils.PhoneMessage;
import edu.ace.infinite.view.MyToast;


public class CropImageActivity extends BaseActivity {
    private CropImageView cropImageView;
    public static Bitmap CroppedImageBitmap; //裁剪后的Bitmap
    public static Uri uri;
    public static int[] xyScale = new int[2]; //XY比例，索引0为X，1为Y

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        BaseActivity.setStatusBarFullTransparent(getWindow());

//        Intent intent = getIntent();
        cropImageView = findViewById(R.id.cropImageView);
        RelativeLayout.LayoutParams cropImageViewLayoutParams = (RelativeLayout.LayoutParams) cropImageView.getLayoutParams();
        cropImageViewLayoutParams.topMargin = PhoneMessage.statusBarHeight + PhoneMessage.dpToPx(50);
        cropImageView.setLayoutParams(cropImageViewLayoutParams);

        Bitmap bitmap = null;
//        Uri uri = intent.getParcelableExtra("uri");
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        }catch (Exception e){
            e.printStackTrace();
        }
        cropImageView.setImageBitmap(bitmap);
        cropImageView.setAspectRatio(xyScale[0], xyScale[1]);
        cropImageView.setFixedAspectRatio(true);

        findViewById(R.id.back).setOnClickListener(v -> finish());
        Bitmap finalBitmap = bitmap;
        findViewById(R.id.affirm_btn).setOnClickListener(v -> {
            if(finalBitmap != null){
                try {
                    CroppedImageBitmap = cropImageView.getCroppedImage();
                }catch (Exception e){
                    e.printStackTrace();
                    MyToast.show("图片裁剪出错了,请重试",Toast.LENGTH_LONG,false);
                    finish();
                    return;
                }
            }else {
                CroppedImageBitmap = null;
            }
            Intent intent1 = new Intent();
            setResult(RESIZE_REQUEST_CODE, intent1);
            finish();
        });
    }
}