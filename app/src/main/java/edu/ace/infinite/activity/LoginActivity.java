package edu.ace.infinite.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    private Button loginButton;
    private Button registerButton;
    private Animation buttonClickAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        buttonClickAnim = AnimationUtils.loadAnimation(this, R.anim.button_click);

        loginButton.setOnClickListener(view -> {
            view.startAnimation(buttonClickAnim);
            // 处理登录逻辑
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        registerButton.setOnClickListener(view -> {
            view.startAnimation(buttonClickAnim);
            // 处理注册逻辑
        });
    }
}
