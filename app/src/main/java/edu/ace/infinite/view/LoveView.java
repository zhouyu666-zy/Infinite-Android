package edu.ace.infinite.view;

import static com.bumptech.glide.load.resource.drawable.DrawableDecoderCompat.getDrawable;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import java.util.Random;

import edu.ace.infinite.R;
import edu.ace.infinite.utils.PhoneMessage;

public class LoveView extends RelativeLayout {
    private Context mContext;
    private final float[] num = {-30, -20, 0, 20, 30};//随机心形图片角度

    public LoveView(Context context) {
        super(context);
        initView(context);
    }

    public LoveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LoveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
    }

    public void addLoveView(MotionEvent event){
        ImageView imageView = new ImageView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                PhoneMessage.dpToPx(120), PhoneMessage.dpToPx(120)); //爱心高宽
        //根据点击位置确定爱心显示位置
        params.leftMargin = (int) event.getX() - PhoneMessage.dpToPx(90);
        params.topMargin = (int) event.getY() - PhoneMessage.dpToPx(90);
        imageView.setImageResource(R.drawable.like_icon_y);
        imageView.setLayoutParams(params);
        addView(imageView);
        AnimatorSet animatorSet = new AnimatorSet();
        float[] num = {-35, -20, 0, 20, 35};//随机心形图片角度
        animatorSet.play(scale(imageView, "scaleX", 2f, 0.9f, 100, 0))
                .with(scale(imageView, "scaleY", 2f, 0.9f, 100, 0))
                .with(rotation(imageView, 0, 0, num[new Random().nextInt(4)]))
                .with(alpha(imageView, 0, 1, 100, 0))
                .with(scale(imageView, "scaleX", 0.9f, 1, 50, 150))
                .with(scale(imageView, "scaleY", 0.9f, 1, 50, 150))
                .with(translationY(imageView, 0, -1200, 800, 400))
                .with(alpha(imageView, 1, 0, 300, 400))
                .with(scale(imageView, "scaleX", 1, 3f, 700, 400))
                .with(scale(imageView, "scaleY", 1, 3f, 700, 400));
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                removeViewInLayout(imageView);
            }
        });
    }


    public static ObjectAnimator scale(View view, String propertyName, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , propertyName
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }

    public static ObjectAnimator translationX(View view, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , "translationX"
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }

    public static ObjectAnimator translationY(View view, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , "translationY"
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }

    public static ObjectAnimator alpha(View view, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , "alpha"
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }

    public static ObjectAnimator rotation(View view, long time, long delayTime, float... values) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", values);
        rotation.setDuration(time);
        rotation.setStartDelay(delayTime);
        rotation.setInterpolator(input -> input);
        return rotation;
    }
}
