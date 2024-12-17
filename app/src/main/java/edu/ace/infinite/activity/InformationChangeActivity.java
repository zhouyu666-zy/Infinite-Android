package edu.ace.infinite.activity;


import static edu.ace.infinite.activity.CropImageActivity.CroppedImageBitmap;
import static edu.ace.infinite.fragment.PersonalFragment.IMAGE_RETURN_CODE;
import static edu.ace.infinite.fragment.PersonalFragment.RESIZE_REQUEST_CODE;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.orhanobut.hawk.Hawk;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import edu.ace.infinite.R;
import edu.ace.infinite.application.Application;
import edu.ace.infinite.fragment.PersonalFragment;
import edu.ace.infinite.pojo.CityJsonBean;
import edu.ace.infinite.pojo.LoginInformation;
import edu.ace.infinite.pojo.User;
import edu.ace.infinite.utils.GlideEngine;
import edu.ace.infinite.utils.http.UserHttpUtils;
import edu.ace.infinite.utils.http.VideoHttpUtils;
import edu.ace.infinite.view.CircleImage;
import edu.ace.infinite.view.MyDialog;
import edu.ace.infinite.view.MyProgressDialog;
import edu.ace.infinite.view.MySelectDialog;
import edu.ace.infinite.view.MyToast;

public class InformationChangeActivity extends BaseActivity {
    private CircleImage avatarImageView; //头像
    private Bitmap avatarBitmap; //头像bitmap
    private TextView nicknameTextView; //昵称
    private String nicknameText; //昵称文本
    private TextView introduceTextView; //简介
    private String introduceText; //简介文本
    private TextView sexTextView; //性别
    private String sexText; //性别文本
    private TextView birthdayTextView; //生日
    private long birthdayText = 0; //生日文本（毫秒值）
    private TextView districtTextView; //地区
    private String districtText; //地区文本
    private boolean isChange = false; //是否有信息修改

