package edu.ace.infinite.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import edu.ace.infinite.R;
import edu.ace.infinite.adapter.PersonalPagerAdapter;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class PersonalFragment extends BaseFragment {
    private ImageView ivAvatar;
    private TextView tvUsername, tvUserId, tvFollows, tvFollowing, tvIntro;
    private Button btnEditProfile;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private PersonalPagerAdapter pagerAdapter;
    private ImageView backgroundImage;
    private AppBarLayout appBarLayout;
    private static final long DEBOUNCE_DELAY = 100; // 100ms
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateBackgroundRunnable;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_personal, container, false);

        // 初始化视图
        initViews();

        // 设置 ViewPager2 的适配器
        PersonalPagerAdapter adapter = new PersonalPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // 在这里添加 OnPageChangeCallback
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                appBarLayout.setExpanded(true, true);
            }
        });

        // 设置用户信息
        setUserInfo();
        // 设置ViewPager
        setupViewPager();
        // 设置TabLayout
        setupTabLayout();
        // 设置监听器
        setupListeners();

        backgroundImage = view.findViewById(R.id.background_image);
        appBarLayout = view.findViewById(R.id.appbar);
        // 确保 AppBarLayout 初始是展开的
        appBarLayout.setExpanded(true, false);

        setupBackgroundEffect();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 设置透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
            requireActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        // 获取状态栏高度
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        // 调整用户信息 ConstraintLayout 的顶部边距
        View userInfoLayout = view.findViewById(R.id.user_info_layout); // 确保给 ConstraintLayout 设置了 id
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) userInfoLayout.getLayoutParams();
        params.topMargin = statusBarHeight;
        userInfoLayout.setLayoutParams(params);

        // 动态调整背景图片的高度
        AppBarLayout appBarLayout = view.findViewById(R.id.appbar);
        ImageView backgroundImage = view.findViewById(R.id.background_image);

        appBarLayout.post(new Runnable() {
            @Override
            public void run() {
                int appBarHeight = appBarLayout.getHeight();
                ViewGroup.LayoutParams imageParams = backgroundImage.getLayoutParams();
                imageParams.height = appBarHeight;
                backgroundImage.setLayoutParams(imageParams);
            }
        });

    }

    /// 设置背景效果
    private void setupBackgroundEffect() {
        // 预加载原始图片
        Glide.with(this)
                .load(R.drawable.user_background)
                .into(backgroundImage);

        // 创建一个模糊处理的 RequestBuilder
        RequestBuilder<Drawable> blurredBuilder = Glide.with(this)
                .load(R.drawable.user_background)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25)));

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float scrollProgress = (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange();

                // 放大效果
                float scale = 1 + (0.2f * (1 - scrollProgress));
                backgroundImage.setScaleX(scale);
                backgroundImage.setScaleY(scale);

                // 使用防抖动机制更新背景
                if (updateBackgroundRunnable != null) {
                    handler.removeCallbacks(updateBackgroundRunnable);
                }
                updateBackgroundRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (scrollProgress > 0.1f) {
                            blurredBuilder.into(backgroundImage);
                        } else {
                            Glide.with(PersonalFragment.this)
                                    .load(R.drawable.user_background)
                                    .into(backgroundImage);
                        }
                    }
                };
                handler.postDelayed(updateBackgroundRunnable, DEBOUNCE_DELAY);
            }
        });
    }

    private void initViews() {
        ivAvatar = findViewById(R.id.iv_avatar);
        tvUsername = findViewById(R.id.tv_username);
        tvUserId = findViewById(R.id.tv_userid);
        tvIntro = findViewById(R.id.tv_intro);
        tvFollows = findViewById(R.id.tv_followers);
        tvFollowing = findViewById(R.id.tv_following);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
    }

    private void setupViewPager() {
        pagerAdapter = new PersonalPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
    }

    private void setupTabLayout() {
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("作品");
                            break;
                        case 1:
                            tab.setText("喜欢");
                            break;
                        case 2:
                            tab.setText("收藏");
                            break;
                    }
                }
        ).attach();
    }


    private void setUserInfo() {
        // 这里应该从服务器或本地数据库获取用户信息
        // 以下是示例数据
        ivAvatar.setImageResource(R.drawable.logo);
        tvUsername.setText("JAY-Chou-jay");
        tvUserId.setText("抖音号：41963388141");
        tvFollows.setText("218 关注");
        tvFollowing.setText("15 粉丝");
        tvIntro.setText("点击添加介绍，让大家认识你...");
    }

    private void setupListeners() {
        btnEditProfile.setOnClickListener(v -> {
            // TODO: 实现编辑资料功能
            Toast.makeText(getContext(), "编辑资料", Toast.LENGTH_SHORT).show();
        });
    }

}