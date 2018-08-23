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

public class ChangeActivity extends AppCompatActivity {

    private EditText newpwd,cof_newpwd;
    private Button surepwd;



    public static final int SHOW_RESPONSE=3;
    public Handler handler=new Handler() {
        public void handleMessage(Message msg)
        {
            switch (msg.what){
                case SHOW_RESPONSE:
                    String reponse = (String)msg.obj;
                    Toast.makeText(ChangeActivity.this,reponse,Toast.LENGTH_SHORT).show();

                    if(reponse.equals("修改成功")){
                        Intent change = new Intent();
                        setResult(RESULT_FIRST_USER,change);
                        finish();
                    }else {
                        Toast.makeText(ChangeActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_change);

        newpwd = findViewById(R.id.new_pwd);
        cof_newpwd = findViewById(R.id.cof_new_pwd);
        surepwd = findViewById(R.id.sure);
        surepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeID = getIntent();
                String id = changeID.getStringExtra("changeID");
                String spwd = newpwd.getText().toString().trim();
                String cspwd = cof_newpwd.getText().toString().trim();
                String type = "change";
                if(!spwd.equals(cspwd) || spwd.equals("") || cspwd.equals("")){
                    Toast.makeText(ChangeActivity.this,"请输入完整信息，并确定两个密码一致",Toast.LENGTH_SHORT).show();
                }else {
                    SendByHttpClient(id,spwd,cspwd,type);
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