    public static LoginInformation loginInformation = new LoginInformation();

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());  //生日时间格式化

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_change);
        setStatusBarTextColor(true);

        initView();
        initEvent();
        initData();
    }

    private void initView() {
        avatarImageView = findViewById(R.id.avatarImageView);
        nicknameTextView = findViewById(R.id.nicknameTextView);
        introduceTextView = findViewById(R.id.introduceTextView);
        sexTextView = findViewById(R.id.sexTextView);
        birthdayTextView = findViewById(R.id.birthdayTextView);
        districtTextView = findViewById(R.id.districtTextView);
    }

    private void initEvent() {
        findViewById(R.id.back).setOnClickListener(v -> finish());

        findViewById(R.id.avatar).setOnClickListener(v ->
//                EasyPhotos.createAlbum(this, true, GlideEngine.getInstance())//参数说明：上下文，是否显示相机按钮，[配置Glide为图片加载引擎](https://github.com/HuanTanSheng/EasyPhotos/wiki/12-%E9%85%8D%E7%BD%AEImageEngine%EF%BC%8C%E6%94%AF%E6%8C%81%E6%89%80%E6%9C%89%E5%9B%BE%E7%89%87%E5%8A%A0%E8%BD%BD%E5%BA%93)
//                .setFileProviderAuthority("com.zhouyu.music.fileprovider")//参数说明：见下方`FileProvider的配置`
//                .setPuzzleMenu(false) //设置是否显示拼图按钮
//                .setCleanMenu(false)  //设置是否显示清空按钮
//                .start(IMAGE_RETURN_CODE)
                EasyPhotos.createAlbum(this, false,false, GlideEngine.getInstance())//参数说明：上下文，是否显示相机按钮，是否使用宽高数据（false时宽高数据为0，扫描速度更快），[配置Glide为图片加载引擎](https://github.com/HuanTanSheng/EasyPhotos/wiki/12-%E9%85%8D%E7%BD%AEImageEngine%EF%BC%8C%E6%94%AF%E6%8C%81%E6%89%80%E6%9C%89%E5%9B%BE%E7%89%87%E5%8A%A0%E8%BD%BD%E5%BA%93)
                        .setPuzzleMenu(false) //设置是否显示拼图按钮
                        .setCleanMenu(false)  //设置是否显示清空按钮
                        .start(IMAGE_RETURN_CODE)
        );

        findViewById(R.id.nickname).setOnClickListener(v -> {
            MyDialog myDialog = new MyDialog(InformationChangeActivity.this);
            myDialog.isInputDialog(true);
            myDialog.setTitle("修改昵称");
            myDialog.setDialogInputHint("请输入要修改的昵称");
            if(nicknameText == null){
                String nickname = loginInformation.getNickname();
                if(nickname != null){
                    myDialog.setDialog_input_default_value(nickname);
                }
            }else {
                myDialog.setDialog_input_default_value(nicknameText);
            }
            myDialog.setNoOnclickListener("取消", myDialog::dismiss);
            myDialog.setYesOnclickListener("确定", () -> {
                String name = myDialog.getDialogInputText();
                nicknameTextView.setText(name);
                nicknameText = name;
                isChange = true;
                myDialog.dismiss();
            });
            myDialog.show();
        });

        findViewById(R.id.introduce).setOnClickListener(v -> {
            MyDialog myDialog = new MyDialog(InformationChangeActivity.this);
            myDialog.isInputDialog(true);
            myDialog.setTitle("修改简介");
            myDialog.setDialogInputHint("请输入简介");
            if(introduceText == null){
                String introduce = loginInformation.getIntroduce();
                if(introduce != null){
                    myDialog.setDialog_input_default_value(introduce);
                }
            }else {
                myDialog.setDialog_input_default_value(introduceText);
            }
            myDialog.setNoOnclickListener("取消", myDialog::dismiss);
            myDialog.setYesOnclickListener("确定", () -> {
                String name = myDialog.getDialogInputText();
                introduceTextView.setText(name);
                introduceText = name;
                isChange = true;
                myDialog.dismiss();
            });
            myDialog.show();
        });

        findViewById(R.id.sex).setOnClickListener(v -> {
            int index = -1;
            String[] items = {"男","女"};
            for (int i = 0; i < items.length; i++) {
                if(items[i].equals(sexText)){
                    index = i;
                }
            }
            MySelectDialog mySelectDialog = new MySelectDialog(this,items,index);
            mySelectDialog.setTitle("请选择您的性别");
            mySelectDialog.setItemOnClickListener((index2,item,dialog) -> {
                sexTextView.setText(item);
                sexText = item;
                isChange = true;
                dialog.dismiss();
            });
            mySelectDialog.show();
        });

        findViewById(R.id.birthday).setOnClickListener(v -> showTimePickerView());

        initJsonData();
        findViewById(R.id.district).setOnClickListener(v -> showDistrictPickerView());
    }

    private void initData() {
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        TextView top_title = findViewById(R.id.top_title);
        top_title.setText(title);


        Glide.with(this).load(loginInformation.getAvatarUrl()).into(avatarImageView);

        TextView usernameTextView = findViewById(R.id.usernameTextView);
        usernameTextView.setText(loginInformation.getUsername());

        //昵称加载
        String nickname = loginInformation.getNickname();
        if(nickname!=null){
            nicknameTextView.setText(nickname);
        }else {
            nicknameTextView.setText("");
        }

        String introduce = loginInformation.getIntroduce();
        if(introduce!=null){
            introduceTextView.setText(introduce);
        }else {
            introduceTextView.setText("暂无简介，点我修改");
        }

        String sex = loginInformation.getSex();
        sexText = sex;
        sexTextView.setText(sex);

        long birthday = loginInformation.getBirthday();
        if(birthday != 0){
            birthdayTextView.setText(sdf.format(new Date(birthday)));
        }

        String district = loginInformation.getDistrict();
        if(district != null){
            districtTextView.setText(district);
        }
    }

    /**
     * 显示时间dialog
     */
    private void showTimePickerView(){
        long birthday = loginInformation.getBirthday();

        //时间选择器
        final TimePickerView pvTime;
        try {
            Calendar defaultSelectCalendar = Calendar.getInstance(TimeZone.getDefault());
            //设置初始选择时间
            if(birthdayText != 0){
                defaultSelectCalendar.setTime(new Date(birthdayText));
            }else {
                if(birthday != 0){
                    defaultSelectCalendar.setTime(new Date(birthday));
                }
            }

            Calendar startCalendar = Calendar.getInstance(TimeZone.getDefault());
            //设置开始日期
            startCalendar.setTime(Objects.requireNonNull(sdf.parse("1900年1月1日")));

            pvTime = new TimePickerBuilder(this, (date, v1) -> {
                birthdayTextView.setText(sdf.format(date));
                birthdayText = date.getTime();
                isChange = true;
            }).setType(new boolean[]{true,true,true,false,false,false})// 默认全部显示
                    .setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setContentTextSize(14)//滚轮文字大小
                    .setTitleSize(16)//标题文字大小
                    .setTitleText("请选择您的生日")//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .isCyclic(false)//是否循环滚动
                    .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                    .setTitleColor(Color.BLACK)//标题文字颜色
                    .setSubmitColor(getColor(R.color.Theme))//确定按钮文字颜色
                    .setCancelColor(getColor(R.color.Theme))//取消按钮文字颜色
                    .setTitleBgColor(getColor(R.color.themeColor))//标题背景颜色
                    .setBgColor(getColor(R.color.viewColor))//滚轮背景颜色 Night mode
                    .setLabel("年","月","日","时","分","秒")//默认设置为年月日时分秒
                    .isCenterLabel(false)
                    .isDialog(false)
                    .setDate(defaultSelectCalendar)
                    .setRangDate(startCalendar,Calendar.getInstance())
                    .build();
            pvTime.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示地区dialog
     */
    private void showDistrictPickerView() {
        // 弹出选择器（省市区三级联动）
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, (options1, options2, options3, v) -> {
            //返回的分别是三个级别的选中位置
            String data = options1Items.get(options1).getPickerViewText() + "-"
                    + options2Items.get(options1).get(options2);
            districtText = data;
            districtText = districtText.replace("-"," · ")
                    .replace("省","")
                    .replaceAll("市","");
            districtTextView.setText(districtText);
            isChange = true;
        }).setTitleText("请选择您所在的地区")
                .setTextColorCenter(getColor(R.color.textGeneral)) //设置选中项文字颜色
                .setContentTextSize(20)
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setContentTextSize(14)//滚轮文字大小
                .setTitleSize(16)//标题文字大小
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .setTitleColor(getColor(R.color.textGeneral))//标题文字颜色
                .setSubmitColor(getColor(R.color.Theme))//确定按钮文字颜色
                .setCancelColor(getColor(R.color.Theme))//取消按钮文字颜色
                .setTitleBgColor(getColor(R.color.themeColor))//标题背景颜色 Night mode
                .setBgColor(getColor(R.color.viewColor))//滚轮背景颜色 Night mode
                .isDialog(false)//是否显示为对话框样式
                .build();

        pvOptions.setPicker(options1Items, options2Items);//三级选择器
        pvOptions.show();
    }
    private ArrayList<CityJsonBean> options1Items = new ArrayList<>(); //省
    private final ArrayList<ArrayList<String>> options2Items = new ArrayList<>();//市
//    private final ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();//区
    private void initJsonData() {
        //解析数据 （省市区三级联动）
        String JsonData = getJson(this, "province.json");//获取assets目录下的json文件数据
        ArrayList<CityJsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        //  添加省份数据
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三级）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市
                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().isEmpty()) {
                    City_AreaList.add("");
                } else {
                    City_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }
            //添加城市数据
            options2Items.add(CityList);
//            //添加地区数据
//            options3Items.add(Province_AreaList);
        }
    }
    public ArrayList<CityJsonBean> parseData(String result) {//Gson 解析
        ArrayList<CityJsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                CityJsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), CityJsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    public void finish() {
        if(isChange){
            final MyProgressDialog myProgressDialog = new MyProgressDialog(this);
            myProgressDialog.setTitleStr("信息修改");
            myProgressDialog.setHintStr("正在修改信息，请稍等...");
            myProgressDialog.show();
            new Thread(()-> {
                PersonalFragment.refreshInfo = true;
                //保存个人信息
                if (nicknameText != null) {
                    loginInformation.setNickname(nicknameText);
                }

                if (introduceText != null) {
                    loginInformation.setNickname(introduceText);
                }


                if (sexText != null) {
                    loginInformation.setSex(sexText);
                }

                if (birthdayText != 0) {
                    loginInformation.setBirthday(birthdayText);
                }

                if (districtText != null) {
                    loginInformation.setDistrict(districtText);
                }
                //保存信息
                Hawk.put("login_information", loginInformation);

                User user = new User();
                user.setNickname(nicknameText);
                user.setIntro(introduceText);
                boolean b = UserHttpUtils.editUserInfo(user);

                runOnUiThread(() -> {
                    if(b){
                        MyToast.show("信息修改成功",true);
                    }else {
                        MyToast.show("信息修改失败",false);
                    }
                    myProgressDialog.dismiss();
                    super.finish();
                });
            }).start();
        }else {
            super.finish();
        }
    }
    private final Handler uploadImageHandler = new Handler(Looper.getMainLooper());

    //文件读取JSON
    public String getJson(Context context, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    //获取文件名
    public static String getFileNameFromContentUri(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (displayNameIndex >= 0) {
                        result = cursor.getString(displayNameIndex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_RETURN_CODE: //执行裁剪
                if(data != null){
                    ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                    if(resultPhotos == null || resultPhotos.size() == 0){
                        runOnUiThread(() -> MyToast.show("图片选择失败",false));
                        return;
                    }
                    Photo photo = resultPhotos.get(0);
                    Intent intent = new Intent(InformationChangeActivity.this, CropImageActivity.class);

                    CropImageActivity.xyScale[0] = 1; //设置裁剪比例X
                    CropImageActivity.xyScale[1] = 1;  //设置裁剪比例Y
                    CropImageActivity.uri = photo.uri;
                    startActivityForResult(intent,RESIZE_REQUEST_CODE);
                }
                break;
            case RESIZE_REQUEST_CODE: //裁剪完成
                if (data != null) {
                    if(CroppedImageBitmap == null){
                        MyToast.show("图片读取失败，请重试", Toast.LENGTH_LONG,false);
                        return;
                    }
                    avatarImageView.setImageBitmap(CroppedImageBitmap);
                    Uri uri = CropImageActivity.uri;
                    String fileName = getFileNameFromContentUri(this, uri);

                    final MyProgressDialog myProgressDialog = new MyProgressDialog(this);
                    myProgressDialog.setTitleStr("上传图片");
                    myProgressDialog.setHintStr("正在上传，请稍等...");
                    myProgressDialog.setCanceledOnTouchOutside(false);
                    myProgressDialog.show();
                    new Thread(() -> {
                        boolean b = UserHttpUtils.uploadImage(0, CroppedImageBitmap, fileName);
                        runOnUiThread(() -> {
                            if(b){
                                MyToast.show("头像上传成功", Toast.LENGTH_LONG,true);
                                PersonalFragment.refreshInfo = true;
                            }else {
                                MyToast.show("头像上传失败，请重试", Toast.LENGTH_LONG,false);
                            }
                            myProgressDialog.dismiss();
                        });
                    }).start();
                }
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        uploadImageHandler.removeCallbacksAndMessages(null);
    }
}