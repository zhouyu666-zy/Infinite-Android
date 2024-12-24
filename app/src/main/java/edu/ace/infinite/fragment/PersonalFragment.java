package edu.ace.infinite.fragment;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import static edu.ace.infinite.activity.CropImageActivity.CroppedImageBitmap;
import static edu.ace.infinite.activity.InformationChangeActivity.getFileNameFromContentUri;
import static edu.ace.infinite.activity.InformationChangeActivity.loginInformation;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devil.library.media.MediaSelectorManager;
import com.devil.library.media.config.DVCameraConfig;
import com.devil.library.media.enumtype.DVCameraType;
import com.devil.library.media.enumtype.DVMediaType;
import com.devil.library.media.listener.OnSelectMediaListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


import edu.ace.infinite.R;
import edu.ace.infinite.activity.CropImageActivity;
import edu.ace.infinite.activity.FollowListActivity;
import edu.ace.infinite.activity.InformationChangeActivity;
import edu.ace.infinite.activity.MainActivity;
import edu.ace.infinite.fragment.personalfragment.FavoritesFragment;
import edu.ace.infinite.fragment.personalfragment.LikesFragment;
import edu.ace.infinite.fragment.personalfragment.WorksFragment;
import edu.ace.infinite.pojo.User;
import edu.ace.infinite.utils.ConsoleUtils;
import edu.ace.infinite.utils.GlideEngine;
import edu.ace.infinite.utils.TimeUtils;
import edu.ace.infinite.utils.http.UserHttpUtils;
import edu.ace.infinite.view.MyDialog;
import edu.ace.infinite.view.MyProgressDialog;
import edu.ace.infinite.view.MyToast;
import edu.ace.infinite.utils.http.Config;

public class PersonalFragment extends BaseFragment {
    private ImageView ivAvatar;
    private TextView tvUsername, tvFollows, tvFollowing, tvIntro;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageView backgroundImage;
    private AppBarLayout appBarLayout;
    private static final long DEBOUNCE_DELAY = 100; // 100ms
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateBackgroundRunnable;

