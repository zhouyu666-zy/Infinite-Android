package edu.ace.infinite.activity;

import static edu.ace.infinite.activity.CropImageActivity.CroppedImageBitmap;
import static edu.ace.infinite.fragment.PersonalFragment.IMAGE_RETURN_CODE;
import static edu.ace.infinite.fragment.PersonalFragment.RESIZE_REQUEST_CODE;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.orhanobut.hawk.Hawk;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

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
import edu.ace.infinite.pojo.ChatMessage;
import edu.ace.infinite.pojo.MessageListItem;
import edu.ace.infinite.pojo.User;
import edu.ace.infinite.utils.CleanCacheUtils;
import edu.ace.infinite.utils.ConsoleUtils;
import edu.ace.infinite.utils.MessageList;
import edu.ace.infinite.utils.NotificationHelper;
import edu.ace.infinite.utils.PhoneMessage;
import edu.ace.infinite.utils.TimeUtils;
import edu.ace.infinite.utils.http.Config;
import edu.ace.infinite.view.CustomViewPager;
import edu.ace.infinite.view.MyDialog;
import edu.ace.infinite.view.MyProgressDialog;
import edu.ace.infinite.view.MyToast;
import edu.ace.infinite.application.WebSocketManager;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends BaseActivity {

    private List<Fragment> fragmentList;
    private CustomViewPager view_pager;
    private SmoothBottomBar smoothBottomBar;
    private DrawerLayout drawerLayout;
    private boolean isOpenDrawerLayout;

    public final static String OPEN_CHAT_ACTIVITY = "OPEN_CHAT_ACTIVITY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        initView();
        initDrawerLayout();
        connectWebSocket();

        NotificationHelper.getNotification(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra(OPEN_CHAT_ACTIVITY, false)) {
            Intent startIntent = new Intent(this, ChatActivity.class);
            Bundle extras = intent.getExtras();
            if(extras != null) {
                startIntent.putExtras(extras);
            }
            startActivity(startIntent);
        }
    }

    WebSocketManager.MessageCallback messageCallback;
    private void connectWebSocket() {
        String token = Hawk.get("loginToken");
        messageCallback = new WebSocketManager.MessageCallback() {
            @Override
            public void onMessage(String message) {
//                runOnUiThread(() -> MyToast.show("收到消息：" + message));
                try {
                    ConsoleUtils.logErr(message);
                    JSONObject jsonObject = new JSONObject(message);
                    String senderId = jsonObject.getString("senderId");
//                    long timestamp = jsonObject.getLong("timestamp");
                    JSONObject userInfo = jsonObject.getJSONObject("userInfo");
                    String nickname = userInfo.getString("nickname");
                    String content = jsonObject.getString("content");

                    List<MessageListItem> messageList = MessageFragment.getMessageList();
                    MessageListItem sendMessageListItem = null;
                    boolean isExist = false;
                    for (MessageListItem messageListItem : messageList) {
                        String userId = messageListItem.getUserId();
                        if (userId.trim().equals(senderId.trim())) {
                            //如果存在，则更新消息
                            sendMessageListItem = messageListItem;
                            messageListItem.setLastMessage(content);
                            messageListItem.setLastTime(TimeUtils.getMessageTime(System.currentTimeMillis()));
                            messageListItem.setUnreadCount(messageListItem.getUnreadCount() + 1); //未读加1
                            //添加到消息列表中
                            List<ChatMessage> chatMessageList = messageListItem.getChatMessageList();
                            ChatMessage chatMessage = new ChatMessage(senderId, nickname,
                                    token, content, 1);
                            chatMessageList.add(chatMessage);
                            isExist = true;
                            break;
                        }
                    }

                    //如不存在聊天记录，则添加到聊天中
                    if (!isExist) {
                        long currentTimeMillis = System.currentTimeMillis();
                        String avatar = userInfo.getString("avatar");
                        int id = userInfo.getInt("id");
                        MessageListItem messageListItem = new MessageListItem();
                        messageListItem.setUserId(String.valueOf(id));
                        messageListItem.setAvatar(avatar);
                        messageListItem.setLastMessage(content);
                        messageListItem.setLastTime(TimeUtils.getMessageTime(currentTimeMillis));
                        messageListItem.setOnline(true);
                        messageListItem.setUnreadCount(1);
                        messageListItem.setUsername(nickname);
                        MessageList<ChatMessage> chatMessages = new MessageList<>();
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setContent(content);
                        chatMessage.setMessageType(1);
                        chatMessage.setSenderAvatar(avatar);
                        chatMessage.setSenderId(String.valueOf(id));
                        chatMessage.setSenderName(nickname);
                        chatMessage.setReceiverId(token);
                        chatMessage.setTimestamp(currentTimeMillis);
                        chatMessages.add(chatMessage);
                        messageListItem.setChatMessageList(chatMessages);
                        messageList.add(messageListItem);
                        sendMessageListItem = messageListItem;
                    }

                    //发送通知
                    boolean allowNotification = NotificationHelper.isOpenNotification(MainActivity.this);
                    if(allowNotification){
                        Bitmap avatar = BitmapFactory.decodeResource(getResources(), R.drawable.person);
                        String time = TimeUtils.getNotificationTime(System.currentTimeMillis());
                        NotificationHelper.createMessageNotification(MainActivity.this,nickname, content,time, avatar,sendMessageListItem);
                    }
                    MessageFragment.refreshList = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConnected() {

            }

            @Override
            public void onDisconnected() {

            }

            @Override
            public void onError(String error) {
                ConsoleUtils.logErr("错误" + error);

                //重新连接服务器
                if (!WebSocketManager.isConnectWebSocket) {
                    view_pager.postDelayed(() -> WebSocketManager.getInstance().connect(), 500);
                }

//                runOnUiThread(() ->
//                        MyToast.show("错误: " + error));
            }
        };
        WebSocketManager.getInstance().setMessageCallback(messageCallback);
        WebSocketManager.getInstance().connect();
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
                        MessageFragment.refreshList = true;
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

    private TextView drawer_view_username;
    private ImageView drawer_view_user_avatar;

    private void initDrawerLayout() {
        drawer_view_username = findViewById(R.id.drawer_view_username);
        drawer_view_user_avatar = findViewById(R.id.drawer_view_user_avatar);
        TextView cache_size_text = findViewById(R.id.cache_size_text);

        //清除缓存
        findViewById(R.id.clear_cache).setOnClickListener(v -> {
            CleanCacheUtils.getInstance().calculateCacheSize(cache_size_text);
            CleanCacheUtils.getInstance().showDialog(this,cache_size_text);
        });
        CleanCacheUtils.getInstance().calculateCacheSize(cache_size_text);

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
                CleanCacheUtils.getInstance().calculateCacheSize(cache_size_text);
            }
            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                isOpenDrawerLayout = false;
            }
            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        //编辑信息
        findViewById(R.id.edit_message).setOnClickListener(view -> {
            closeDrawer();
            view.postDelayed(() -> {
                closeDrawer();
                Intent intent = new Intent(MainActivity.this, InformationChangeActivity.class);
                startActivity(intent);
            },200);
        });

        //退出登录
        findViewById(R.id.quit_login).setOnClickListener(view -> {
            MyDialog myDialog = new MyDialog(this,R.style.MyDialog);
            myDialog.setTitle("退出登录");
            myDialog.setThemeColor(getColor(R.color.Theme2));
            myDialog.setMessage("确定退出登录吗？");
            myDialog.setYesOnclickListener("确定退出", () -> {
                myDialog.dismiss();
                Hawk.delete("loginToken");
                WebSocketManager.getInstance().disconnect(); //退出登录关闭连接
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                isReturn = true;
                finish();
            });
            myDialog.setNoOnclickListener("取消退出", myDialog::dismiss);
            myDialog.show();
        });
    }

    public void refreshUser(User user) {
        String avatarUrl = Config.BaseUrl + user.getAvatar();
        drawer_view_username.setText(user.getNickname());
        Glide.with(this).load(avatarUrl).into(drawer_view_user_avatar);
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

    public void closeDrawer() {
        if(drawerLayout != null && isOpenDrawerLayout){
            drawerLayout.closeDrawer(GravityCompat.END);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //重新连接服务器
        if (!WebSocketManager.isConnectWebSocket) {
            WebSocketManager.getInstance().connect();
        }
    }

    private boolean isReturn;
    private long touchTime = 0;
    @Override
    public void finish() {
        if(isReturn){
            if(messageCallback != null){
                WebSocketManager.getInstance().removeMessageCallback(messageCallback);
            }
            super.finish();
        }
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