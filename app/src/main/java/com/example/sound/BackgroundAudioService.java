/**
 *
 */
package com.example.sound;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;
import android.util.Log;

/**
 * @author lynn
 */
public class BackgroundAudioService extends Service implements
        OnCompletionListener, MediaPlayer.OnErrorListener {
    private static final String TAG = "BackgroundAudioService";
    MediaPlayer mediaPlayer;
    Intent playbackServiceIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mediaPlayer = MediaPlayer.create(this, R.raw.dog);// raw/s.mp3

        //called async when the media has finished playing
        mediaPlayer.setOnCompletionListener(this);
        Log.d(TAG, "SERVICE in oncreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }
        Log.d(TAG, "SERVICE in onStartCommand");
        return START_STICKY;
    }

    public void onDestroy() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        Log.d(TAG, "SERVICE in onDestroy");
    }

    public void onCompletion(MediaPlayer _mediaPlayer) {
        //stop when done
        //stopSelf();
    }

    /* (non-Javadoc)
     * @see android.media.MediaPlayer.OnErrorListener#onError(android.media.MediaPlayer, int, int)
     * this is called when something goes wrong
     */
    @Override
    public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
        Log.d(TAG, "In BackgroundAudioService");
        return false;
    }

}