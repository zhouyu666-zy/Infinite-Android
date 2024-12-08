package edu.ace.infinite.activity;

import android.os.Bundle;

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
import edu.ace.infinite.view.CustomViewPager;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends BaseActivity {

    private List<Fragment> fragmentList;
    private CustomViewPager view_pager;
    private SmoothBottomBar smoothBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);


        initView();
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

        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private boolean isPauseVideo = false;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                changePage(position);

                if(position == 2){
                    FavoritesFragment.refreshList = true;
                    LikesFragment.refreshList = true;
                    WorksFragment.refreshList = true;
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
}