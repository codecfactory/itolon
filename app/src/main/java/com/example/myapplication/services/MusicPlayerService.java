package com.example.myapplication.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.model.CartModel;
import com.example.myapplication.prefrences.Constants;
import com.example.myapplication.utils.RxBus;

import java.io.IOException;
import java.util.ArrayList;

public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    Context context;
    private static final String ACTION_PLAY = "PLAY";
    private static final String TAG = "SONG SERVICE";
    MediaPlayer mediaPlayer;
    private int currentTrack = 0;
    ArrayList<String> list;

    public MusicPlayerService() {
        context = getBaseContext();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getBaseContext();
//        list = (ArrayList<String>)intent.getSerializableExtra("arraylist");
        list = Constants.INSTANCE.getServiceSongsArrayList();
        int count = 0;
        Log.d(TAG, "total count:" + list.size());
        //playing song one by one
        for (String string : list) {
            //play(string);
            count++;
            Log.d(TAG, "count:" + list);
        }
        play(currentTrack);
        Log.d(TAG, "count:" + count);
        if (count == list.size()) {
            //stopSelf();
            Log.d(TAG, "stoping service");
            //mediaPlayer.setOnCompletionListener(this);
        } else {
            Log.d(TAG, "not stoping service");
        }

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            Log.d(TAG, "oncommat");
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {

//        Toast.makeText(this, "Service was Created", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onStart(Intent intent, int startId) {
        // Perform your long running operations here.
//        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
//        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
//        Log.d("service", "destroyed");
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // TODO Auto-generated method stub

    }

    private void play(int id) {


        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//            Log.d("*****begin*****", "playing");
            stopPlaying();
//            Log.d("*****begin*****", "stoping");
        } else {
//            Log.d("*****begin*****", "nothing");
        }

        Log.d("*****play count*****", "=" + currentTrack);
        Log.i("******playing", list.get(currentTrack));

        Uri myUri1 = Uri.parse(list.get(id));
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setOnPreparedListener(this);
//        getDuration();
        //mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        try {
            mediaPlayer.setDataSource(context, myUri1);
            Log.i("******playing", myUri1.getPath());
        } catch (IllegalArgumentException e) {
//            Toast.makeText(context, "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (SecurityException e) {
//            Toast.makeText(context, "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
//            Toast.makeText(context, "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mediaPlayer.prepare();
        } catch (IllegalStateException e) {
//            Toast.makeText(context, "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
//            Toast.makeText(context, "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
//                currentTrack = currentTrack + 1;
//                play(currentTrack);
                RxBus.INSTANCE.publish("");
				/* currentTrack = (currentTrack + 1) % list.size();
				 Uri nextTrack=Uri.parse(list.get(currentTrack));

				 try {
					 mediaPlayer.setDataSource(context,nextTrack);
					 mediaPlayer.prepare();
					// mediaPlayer.start();
				} catch (Exception e) {
					e.printStackTrace();
				}*/

            }
        });

        mediaPlayer.start();
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

//    private void getDuration(){
//        new Handler().postDelayed(() -> {
//            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                RxBus.INSTANCE.publish(mediaPlayer);
//
//            }
//        }, 100);
//    }

    @Override
    public void onCompletion(MediaPlayer mp) {
    }
}