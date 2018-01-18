package com.example.administrator.registerandlogin.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.registerandlogin.R;
import com.example.administrator.registerandlogin.bean.Zhuce_bean;
import com.example.administrator.registerandlogin.util.OkHttp3Util;
import com.example.administrator.registerandlogin.bean.Denglu_bean;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    public String regex;
    private EditText phone;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);

        //手机号的正则
        regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[0-9])|(18[0,5-9]))\\d{8}$";
    }

    //点击注册   访问接口
    public void register(View view) {

        //获得输入的数据
        String myphone = phone.getText().toString();
        String mypassword = password.getText().toString();


        //判断 使用TextUtils.isEmpty()，用来判断字符串是否为空
        if (!TextUtils.isEmpty(myphone) && !TextUtils.isEmpty(mypassword)) {
            //验证手机号   regex添加的是正则
            Pattern compile = Pattern.compile(regex);
            //要测的手机号
            Matcher m = compile.matcher(myphone);
            boolean matches = m.matches();
            //如果要测的手机号和密码都成立    下面的两个条件都成立的时候  分来理解 前面为true,后边成立为true
            if (m.matches() && mypassword.length() >= 6) {

                try {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("mobile", myphone);
                    map.put("password", mypassword);
                    //去访问接口
                    OkHttp3Util.doPost("https://www.zhaoapi.cn/user/reg", map, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (response.isSuccessful()) {
                                        try {

                                            String string = response.body().string();
                                            Gson gson = new Gson();
                                            Zhuce_bean zhuce_bean = gson.fromJson(string, Zhuce_bean.class);
                                            String code = zhuce_bean.getCode();
                                            if ("0".equals(code)) {//0注册成功   1注册失败
                                                Toast.makeText(RegisterActivity.this, "注册成功,跳转到登录页面", Toast.LENGTH_SHORT).show();
                                                //注册成功后 跳到登录
                                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                //---------------------

                                            } else {
                                                Toast.makeText(RegisterActivity.this, zhuce_bean.getMsg(), Toast.LENGTH_SHORT).show();
                                            }

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                Toast.makeText(RegisterActivity.this, "帐号或密码长度不正确!!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(RegisterActivity.this, "帐号或密码不能为空!!", Toast.LENGTH_SHORT).show();
        }

    }
}