    private MainActivity activity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_personal, container, false);
        // 初始化视图
        initViews();
        // 设置用户信息
        refreshInfo = true;
        refreshInfo();
        // 设置TabLayout
        initTabLayout();

        setupBackgroundEffect();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 设置透明状态栏
        requireActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // 动态调整背景图片的高度
        AppBarLayout appBarLayout = view.findViewById(R.id.appbar);
        ImageView backgroundImage = view.findViewById(R.id.background_image);
        appBarLayout.post(() -> {
            int appBarHeight = appBarLayout.getHeight();
            ViewGroup.LayoutParams imageParams = backgroundImage.getLayoutParams();
            imageParams.height = appBarHeight;
            backgroundImage.setLayoutParams(imageParams);
        });

        findViewById(R.id.open_drawer_view).setOnClickListener(view1 -> {
            FragmentActivity activity = getActivity();
            if(activity instanceof MainActivity){
                ((MainActivity) activity).openDrawer();
            }
        });
    }


    /// 设置背景效果
    private void setupBackgroundEffect() {
        // 预加载原始图片
        Glide.with(this)
                .load(R.drawable.user_background)
                .into(backgroundImage);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

            }
        });
    }

    private TextView tvGender;
    private TextView tvAge;
    private TextView tvLocation;
    private void initViews() {
        activity = (MainActivity) getActivity();
        ivAvatar = findViewById(R.id.iv_avatar);
        tvUsername = findViewById(R.id.tv_username);
        tvIntro = findViewById(R.id.tv_intro);
        tvFollows = findViewById(R.id.tv_followers);
        tvFollowing = findViewById(R.id.tv_following);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        tvGender = findViewById(R.id.tv_gender);
        tvAge = findViewById(R.id.tv_age);
        tvLocation = findViewById(R.id.tv_location);

        backgroundImage = view.findViewById(R.id.background_image);
        appBarLayout = view.findViewById(R.id.appbar);
        // 确保 AppBarLayout 初始是展开的
        appBarLayout.setExpanded(true, false);

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new WorksFragment());
        fragments.add(new LikesFragment());
        fragments.add(new FavoritesFragment());
        // 设置 ViewPager2 的适配器
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragments.get(position);
            }

            @Override
            public int getItemCount() {
                return fragments.size();
            }
        });


        findViewById(R.id.film_btn).setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                MyDialog myDialog2 = new MyDialog(getActivity());
                myDialog2.setTitle("所有文件访问权限");
                myDialog2.setMessage("由于Android 11以上系统限制，访问相册需要所有文件访问权限");
                myDialog2.setYesOnclickListener("去开启", () -> {
                    myDialog2.dismiss();
                    XXPermissions.with(this)
                            // 适配 Android 11 需要这样写，这里无需再写 Permission.Group.STORAGE
                            .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                            .request((permissions, all) -> {
                                if (all) {
                                    EasyPhotos.createAlbum(this, true,false, GlideEngine.getInstance())//参数说明：上下文，是否显示相机按钮，是否使用宽高数据（false时宽高数据为0，扫描速度更快），[配置Glide为图片加载引擎](https://github.com/HuanTanSheng/EasyPhotos/wiki/12-%E9%85%8D%E7%BD%AEImageEngine%EF%BC%8C%E6%94%AF%E6%8C%81%E6%89%80%E6%9C%89%E5%9B%BE%E7%89%87%E5%8A%A0%E8%BD%BD%E5%BA%93)
                                            .setPuzzleMenu(false)
                                            .setCleanMenu(false)
                                            .onlyVideo()
                                            .setFileProviderAuthority("edu.ace.infinite.fileprovider")
                                            .start(IMAGE_RETURN_CODE);
                                }
                            });
                });
                myDialog2.setNoOnclickListener("取消", myDialog2::dismiss);
                myDialog2.show();
            }else {
                DVCameraConfig config = MediaSelectorManager.getDefaultCameraConfigBuilder()
                        //相机的类型(系统照相机、普通照相机、美颜相机)默认普通照相机
                        .cameraType(DVCameraType.NORMAL)
                        //是否需要裁剪
                        .needCrop(true)
                        //裁剪大小
                        .cropSize(1, 1, 200, 200)
                        //媒体类型（如果是使用系统照相机，必须指定DVMediaType.PHOTO或DVMediaType.VIDEO）
                        .mediaType(DVMediaType.ALL)
                        //设置录制时长
                        .maxDuration(10)
                        //闪光灯是否启用
                        .flashLightEnable(true)
                        .build();
                MediaSelectorManager.openCameraWithConfig(getActivity(), config, new OnSelectMediaListener() {
                    @Override
                    public void onSelectMedia(List<String> li_path) {
                        try {
                            MyDialog myDialog = new MyDialog(getContext());
                            myDialog.setTitle("上传视频");
                            myDialog.isInputDialog(true);
                            myDialog.setDialogInputHint("请输入视频标题");
                            myDialog.setYesOnclickListener("确认上传", () -> {
                                myDialog.dismiss();
                                String text = myDialog.getDialogInputText();
                                String path = li_path.get(0);
                                MyProgressDialog myProgressDialog = new MyProgressDialog(getActivity());
                                myProgressDialog.setTitleStr("上传视频");
                                myProgressDialog.setHintStr("正在上传视频，请稍等...");
                                myProgressDialog.setCanceledOnTouchOutside(false);
                                myProgressDialog.show();
                                new Thread(() -> {
                                    boolean b = UserHttpUtils.uploadVideo(path,text);
                                    activity.runOnUiThread(() -> {
                                        if(b){
                                            MyToast.show("视频上传成功", Toast.LENGTH_LONG,true);
                                            WorksFragment.refreshList = true;
                                        }else {
                                            MyToast.show("视频上传失败，请重试", Toast.LENGTH_LONG,false);
                                        }
                                        myProgressDialog.dismiss();
                                    });
                                }).start();
                            });
                            myDialog.setNoOnclickListener("取消上传", myDialog::dismiss);
                            myDialog.show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
//                EasyPhotos.createAlbum(this, true,false, GlideEngine.getInstance())//参数说明：上下文，是否显示相机按钮，是否使用宽高数据（false时宽高数据为0，扫描速度更快），[配置Glide为图片加载引擎](https://github.com/HuanTanSheng/EasyPhotos/wiki/12-%E9%85%8D%E7%BD%AEImageEngine%EF%BC%8C%E6%94%AF%E6%8C%81%E6%89%80%E6%9C%89%E5%9B%BE%E7%89%87%E5%8A%A0%E8%BD%BD%E5%BA%93)
//                        .setPuzzleMenu(false)
//                        .setCleanMenu(false)
//                        .onlyVideo()
//                        .setFileProviderAuthority("edu.ace.infinite.fileprovider")
//                        .start(IMAGE_RETURN_CODE);
            }
        });
        ivAvatar.setOnClickListener(view12 -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                MyDialog myDialog2 = new MyDialog(getActivity());
                myDialog2.setTitle("所有文件访问权限");
                myDialog2.setMessage("由于Android 11以上系统限制，访问相册需要所有文件访问权限");
                myDialog2.setYesOnclickListener("去开启", () -> {
                    myDialog2.dismiss();
                    XXPermissions.with(this)
                            // 适配 Android 11 需要这样写，这里无需再写 Permission.Group.STORAGE
                            .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                            .request((permissions, all) -> {
                                if (all) {
                                    EasyPhotos.createAlbum(this, false,false, GlideEngine.getInstance())//参数说明：上下文，是否显示相机按钮，是否使用宽高数据（false时宽高数据为0，扫描速度更快），[配置Glide为图片加载引擎](https://github.com/HuanTanSheng/EasyPhotos/wiki/12-%E9%85%8D%E7%BD%AEImageEngine%EF%BC%8C%E6%94%AF%E6%8C%81%E6%89%80%E6%9C%89%E5%9B%BE%E7%89%87%E5%8A%A0%E8%BD%BD%E5%BA%93)
                                            .setPuzzleMenu(false)
                                            .setCleanMenu(false)
                                            .start(IMAGE_RETURN_CODE);
                                }
                            });
                });
                myDialog2.setNoOnclickListener("取消", myDialog2::dismiss);
                myDialog2.show();
            }else {
                EasyPhotos.createAlbum(this, false,false, GlideEngine.getInstance())//参数说明：上下文，是否显示相机按钮，是否使用宽高数据（false时宽高数据为0，扫描速度更快），[配置Glide为图片加载引擎](https://github.com/HuanTanSheng/EasyPhotos/wiki/12-%E9%85%8D%E7%BD%AEImageEngine%EF%BC%8C%E6%94%AF%E6%8C%81%E6%89%80%E6%9C%89%E5%9B%BE%E7%89%87%E5%8A%A0%E8%BD%BD%E5%BA%93)
                        .setPuzzleMenu(false)
                        .setCleanMenu(false)
                        .start(IMAGE_RETURN_CODE);
            }

        });

    }


    private void initTabLayout() {
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


    @SuppressLint("SetTextI18n")
    private void loadUserInfo() {
        tvGender.setText(loginInformation.getSex());
        long birthday = loginInformation.getBirthday();
        if(birthday > 0){
            tvAge.setText(TimeUtils.calculateAge(birthday) + "岁");
        }
        String district = loginInformation.getDistrict();
        if(district!=null && !district.isEmpty()){
            tvLocation.setText(district);
        }

        // 从服务器获取用户信息
        new Thread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                User user = UserHttpUtils.getUserInfo();
               if(user != null){
                   activity.runOnUiThread(() -> {
                       String s = Config.BaseUrl + user.getAvatar();
                       Glide.with(activity).load(s).into(ivAvatar);
                       loginInformation.setAvatarUrl(s);
                       loginInformation.setNickname(user.getNickname());
                       loginInformation.setIntroduce(user.getIntro());
                       loginInformation.setUsername(user.getUname());
                       tvUsername.setText(user.getNickname());
                       tvFollows.setText(user.getFollowCount()+"");
                       tvFollowing.setText(user.getFansCount()+"");
                       activity.refreshUser(user);
                       if (user.getIntro() == null) {
                           user.setIntro("这个家伙很懒什么也没有写……");
                       } else {
                           tvIntro.setText(user.getIntro());
                       }
                   });
               }
            }
        }).start();

        // 在 PersonalFragment.java 中
        TextView followers = findViewById(R.id.followers);
        TextView following = findViewById(R.id.following);
        followers.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FollowListActivity.class);
            intent.putExtra("isFollow", true);
            startActivity(intent);
        });
        following.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FollowListActivity.class);
            intent.putExtra("isFollow", false);
            startActivity(intent);
        });
    }

    public static boolean refreshInfo = false;
    private void refreshInfo() {
        view.post(new Runnable() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                if(refreshInfo){
                    refreshInfo = false;
                    loadUserInfo();
                }
                view.postDelayed(this, 500);
            }
        });
    }

    public static final int IMAGE_RETURN_CODE = 102; //图片选择返回码
    public static final int RESIZE_REQUEST_CODE = 103; //裁剪图片返回码

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case IMAGE_RETURN_CODE: //执行裁剪
                    if(data != null){
                        ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                        if(resultPhotos != null && !resultPhotos.isEmpty()){
                            Photo photo = resultPhotos.get(0);
                            Intent intent = new Intent(getActivity(), CropImageActivity.class);

                            int BACKGROUND_X = 1;
                            int BACKGROUND_Y = 1;
                            CropImageActivity.xyScale[0] = BACKGROUND_X; //设置裁剪比例X
                            CropImageActivity.xyScale[1] = BACKGROUND_Y;  //设置裁剪比例Y
                            CropImageActivity.uri = photo.uri;
//                            intent.putExtra("uri",photo.uri);
                            startActivityForResult(intent,RESIZE_REQUEST_CODE);
                        }
                    }
                    break;
                case RESIZE_REQUEST_CODE: //裁剪完成
                    if (data != null) {
                        if(CroppedImageBitmap == null){
                            MyToast.show("图片读取失败，请重试", Toast.LENGTH_LONG,false);
                            return;
                        }
                        Uri uri = CropImageActivity.uri;
                        String fileName = getFileNameFromContentUri(getContext(), uri);

                        final MyProgressDialog myProgressDialog = new MyProgressDialog(getActivity());
                        myProgressDialog.setTitleStr("上传图片");
                        myProgressDialog.setHintStr("正在上传，请稍等...");
                        myProgressDialog.setCanceledOnTouchOutside(false);
                        myProgressDialog.show();
                        new Thread(() -> {
                            boolean b = UserHttpUtils.uploadImage(0, CroppedImageBitmap, fileName);
                            getActivity().runOnUiThread(() -> {
                                if(b){
                                    MyToast.show("上传成功", Toast.LENGTH_LONG,true);
                                    refreshInfo = true;
                                }else {
                                    MyToast.show("上传失败，请重试", Toast.LENGTH_LONG,false);
                                }
                                myProgressDialog.dismiss();
                            });
                        }).start();
                    }
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}