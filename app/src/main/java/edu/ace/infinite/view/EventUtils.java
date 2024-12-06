package edu.ace.infinite.view;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.EventLog;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.nio.file.attribute.FileAttribute;

import edu.ace.infinite.utils.ConsoleUtils;
import edu.ace.infinite.utils.PhoneMessage;

public class EventUtils {
    /**
     * 自定义双击单击判断
     */
    public static class OnDoubleClickListener implements View.OnTouchListener{
        private boolean firstClick = false;
        private final Handler handler = new Handler(Looper.getMainLooper());
        /**
         * 两次点击时间间隔，单位毫秒
         */
        private final int totalTime = 300;
        /**
         * 自定义回调接口
         */
        private final DoubleClickCallback mCallback;
        private final GestureDetector gestureDetector;
        private boolean isLongPress = false;
        public interface DoubleClickCallback {
            void onDoubleClick(MotionEvent event);
            void onClick(MotionEvent event);

            default boolean onTouch(View view, MotionEvent event) {
                return true;
            }

            void onLongPress(MotionEvent event);
            void onLongPressFinish(MotionEvent event);
        }
        public OnDoubleClickListener(DoubleClickCallback callback, Context context) {
            this.mCallback = callback;
            this.gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);
                    isLongPress = true;
                    mCallback.onLongPress(e);
                }
            });

        }

        private final Handler handler_double = new Handler(Looper.getMainLooper());
        /**
         * 触摸事件处理
         */
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            if (MotionEvent.ACTION_UP == event.getAction()) {//按下抬起
                if(firstClick){
                    handler.removeCallbacksAndMessages(null);
                    if (mCallback != null) {
                        mCallback.onDoubleClick(event);
                    }
                    handler_double.removeCallbacksAndMessages(null);
                    handler_double.postDelayed(() -> firstClick = false,300);
                    return true;
                }
                firstClick = true;

                if(isLongPress){
                    mCallback.onLongPressFinish(event);
                    isLongPress = false;
                    firstClick = false;
                }else {
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(() -> {
                        if (mCallback != null) {
                            mCallback.onClick(event);
                        }
                        firstClick = false;
                        handler.removeCallbacksAndMessages(null);
                    },totalTime);
                }
            }
            return mCallback.onTouch(v,event);
        }
    }


    public static class OnMoreEventListener implements View.OnTouchListener{

        /**
         * 自定义回调接口
         */
        private final MoreEventCallback mEventCallback;
        public interface MoreEventCallback {
            void onDoubleClick(MotionEvent event);
            void onClick();
            void onLongPress();
            void onLongPressFinish();

            default void longPressFinishLeft() {

            }

            default void longPressFinishRight() {

            }

            default boolean onTouch(View view, MotionEvent event) {
                return true;
            }

            default void onMoveLeftUp() {

            }

            default void onMoveRightUp() {

            }

            default void onMoveRightDown() {

            }

            default void onMoveLeftDown() {

            }
        }
        public OnMoreEventListener(MoreEventCallback callback) {
            super();
            this.mEventCallback = callback;
        }

        boolean isVertical = false; //纵向滑动
        boolean isHorizontal = false; //横向滑动
        private float startX = 0; //手指按下时的X坐标
        private float startY = 0; //手指按下时的Y坐标
        private float lastCalcX = 0;
        private float lastCalcY = 0;
        private static final int MAX_LONG_PRESS_TIME = 400;// 长按/双击最长等待时间
        private static final int MAX_SINGLE_CLICK_TIME = 420;// 单击后等待的时间
        private static final int MIN_DISTANCE = 3; //最小滑动距离
        private int mClickCount;// 点击次数
        private int mDownX;
        private int mDownY;
        private long mLastDownTime;
        private long mFirstClick;
        private long mSecondClick;
        private boolean isDoubleClick = false;
        private final Handler mBaseHandler = new Handler(Looper.getMainLooper());
        private boolean isUp;

        @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:{
                    float x = event.getX();
                    float y = event.getY();
                    isLongPress = false;
                    startX = x;
                    startY = y;
                    lastCalcX = x;
                    lastCalcY = y;
                    mLastDownTime = System.currentTimeMillis();
                    mDownX = (int) x;
                    mDownY = (int) y;
                    mClickCount++;
                    if (mSingleClickTask != null) {
                        mBaseHandler.removeCallbacks(mSingleClickTask);
                    }
                    if(!isDoubleClick){
                        longPressPositionX = x;
                        isUp = false;
                        mBaseHandler.postDelayed(mLongPressTask,MAX_LONG_PRESS_TIME);
                    }
                    if (1 == mClickCount) {
                        mFirstClick = System.currentTimeMillis();
                    }else if(mClickCount >= 2) {// 双击
                        mSecondClick = System.currentTimeMillis();
                        if (mSecondClick - mFirstClick <= MAX_LONG_PRESS_TIME) {
                            //处理双击
                            mDoubleClickTask(event);
                        }
                    }
                    break;
                }
                case MotionEvent.ACTION_MOVE:{
                    float x = event.getX();
                    float y = event.getY();
                    int mMoveX = (int) x;
                    int mMoveY = (int) y;
                    int absMx = Math.abs(mMoveX - mDownX);
                    int absMy = Math.abs(mMoveY - mDownY);
                    if (absMx > MIN_DISTANCE || absMy > MIN_DISTANCE) {
                        mBaseHandler.removeCallbacks(mLongPressTask);
                        mBaseHandler.removeCallbacks(mSingleClickTask);
                        isDoubleClick = false;
                        mClickCount = 0;//移动了
                    }

                    if (absMx > MIN_DISTANCE || absMy > MIN_DISTANCE) {
                        mBaseHandler.removeCallbacks(mLongPressTask);
                        mBaseHandler.removeCallbacks(mSingleClickTask);
                        isDoubleClick = false;
                        mClickCount = 0;//移动了

                        float distanceY;
                        boolean isUP = false;
                        if(y > lastCalcY){
                            distanceY = y - lastCalcY;
                        }else {
                            distanceY = lastCalcY - y;
                            isUP = true;
                        }

                        float distanceX;
                        boolean isRight = false;
                        if(x > lastCalcX){
                            distanceX = x - lastCalcX;
                            isRight = true;
                        }else {
                            distanceX = lastCalcX - x;
                        }
//                            float distanceX = lastx - x;
//                            float absDistanceY = Math.abs(distanceY);
//                            float absDistanceX = Math.abs(distanceX);
                        if(!isVertical && !isHorizontal){ //判断本次滑动是横向还是纵向
                            if(distanceX > PhoneMessage.dpToPx(10)){
                                isHorizontal = true;
                            }else if(distanceY > PhoneMessage.dpToPx(5)){
                                isVertical = true;
                            }
                        }


                        //横向滑动
                        if(isHorizontal){
                            int maxSize = PhoneMessage.getWidthPixels();
                            int one = maxSize / 1000;
                            int changePosition = (int) (distanceX / one) * 150;
                            if(distanceX >= one){
                                lastCalcX = x;
                            }
                        }
                        if(isVertical){
                            //竖向滑动
                            int maxSize = PhoneMessage.getHeightPixels() / 2;
                            int one = maxSize / 100;
                            if (startX > (float) v.getWidth() / 2) { //起始位置在屏幕右边（改变音量）
                                if(distanceY >= one){
                                    if(isUP){
                                        //上滑
                                        mEventCallback.onMoveRightUp();
                                    }else {
                                        //下滑
                                        mEventCallback.onMoveRightDown();
                                    }
                                    lastCalcY = y;
                                }
                            } else { //起始位置在屏幕左边（改变亮度）
                                if(distanceY >= one){
                                    if(isUP){
                                        mEventCallback.onMoveLeftUp();
                                    }else {
                                        mEventCallback.onMoveLeftDown();
                                    }
                                    lastCalcY = y;
                                }
                            }
                        }
                    }
                    break;
                }
                case MotionEvent.ACTION_UP:{
                    ConsoleUtils.logErr("UP");
                    isHorizontal = false;
                    isVertical = false;
                    float x = event.getX();
                    float y = event.getY();
                    isUp = true;
                    if(isLongPress){
                        longPressFinish();
                        return true;
                    }
                    long mLastUpTime = System.currentTimeMillis();
                    int mUpX = (int) x;
                    int mUpY = (int) y;
                    int mx = Math.abs(mUpX - mDownX);
                    int my = Math.abs(mUpY - mDownY);
                    if (mx <= MIN_DISTANCE && my <= MIN_DISTANCE) {
                        if((mLastUpTime - mLastDownTime) <= MAX_LONG_PRESS_TIME) {
                            mBaseHandler.removeCallbacks(mLongPressTask);
                            if (!isDoubleClick){
                                mBaseHandler.postDelayed(mSingleClickTask,MAX_SINGLE_CLICK_TIME);
                            }
                        }else{
                            //超出了双击间隔时间
                            mClickCount = 0;
                        }
                    } else{
                        //移动了
                        mClickCount = 0;
                    }
                    if(isDoubleClick){
                        isDoubleClick = false;
                    }
                    break;
                }
            }
            return mEventCallback.onTouch(v,event);
        }

        private final Runnable mSingleClickTask = new Runnable() {
            @Override
            public void run() {
                // 处理单击
                if(isDoubleClick){
                    return;
                }
                mClickCount =0;
                mEventCallback.onClick();
            }
        };

        private void mDoubleClickTask(MotionEvent event){
            //处理双击
            isDoubleClick=true;
            mClickCount = 0;
            mFirstClick = 0;
            mSecondClick = 0;
            mBaseHandler.removeCallbacks(mSingleClickTask);
            mBaseHandler.removeCallbacks(mLongPressTask);
            mEventCallback.onDoubleClick(event);
        }

        private float longPressPositionX; //长按位置
        private boolean isLongPress;
        private boolean isLongPressLeft;
        private ObjectAnimator right_speed_view_animate;
        private ObjectAnimator left_speed_view_animate;
        private final Runnable mLongPressTask =  new Runnable() {
            @Override
            public void run() {
                try {
                    if(isUp){
                        return;
                    }
                    //处理长按
                    mClickCount = 0;
                    isLongPress = true;
                    mEventCallback.onLongPress();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        private void longPressFinish(){
            isLongPress = false;
            //长按结束
            if(isLongPressLeft){
                mEventCallback.longPressFinishLeft();
            }else {
                mEventCallback.longPressFinishRight();
            }
            mEventCallback.onLongPressFinish();
        }
    }
}
