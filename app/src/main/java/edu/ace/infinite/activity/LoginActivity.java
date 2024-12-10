package edu.ace.infinite.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.ace.infinite.R;
import edu.ace.infinite.utils.VerificationCode;
import edu.ace.infinite.utils.http.UserHttpUtils;
import edu.ace.infinite.view.MyProgressDialog;
import edu.ace.infinite.view.MyToast;

public class LoginActivity extends BaseActivity {
    private EditText username; //用户名输入框
    private EditText password; //密码输入框
    private ImageView password_eye_image;
    private boolean passwordIsOpen = false;
    private EditText verification_code; //验证码输入框
    private ImageView verification_code_image; //验证码图片
    private final VerificationCode verificationCode = new VerificationCode(); //生成验证码
    private boolean isLogin = true; //是否再登录界面
    private LinearLayout login_view; //登录界面
    private LinearLayout register_view; //注册界面
    private EditText username_register; //注册用户名
    private EditText password_register; //注册密码输入框
    private ImageView password_eye_image_register;
    private boolean passwordIsOpenRegister = false;
    private EditText password_register_second; //注册确认密码输入框
    private ImageView password_eye_image_register_second;
    private boolean passwordIsOpenRegisterSecond = false;
    private boolean isReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTextColor(false);
        setContentView(R.layout.activity_login);


        initView();

