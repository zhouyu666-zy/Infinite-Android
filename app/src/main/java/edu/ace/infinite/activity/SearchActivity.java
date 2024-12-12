package edu.ace.infinite.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.ace.infinite.R;
import edu.ace.infinite.adapter.UserAdapter;
import edu.ace.infinite.pojo.User;
import edu.ace.infinite.utils.http.UserHttpUtils;

public class SearchActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private EditText search_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search);
        super.onCreate(savedInstanceState);
        setStatusBarTextColor(true);
        search_input = findViewById(R.id.search_input);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        search_input.requestFocus(); //输入框获得焦点

        search_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            boolean isLoad;
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                new Thread(() -> {
                    if(isLoad){
                       return;
                    }
                    isLoad = true;
                    String keyword = search_input.getText().toString();
                    List<User> userList = UserHttpUtils.searchUser(keyword);
                    if(userList != null){
                        runOnUiThread(() -> recyclerView.setAdapter(new UserAdapter(SearchActivity.this,userList)));
                    }
                    isLoad = false;
                }).start();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

}