package com.example.tangjie.news;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {


    private TextView register;
    private EditText uid;
    public static final int SHOW_RESPONSE=1;
    public Handler handler=new Handler() {
        public void handleMessage(Message msg)
        {
            switch (msg.what){
                case SHOW_RESPONSE:
                    String response=(String)msg.obj;
                    if (response.equals("登录失败")){Toast.makeText(LoginActivity.this,response,Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent = new Intent();
                        intent.putExtra("username",response);
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        uid=(EditText)findViewById(R.id.user);
        final Button login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pwd=(EditText)findViewById(R.id.password);
                String id=uid.getText().toString().trim();
                String pw=pwd.getText().toString().trim();
                String type = "login";
                if(id.equals("") || pw.equals("")){
                    Toast.makeText(LoginActivity.this,"请输入完整信息",Toast.LENGTH_SHORT).show();
                }else {
                    SendByHttpClient(id,pw,type);
                }
            }
        });
        registerOnclik();
    }

    public void SendByHttpClient(final String id, final String pw,final String type){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpclient=new DefaultHttpClient();
                    HttpPost httpPost=new HttpPost("http://192.168.155.3:8080/Login");//服务器地址，指向Servlet
                    //HttpPost httpPost=new HttpPost("http://172.18.112.216:8080/Login");//服务器地址，指向Servlet
                    List<NameValuePair> params=new ArrayList<>();//将id和pw装入list
                    params.add(new BasicNameValuePair("ID",id));
                    params.add(new BasicNameValuePair("PW",pw));
                    params.add(new BasicNameValuePair("type",type));
                    final UrlEncodedFormEntity entity=new UrlEncodedFormEntity(params,"utf-8");//以UTF-8格式发送
                    httpPost.setEntity(entity);
                    HttpResponse httpResponse= httpclient.execute(httpPost);
                    if(httpResponse.getStatusLine().getStatusCode()==200)//在200毫秒之内接收到返回值
                    {
                        HttpEntity entity1=httpResponse.getEntity();
                        String response= EntityUtils.toString(entity1, "utf-8");//以UTF-8格式解析
                        Message message=new Message();
                        message.what=SHOW_RESPONSE;
                        if (response.equals("登录失败")){
                            message.obj = response;
                        }else {
                            message.obj= uid.getText().toString().trim();
                        }
                        handler.sendMessage(message);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void registerOnclik(){
        register = (TextView)findViewById(R.id.register);
        SpannableString spannable = new SpannableString("还没有账号？点击这里注册");
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivityForResult(new Intent(LoginActivity.this, RegisterActivity.class),2);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(true);
            }
        }, 8, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        register.append(spannable);
        register.setMovementMethod(LinkMovementMethod.getInstance());  //很重要，点击无效就是由于没有设置这个引起
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case 2:
                if (resultCode == RESULT_OK)
                {
                    String returnedData = data.getStringExtra("rusername");
                    uid.setText(returnedData);
                }
                break;
            default:
        }
    }
}
