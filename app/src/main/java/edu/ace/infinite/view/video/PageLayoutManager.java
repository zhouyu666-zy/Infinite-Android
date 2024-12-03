package edu.ace.infinite.view.video;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 仿抖音实现翻页效果
 */
public class PageLayoutManager extends LinearLayoutManager implements RecyclerView.OnChildAttachStateChangeListener {
    private int mDrift; //位移，用来判断移动方向

    private final PagerSnapHelper mPagerSnapHelper;  //实现ViewPage的翻页效果
    private OnViewPagerListener mOnViewPagerListener;  //处理页面切换回调

    public PageLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        mPagerSnapHelper = new PagerSnapHelper();
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {

        view.addOnChildAttachStateChangeListener(this);
        mPagerSnapHelper.attachToRecyclerView(view);
        super.onAttachedToWindow(view);
    }

    public void setOnViewPagerListener(OnViewPagerListener mOnViewPagerListener) {
        this.mOnViewPagerListener = mOnViewPagerListener;
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE) {  //当滑动停止时，获取当前视频的索引
            View view = mPagerSnapHelper.findSnapView(this);
            if (view != null) {
                int position = getPosition(view);
                if (mOnViewPagerListener != null) {  //倒数第二视频为底部
                    mOnViewPagerListener.onPageSelected(position, position >= getItemCount() - 5);
                }
            }
        }
    }

    //当item从窗口释放时，获取当前视频的索引
    @Override
    public void onChildViewDetachedFromWindow(@NonNull View view) {
        //暂停播放操作
        if (mDrift >= 0) {
            if (mOnViewPagerListener != null)
                mOnViewPagerListener.onPageRelease(true, getPosition(view));
        } else {
            if (mOnViewPagerListener != null)
                mOnViewPagerListener.onPageRelease(false, getPosition(view));
        }
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.mDrift = dy;
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    @Override
    public void onChildViewAttachedToWindow(@NonNull View view) {
        //播放视频操作 即将要播放的是上一个视频 还是下一个视频
//        int position = getPosition(view);
//        if (0 == position) {
//            if (mOnViewPagerListener != null) {
//                mOnViewPagerListener.onPageSelected(getPosition(view), false);
//            }
//        }
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }
}
