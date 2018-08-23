package com.example.tangjie.news;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class ListcontentActivity extends AppCompatActivity {

    private TextView title;
    private ImageView imageView;
    private TextView content;

   private Handler handler = new Handler(){
       @Override
       public void handleMessage(Message msg) {
           super.handleMessage(msg);
           switch (msg.what){
               case 1:
                   String response = (String) msg.obj;

                   content.setText(response);
           }
       }
   };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listcontent);

        Intent item = getIntent();
        Bundle bundle = item.getExtras();
        Bitmap bitmap = (Bitmap)bundle.getParcelable("bitmap");
        String biaoti = item.getStringExtra("title");
        initview();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent item = getIntent();
                String url = item.getStringExtra("url");
                String response = HttpUtils.sendRequestWithHttpUrlConnection(url);
                String content_news = HttpUtils.parseNewsContent(response);
                Message message = new Message();
                message.what = 1;
                message.obj = content_news;
                handler.sendMessage(message);
            }
        }).start();

        title.setText(biaoti);
        imageView.setImageBitmap(bitmap);
    }

    public void initview(){
        title = (TextView)findViewById(R.id.content_title);
        imageView = (ImageView)findViewById(R.id.content_image);
        content = (TextView)findViewById(R.id.content);
    }
}
