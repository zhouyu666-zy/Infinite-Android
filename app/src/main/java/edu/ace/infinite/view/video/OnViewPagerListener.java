package edu.ace.infinite.view.video;

public interface OnViewPagerListener {
    //页面被划走
    void onPageRelease(boolean isNext, int position);

    /*选中的监听以及判断是否滑动到底部*/
    //页面被选中
    void onPageSelected(int position, boolean isBottom);
}