package edu.ace.infinite.activity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import edu.ace.infinite.R;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvTitle,tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvTitle = findViewById(R.id.tv_title);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        // 渐变动画 - 标题淡入
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1000);  // 设置动画持续时间
        tvTitle.startAnimation(fadeIn);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 按钮点击时的缩放效果
                ScaleAnimation scaleAnimation = new ScaleAnimation(
                        1f, 0.95f, 1f, 0.95f, // 放大到缩小
                        android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                        android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f
                );
                scaleAnimation.setDuration(200);
                v.startAnimation(scaleAnimation);

                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    // TODO: 实现登录逻辑
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // EditText 获取焦点时的背景色变化（聚焦动画）
        etUsername.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.setBackgroundColor(getResources().getColor(R.color.colorAccent));  // 聚焦时背景色
            } else {
                v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));  // 失去焦点时恢复
            }
        });

        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.setBackgroundColor(getResources().getColor(R.color.colorAccent));  // 聚焦时背景色
            } else {
                v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));  // 失去焦点时恢复
            }
        });
        // 忘记密码按钮点击事件
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 实现忘记密码功能
                Toast.makeText(LoginActivity.this, "忘记密码功能待实现", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
