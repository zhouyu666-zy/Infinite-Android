package edu.ace.infinite.activity;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.ace.infinite.R;
import edu.ace.infinite.adapter.UserAdapter;
import edu.ace.infinite.pojo.User;
import edu.ace.infinite.utils.http.UserHttpUtils;
import java.util.List;

public class FollowListActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private boolean isFollow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTextColor(true);
        setContentView(R.layout.activity_follow_list);

        isFollow = getIntent().getBooleanExtra("isFollow", true);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadFollowList();
    }

    private void loadFollowList() {
        new Thread(() -> {
            if(isFollow){
                userList = UserHttpUtils.getFollowList(); // 获取关注列表
            }else {
                userList = UserHttpUtils.getFansList(); // 获取关注列表
            }
            runOnUiThread(() -> {
                userAdapter = new UserAdapter(this, userList);
                recyclerView.setAdapter(userAdapter);
            });
        }).start();
    }
}