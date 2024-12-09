package edu.ace.infinite.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import edu.ace.infinite.R;
import edu.ace.infinite.adapter.VideoAdapter;
import edu.ace.infinite.fragment.MessageFragment;
import edu.ace.infinite.fragment.PersonalFragment;
import edu.ace.infinite.fragment.RecommendVideoFragment;
import edu.ace.infinite.fragment.personalfragment.FavoritesFragment;
import edu.ace.infinite.fragment.personalfragment.LikesFragment;
import edu.ace.infinite.fragment.personalfragment.WorksFragment;
import edu.ace.infinite.utils.ConsoleUtils;
import edu.ace.infinite.utils.PhoneMessage;
import edu.ace.infinite.view.CustomViewPager;
import edu.ace.infinite.view.MyToast;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends BaseActivity {

    private List<Fragment> fragmentList;
    private CustomViewPager view_pager;
    private SmoothBottomBar smoothBottomBar;
    private DrawerLayout drawerLayout;
    private boolean isOpenDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);


        initView();
        initDrawerLayout();
    }

    private void initDrawerLayout() {
        LinearLayout left_view = findViewById(R.id.main_drawer_view);
        left_view.setPadding(0, PhoneMessage.statusBarHeight,0,0);
        ViewGroup.LayoutParams leftViewLayoutParams = left_view.getLayoutParams();
        leftViewLayoutParams.width = (int) (PhoneMessage.getWidthPixels() * 0.7);
        left_view.setLayoutParams(leftViewLayoutParams);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // 实现整个 Activity 左移效果
                // 当侧滑菜单滑动时，移动主内容
                View content = drawerLayout.getChildAt(0);
                float distance = drawerView.getWidth() * slideOffset;
                content.setTranslationX(-distance);
            }
            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                isOpenDrawerLayout = true;
            }
            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                isOpenDrawerLayout = false;
            }
            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    private void initView() {
        view_pager = findViewById(R.id.main_view_pager);

        fragmentList = new ArrayList<>();
        RecommendVideoFragment recommendVideoFragment = new RecommendVideoFragment(this);
        fragmentList.add(recommendVideoFragment);
        fragmentList.add(new MessageFragment());
        fragmentList.add(new PersonalFragment());

        //设置预加载页数
        view_pager.setOffscreenPageLimit(fragmentList.size());

        view_pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT) {
            @NotNull
            @Override
            public Fragment getItem(int i) {
                return fragmentList.get(i);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });

        View cutOffLineView = findViewById(R.id.cutOffLineView);
        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private boolean isPauseVideo = false;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                changePage(position);

                switch (position) {
                    case 0:
                        smoothBottomBar.setBarBackgroundColor(getColor(R.color.black));
                        cutOffLineView.setBackgroundColor(getColor(R.color.black));
                        break;
                    case 1:
                        smoothBottomBar.setBarBackgroundColor(getColor(R.color.white));
                        cutOffLineView.setBackgroundColor(getColor(R.color.cutOffLine));
                        break;
                    case 2:
                        smoothBottomBar.setBarBackgroundColor(getColor(R.color.white));
                        cutOffLineView.setBackgroundColor(getColor(R.color.cutOffLine));
                        FavoritesFragment.refreshList = true;
                        LikesFragment.refreshList = true;
                        WorksFragment.refreshList = true;
                        break;
                }

                //控制视频播放
                if(VideoAdapter.currMainPlayViewHolder != null ){
                    if(position != 0){
                        if(VideoAdapter.currMainPlayViewHolder.isInitializeComplete){
                            if(VideoAdapter.currMainPlayViewHolder.isPlaying()){
                                VideoAdapter.currMainPlayViewHolder.pauseVideo();
                                isPauseVideo = true;
                            }
                        }else {
                            VideoAdapter.currMainPlayViewHolder.pauseVideo();
                            isPauseVideo = true;
                        }
                    }else {
                        if(isPauseVideo){
                            VideoAdapter.currMainPlayViewHolder.playVideo();
                            isPauseVideo = false;
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });


        smoothBottomBar = findViewById(R.id.main_bottomBar);
        smoothBottomBar.setOnItemSelectedListener(i -> {
            view_pager.setCurrentItem(i);
            return false;
        });
    }

    private int currPage = 0;
    private void changePage(int position){
        if(smoothBottomBar != null){
            smoothBottomBar.setItemActiveIndex(position);
        }
        view_pager.setCurrentItem(position);
        currPage = position;
    }

    public void openDrawer() {
        if(drawerLayout != null){
            drawerLayout.openDrawer(GravityCompat.END);
        }
    }

    private long touchTime = 0;
    @Override
    public void finish() {
        if(isOpenDrawerLayout){
            if(drawerLayout != null){
                drawerLayout.closeDrawer(GravityCompat.END);
            }
            return;
        }
        //返回时提示再按一次退出程序
        long currentTime = System.currentTimeMillis();
        //等待的时间
        if((currentTime-touchTime)>= 1000L) {
            //让Toast的显示时间和等待时间相同
            MyToast.show("再按一次返回键退出", 1000);
            touchTime = currentTime;
        }else {
            moveTaskToBack(true);
        }
//        super.finish();
    }


}