package com.example.sound;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ActivitySound extends Activity implements MediaPlayer.OnCompletionListener,  OnSeekBarChangeListener ,SoundPool.OnLoadCompleteListener{
    private static final int MAX_STREAMS = 10;
    private static final float LEFTVOLUME = 1;
    private static final float RIGHTVOLUME = 1;
    private static final int PRIORITY = 0;
    private static final int LOOPFOREVER = -1;
    private static final int LOOPNOT = 0;
    private static final float RATE = 1;
    private static final int UNINITIALIZED = -1;
    private static final String TAG = "ActivitySound";

    SoundPool sp = null;

    int trackNap;
    int trackMosquito;
    int trackSmite;

    Button bdoNap;
    Button bdoWakeup;
    Button bdoMosquito;
    Button bdoMosquitoSwat;
    Button bdoStart;
    Button bdoStop;
    SeekBar sb;

    //gonna track number times start a stream
    List<Integer> mosquitoStream = new ArrayList<Integer>();

    int napStream = UNINITIALIZED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);

        //pointers to buttons
        bdoNap = (Button) findViewById(R.id.button3);
        bdoWakeup = (Button) findViewById(R.id.button4);
        bdoMosquito = (Button) findViewById(R.id.button5);
        bdoMosquitoSwat = (Button) findViewById(R.id.button6);
        bdoStart = (Button) findViewById(R.id.button1);
        bdoStop = (Button) findViewById(R.id.button2);
        sb = (SeekBar) findViewById(R.id.seekBarSound);
        sb.setMax(100);
        sb.setProgress(50); //have it start maxed
        sb.setOnSeekBarChangeListener(this);

        //get soundpool object
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            sp = new SoundPool.Builder()
                    .setMaxStreams(MAX_STREAMS)       //can have a max of MAX_STREAMS streams, add another and the first rolls off the queue
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
           sp = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);    //srcQuality Currently has no effect. Use 0 for the default.
        }

        //listen for when following loads are done
        sp.setOnLoadCompleteListener(this);

        //load our sounds
        trackNap = sp.load(this, R.raw.snore, 0);
        trackMosquito = sp.load(this, R.raw.mosquito, 0);
        trackSmite = sp.load(this, R.raw.flyswat, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mp!= null)
            doStopKittyCity(null);
        stopNetworkMP();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sound_menu, menu);
        return true;
    }


    //*****************************************************************
    public void doNap(View v) {
        //create stream, the int returned is the value you use to clobber it
        if (napStream == UNINITIALIZED) {
            napStream = sp.play(trackNap, LEFTVOLUME, RIGHTVOLUME, PRIORITY, LOOPFOREVER, RATE);
            Log.d(TAG, "starting nap stream " + Integer.toString(napStream));
        }
        setNapState(true);
    }

    public void doWakeup(View v) {
        if (napStream != UNINITIALIZED)
            sp.stop(napStream);
        napStream = UNINITIALIZED;
        setNapState(false);
    }

    /**
     * enables and disables the buttons
     * @param bVal
     */
    private void setNapState(boolean bVal) {
        bdoWakeup.setEnabled(bVal);
        bdoNap.setEnabled(!bVal);
        //bdoMosquitoSwat.setEnabled(areMosquitoes);
    }
    //*****************************************************************
    public void doMosquitoSound(View v) {
        //create stream, the int returned is the value you use to clobber it
        int stream = sp.play(trackMosquito, LEFTVOLUME, RIGHTVOLUME, PRIORITY, LOOPFOREVER, RATE);

        //if you hit button multiple times you start multiple streams, so save them all in a list
        mosquitoStream.add(stream);

        setMosquitoSwatButtonState(true);
        Log.d(TAG, "starting stream " + Integer.toString(stream));
    }

    public void doMosquitoSwatSound(View v) {
        sp.play(trackSmite, LEFTVOLUME, RIGHTVOLUME, PRIORITY, LOOPNOT, RATE);
        Iterator<Integer> myStreamIterator = mosquitoStream.iterator();
        if (myStreamIterator.hasNext()) {
            int i = myStreamIterator.next();
            myStreamIterator.remove();
            Log.d(TAG, "stopping stream " + Integer.toString(i));
            sp.stop(i);
        }

        setMosquitoSwatButtonState(mosquitoStream.size() > 0);
    }
    /**
     * enables and disables the swat button
     * @param bVal
     */
    private void setMosquitoSwatButtonState(boolean bVal) {
        bdoMosquitoSwat.setEnabled(bVal);
    }



    /* (non-Javadoc)
         * @see android.widget.SeekBar.OnSeekBarChangeListener#onProgressChanged(android.widget.SeekBar, int, boolean)
         */
    @Override
    public void onProgressChanged(SeekBar seekBar, int arg1, boolean arg2) {
        int prog = seekBar.getProgress();
        if (trackMosquito != UNINITIALIZED) {

            //calculating what volume to set all the buzzing
            float volume = (float) prog / 100;

            Iterator<Integer> myStreamIterator = mosquitoStream.iterator();
            while (myStreamIterator.hasNext()) {
                int i = myStreamIterator.next();

                //setting the stream volume
                sp.setVolume(i, volume, volume);
                Log.d(TAG, "adjusting volume on stream " + Integer.toString(i));
            }
        }
    }

    /* (non-Javadoc)
     * @see android.widget.SeekBar.OnSeekBarChangeListener#onStartTrackingTouch(android.widget.SeekBar)
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see android.widget.SeekBar.OnSeekBarChangeListener#onStopTrackingTouch(android.widget.SeekBar)
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    /**
     * Called when a sound has completed loading.
     *
     * @param soundPool SoundPool object from the load() method
     * @param sampleId  the sample ID of the sound loaded.
     * @param status    the status of the load operation (0 = success)
     */
    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        if (sampleId == trackNap)
            bdoNap.setEnabled(true);

        if (sampleId == trackMosquito)
            bdoMosquito.setEnabled(true);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    MediaPlayer mp = null;

    /**
     * Called when the end of a media source is reached during playback.
     *
     * @param mp the MediaPlayer that reached the end of the file
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        doStopKittyCity(null);
        Log.d(TAG, "Stopping Mediaplayer from onCompletion");
    }

    public void doStartKittyCity(View v) {
        doStopKittyCity(null);
        mp = MediaPlayer.create(this, R.raw.kittycity);  //on completion should be in prepared state
        mp.start();
        mp.setOnCompletionListener(this);
        setLocalMediaPlayerButtonState(false);
    }

    public void doStopKittyCity(View v) {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
        setLocalMediaPlayerButtonState(true);
    }

    //toggles buttons
    private void setLocalMediaPlayerButtonState(boolean bState) {
        bdoStart.setEnabled(bState);
        bdoStop.setEnabled(!bState);
    }

    private void setNetworkMediaPlayerButtonState(boolean bState) {
        (findViewById(R.id.buttonStartURL)).setEnabled(bState);
        (findViewById(R.id.buttonStopURL)).setEnabled(!bState);
    }

    MediaPlayer mp1;
    public void doNetworkMP(View view) {
        stopNetworkMP();
        setNetworkMediaPlayerButtonState(false);
        //USE PHYSICAL DEVICE FOR THIS!
        //It seems that MediaPlayer streaming media on emulators fail with logcat error
        // E/MediaPlayerNative: error (1, -2147483648), the '1' value corresponds to the constant
        // in MediaPlayer.MEDIA_ERROR_UNKNOWN. -2147483648 corresponds to hexadecimal 0x80000000
        // which is defined as UNKNOWN_ERROR in frameworks/native/include/utils/Errors.h

        //be careful, some file formats are supported some are not see
        //https://developer.android.com/guide/topics/media/media-formats
        String s = new String("https://download.samplelib.com/mp3/sample-3s.mp3");
//        String s = new String("https://filesamples.com/samples/audio/mp3/sample4.mp3");
//        String s = new String("https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_480_1_5MG.mp4");

        mp1= new MediaPlayer();
        mp1.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        try{
            mp1.setDataSource(s);
            mp1.prepareAsync();     //do the preparation in seperate thread
        } catch (IOException e) {
            e.printStackTrace();
        }

        //media will be started after completion of preparing...
        mp1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer player) {
                player.start();
            }
        });

        //when media finishes playing clean up player and reset button state
        mp1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer player) {
                doStopNetworkMP(null);
            }
        });
    }

    public void stopNetworkMP(){
        if (mp1 != null) {
            mp1.stop();
            mp1.release();
            mp1 = null;
        }
    }
    public void doStopNetworkMP(View view) {
        setNetworkMediaPlayerButtonState(true);
        stopNetworkMP();
    }
}
