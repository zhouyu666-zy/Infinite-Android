package edu.ace.infinite.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class CircleImage extends androidx.appcompat.widget.AppCompatImageView {
    //画笔
    private Paint mPaint;
    //圆形图片的半径
    private int mRadius;
    //图片的宿放比例
    private float mScale;

    public CircleImage(Context context) {
        super(context);
    }

    public CircleImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //测量 View 大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //由于是圆形，宽高应保持一致，取宽高中的最小值赋值给 size
        int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        //圆形的半径为 size 的一半
        mRadius = size / 2;
        //确认控件的大小
        setMeasuredDimension(size, size);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        //实例化画笔对象
        mPaint = new Paint();
        //获取绘图类
        Drawable drawable = getDrawable();
        if (null != drawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            //初始化 BitmapShader，传入 bitmap 对象
            BitmapShader bitmapShader = new BitmapShader(bitmap,
                    Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            //计算缩放比例
            mScale = (mRadius * 2.0f) / Math.min(bitmap.getHeight(),
                    bitmap.getWidth());
            Matrix matrix = new Matrix();
            matrix.setScale(mScale, mScale);
            bitmapShader.setLocalMatrix(matrix);
            mPaint.setShader(bitmapShader);
            //画圆形，指定好坐标，半径，画笔
            canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        } else {
            super.onDraw(canvas);
        }
    }
}