package com.example.tangjie.news;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 波 on 2017/12/30.
 */

public class SplashActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private int[] drawable = {R.drawable.view1,R.drawable.view2,R.drawable.view3};
    private List<View> viewList ;
    private Intent intentService;

    private List<Contentmoudel> first_page_list  = new ArrayList<>();
    private List<Contentmoudel> international_list  = new ArrayList<>();
    private List<Contentmoudel> army_list  = new ArrayList<>();
    private List<Contentmoudel> society_list  = new ArrayList<>();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    String respose0 = (String) msg.obj;
                    HttpUtils.parseFirstPage(respose0,first_page_list);
                    break;
                case 1:
                    String respose1 = (String) msg.obj;
                    try {
                        HttpUtils.parseWithJsoup(respose1,international_list);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    String respose2 = (String) msg.obj;
                    try {
                        HttpUtils.parseWithJsoup(respose2,army_list);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    String respose3 = (String) msg.obj;
                    try {
                        HttpUtils.parseWithJsoup(respose3,society_list);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intentService = new Intent(SplashActivity.this,MusicService.class);
        //判断是否第一次启动应用
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
        Boolean isSkip = sharedPreferences.getBoolean("isSkip",false);
        //isSkip=false;

        //获取网页数据
        final String url[] = {"https://news.qq.com/","http://news.qq.com/world_index.shtml",
                "http://mil.qq.com/mil_index.htm","http://society.qq.com/"};
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0;i<url.length;i++){
                    String response = HttpUtils.sendRequestWithHttpUrlConnection(url[i]);
                    Message message = new Message();
                    message.obj = response;
                    message.what = i;
                    handler.sendMessage(message);
                }
            }
        }).start();

        if (isSkip == false){
            startService(intentService);
            setContentView(R.layout.activity_spash);
            initViews();
            SplashViewPagerAdapter adapter = new SplashViewPagerAdapter(viewList);
            viewPager.setAdapter(adapter);
            viewPager.setPageTransformer(true,new PageTransformerListener());       //监听View滑动的位置变化
            viewPager.setCurrentItem(0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isSkip",true);
            editor.commit();
        }else {
            setContentView(R.layout.activity_listcontent);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    intent.putExtra("first_page_list",(Serializable) first_page_list);
                    intent.putExtra("international_list",(Serializable) international_list);
                    intent.putExtra("army_list",(Serializable) army_list);
                    intent.putExtra("society_list",(Serializable) society_list);
                    startActivity(intent);
                    finish();
                }
            },2500);
        }
    }

    private void initViews(){
        viewPager = (ViewPager) findViewById(R.id.splash_viewpager);
        viewList = new ArrayList<View>();
        View view;
        for (int i = 0; i < drawable.length; i++){
            if (i == (drawable.length - 1)){
                view = LayoutInflater.from(this).inflate(R.layout.lastest_page,null);
                ImageView imageView = (ImageView) view.findViewById(R.id.splash_lastest_page);
                imageView.setImageResource(drawable[i]);
                Button button = (Button) view.findViewById(R.id.get_into_start);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                        stopService(intentService);
                            intent.putExtra("first_page_list", (Serializable) first_page_list);
                            intent.putExtra("international_list", (Serializable) international_list);
                            intent.putExtra("army_list", (Serializable) army_list);
                            intent.putExtra("society_list", (Serializable) society_list);
                            startActivity(intent);
                            finish();
                    }
                });

            }else {
                view = LayoutInflater.from(this).inflate(R.layout.viewpager_item,null);
                ImageView imageView = (ImageView) view.findViewById(R.id.splash_item);
                imageView.setImageResource(drawable[i]);
            }
            viewList.add(view);
        }
    }

    class PageTransformerListener implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                page.setAlpha(1);
                page.setTranslationX(0);
                page.setScaleX(1);
                page.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                page.setAlpha(1 - position);

                // Counteract the default slide transition
                page.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                page.setAlpha(0);
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopService(intentService);
    }
}
