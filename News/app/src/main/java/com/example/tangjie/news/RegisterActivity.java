package com.example.tangjie.news;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegisterActivity extends AppCompatActivity {


    public static final int SHOW_RESPONSE=2;
    public Handler handler=new Handler() {
        public void handleMessage(Message msg)
        {
            switch (msg.what){
                case SHOW_RESPONSE:
                    String response=(String)msg.obj;
                    if(response.equals("用户名已经存在") || response.equals("两次密码不一致")){
                        Toast.makeText(RegisterActivity.this,"该用户已被注册，或两次密码不一致，重新输入",Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent = new Intent();
                        intent.putExtra("rusername",response);
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
        setContentView(R.layout.activity_register);

        final Button singup = (Button)findViewById(R.id.singup);
        singup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText uid=(EditText)findViewById(R.id.user);
                EditText pwd=(EditText)findViewById(R.id.password);
                EditText cofpwd=(EditText)findViewById(R.id.cof_password);
                String id=uid.getText().toString().trim();
                String pw=pwd.getText().toString().trim();
                String cof_pw=cofpwd.getText().toString().trim();
                String type = "register";
                if (id.equals("")||pw.equals("")||cof_pw.equals("")){
                    Toast.makeText(RegisterActivity.this,"请输入完整信息",Toast.LENGTH_SHORT).show();
                }else {
                    SendByHttpClient(id,pw,cof_pw,type);
                }
            }
        });
    }

    public void SendByHttpClient(final String id, final String pw, final String cofpw,final String type){
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
                    params.add(new BasicNameValuePair("COFPW",cofpw));
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
                        message.obj=response;
                        handler.sendMessage(message);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}