        loginInitView();
        register_view.setVisibility(View.GONE);
        login_view.setVisibility(View.VISIBLE);
    }

    private void change(){
        if(isLogin){
            loginInitView();
            register_view.setVisibility(View.GONE);
            login_view.setVisibility(View.VISIBLE);
        }else{
            registerInitView();
            register_view.setVisibility(View.VISIBLE);
            login_view.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化
     */
    private void initView(){
        login_view = findViewById(R.id.login);
        register_view = findViewById(R.id.register);

        int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String hint;
        if(h < 5){
            hint = "晚上好";
        }else if(h < 9){
            hint = "早上好";
        }else if(h < 11){
            hint = "上午好";
        }else if(h < 13){
            hint = "中午好";
        }else if(h < 17){
            hint = "下午好";
        }else{
            hint = "晚上好";
        }
        ((TextView)findViewById(R.id.hint_text)).setText(hint);
    }
    /**
     * 登录初始化
     */
    private void loginInitView() {
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        password.setTypeface(Typeface.DEFAULT);
        LinearLayout password_eye = findViewById(R.id.password_eye);
        password_eye_image = findViewById(R.id.password_eye_image);
        LinearLayout login_btn = findViewById(R.id.login_btn);
        LinearLayout login_change_btn = findViewById(R.id.login_change_btn);

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password.setTypeface(Typeface.DEFAULT);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password_eye.setOnClickListener(v -> {
            if(!passwordIsOpen){
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                password_eye_image.setImageResource(R.drawable.open_eye_icon);
            }else {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                password_eye_image.setImageResource(R.drawable.close_eye_icon);
            }
            passwordIsOpen = !passwordIsOpen;
            password.setTypeface(Typeface.DEFAULT);
        });

        login_change_btn.setOnClickListener(v -> {
            register_view.setVisibility(View.VISIBLE);
            login_view.setVisibility(View.GONE);
            registerInitView();
            isLogin = false;
        });

        login_btn.setOnClickListener(v -> verify());
    }

    /**
     * 注册初始化
     */
    private void registerInitView() {
        username_register = findViewById(R.id.username_register);
        password_register = findViewById(R.id.password_register);
        password_register.setTypeface(Typeface.DEFAULT);
        //是否显示注册密码
        LinearLayout password_eye_register = findViewById(R.id.password_eye_register);
        password_eye_image_register = findViewById(R.id.password_eye_image_register);
        password_register_second = findViewById(R.id.password_register_second);
        password_register_second.setTypeface(Typeface.DEFAULT);
        //是否显示注册确认密码
        LinearLayout password_eye_register_second = findViewById(R.id.password_eye_register_second);
        password_eye_image_register_second = findViewById(R.id.password_eye_image_register_second);
        //注册切换按钮
        LinearLayout register_change_btn = findViewById(R.id.register_change_btn);
        verification_code = findViewById(R.id.verification_code);
        verification_code_image = findViewById(R.id.verification_code_image);
        //注册按钮
        LinearLayout register_btn = findViewById(R.id.register_btn);

        password_register.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password_register.setTypeface(Typeface.DEFAULT);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password_eye_register.setOnClickListener(v -> {
            if(!passwordIsOpenRegister){
                password_register.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                password_eye_image_register.setImageResource(R.drawable.open_eye_icon);
            }else {
                password_register.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                password_eye_image_register.setImageResource(R.drawable.close_eye_icon);
            }
            passwordIsOpenRegister = !passwordIsOpenRegister;
            password_register.setTypeface(Typeface.DEFAULT);
        });
        password_register_second.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password_register_second.setTypeface(Typeface.DEFAULT);
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password_eye_register_second.setOnClickListener(v -> {
            if(!passwordIsOpenRegisterSecond){
                password_register_second.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                password_eye_image_register_second.setImageResource(R.drawable.open_eye_icon);
            }else {
                password_register_second.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                password_eye_image_register_second.setImageResource(R.drawable.close_eye_icon);
            }
            passwordIsOpenRegisterSecond = !passwordIsOpenRegisterSecond;
            password_register_second.setTypeface(Typeface.DEFAULT);
        });

        register_change_btn.setOnClickListener(v -> {
            register_view.setVisibility(View.GONE);
            login_view.setVisibility(View.VISIBLE);
            loginInitView();
            isLogin = true;
        });

        //生成验证码
        Bitmap verification_code_bitmap = verificationCode.createBitmap();
        verification_code_image.setImageBitmap(verification_code_bitmap);

        verification_code_image.setOnClickListener(v -> {
            //生成验证码
            Bitmap verification_code_bitmap1 = verificationCode.createBitmap();
            verification_code_image.setImageBitmap(verification_code_bitmap1);
        });
        register_btn.setOnClickListener(v -> verify());
    }

    private void verify(){
        if(isLogin){
            String username_text = username.getText().toString().replace(" ","");
            String password_text = password.getText().toString().replace(" ","");
            if(username_text.isEmpty()){
                MyToast.show("用户名为空",false);
                return;
            } else if(password_text.isEmpty()){
                MyToast.show("密码为空",false);
                return;
            }else if(!username_text.matches("^[a-z0-9A-Z_]+$")){
                MyToast.show("用户名只能含有字母数字和下划线",false);
                return;
            }else if(!password_text.matches("^[a-z0-9A-Z]+$")){
                MyToast.show("密码只能含有字母和数字",false);
                return;
            }

            final MyProgressDialog myProgressDialog = new MyProgressDialog(this);
            myProgressDialog.setTitleStr("登录");
            myProgressDialog.setHintStr("正在登录中，请稍等...");
            myProgressDialog.show();
            AtomicBoolean dialogCancel = new AtomicBoolean(false);
            myProgressDialog.setOnDismissListener(dialog -> {
                dialogCancel.set(true);
            });
            //TODO 处理登录 完成后执行 myProgressDialog.dismiss();

            new Thread(() -> {
                boolean login = UserHttpUtils.login(username_text, password_text);
                runOnUiThread(() -> {
                    if(login){
                        MyToast.show("登录成功",true);
                        startActivity(new Intent(this, MainActivity.class));
                    }else {
                        MyToast.show("登录失败",false);
                    }
                    myProgressDialog.dismiss();
                });
            }).start();
        }else {
            String username_text = username_register.getText().toString().replace(" ","");
            String password_text = password_register.getText().toString().replace(" ","");
            String password_text_second = password_register_second.getText().toString().replace(" ","");
            String verify_code = verification_code.getText().toString();
            if(username_text.isEmpty()){
                MyToast.show("用户名为空",false);
                return;
            }else if(password_text.isEmpty() || password_text_second.isEmpty()){
                MyToast.show("密码为空",false);
                return;
            }else if(verify_code.isEmpty()){
                MyToast.show("验证码为空",false);
                return;
            }else if(!username_text.matches("^[a-z0-9A-Z_]+$")){
                MyToast.show("用户名只能含有字母数字和下划线",false);
                return;
            }else if(password_text.length() < 6){
                MyToast.show("密码至少为6位",false);
                return;
            }else if(!password_text.equals(password_text_second)){
                MyToast.show("两次输入的密码不一致",false);
                return;
            }else if(!password_text.matches("^[a-z0-9A-Z]+$")){
                MyToast.show("密码只能含有字母和数字",false);
                return;
            }else if (!verify_code.equalsIgnoreCase(verificationCode.getCode())){
                MyToast.show("验证码错误",false);
                return;
            }

            final MyProgressDialog myProgressDialog = new MyProgressDialog(this);
            myProgressDialog.setTitleStr("注册");
            myProgressDialog.setHintStr("正在注册中，请稍等...");
            myProgressDialog.show();
            //TODO 处理注册 完成后执行 myProgressDialog.dismiss();
            new Thread(() -> {
                boolean registerUser = UserHttpUtils.registerUser(username_text, password_text);
                runOnUiThread(() -> {
                    if(registerUser){
                        MyToast.show("注册成功",true);
                    }else {
                        MyToast.show("注册失败",false);
                    }
                    myProgressDialog.dismiss();
                });
            }).start();
        }
    }

    private long touchTime = 0;
    @SuppressLint({"WrongConstant", "ShowToast"})
    @Override
    public void finish() {

        if(isReturn){
            super.finish();
            return;
        }

        //返回时提示再按一次退出程序
        long currentTime = System.currentTimeMillis();
        //等待的时间
        if((currentTime-touchTime)>= 2000L) {
            //让Toast的显示时间和等待时间相同
            MyToast.show("再按一次返回键退出", 1000);
            touchTime = currentTime;
        }else {
            moveTaskToBack(true);
        }
    }
}
