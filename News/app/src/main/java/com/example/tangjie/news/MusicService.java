package com.example.tangjie.news;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by 波 on 2018/1/4.
 */

public class MusicService extends Service {
    private MediaPlayer mp;

    @Override
    public void onCreate() {
        super.onCreate();
        mp = new MediaPlayer();
        mp = MediaPlayer.create(MusicService.this,R.raw.happyorsad);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        Runnable myRun = new Runnable() {
            @Override
            public void run() {
                mp.start();
            }
        };
        Thread playMusicThread = new Thread(myRun);
        playMusicThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mp != null){
            mp.stop();
            mp.release();
        }
    }
}
