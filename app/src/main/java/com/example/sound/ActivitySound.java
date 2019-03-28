package com.example.sound;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ActivitySound extends Activity implements MediaPlayer.OnCompletionListener, OnSeekBarChangeListener ,SoundPool.OnLoadCompleteListener{
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
        sb.setProgress(100); //have it start maxed
        sb.setOnSeekBarChangeListener(this);

        //get soundpool object
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            sp = new SoundPool.Builder()
                    .setMaxStreams(6)       //can have a max of 6 streams, add a seventh and the first rolls off the queue
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

    /**
     * Called after {@link #onRestoreInstanceState}, {@link #onRestart}, or
     * {@link #onPause}, for your activity to start interacting with the user.
     * This is a good place to begin animations, open exclusive-access devices
     * (such as the camera), etc.
     * <p/>
     * <p>Keep in mind that onResume is not the best indicator that your activity
     * is visible to the user; a system window such as the keyguard may be in
     * front.  Use {@link #onWindowFocusChanged} to know for certain that your
     * activity is visible to the user (for example, to resume a game).
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onRestoreInstanceState
     * @see #onRestart
     * @see #onPostResume
     * @see #onPause
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Called as part of the activity lifecycle when an activity is going into
     * the background, but has not (yet) been killed.  The counterpart to
     * {@link #onResume}.
     * <p/>
     * <p>When activity B is launched in front of activity A, this callback will
     * be invoked on A.  B will not be created until A's {@link #onPause} returns,
     * so be sure to not do anything lengthy here.
     * <p/>
     * <p>This callback is mostly used for saving any persistent state the
     * activity is editing, to present a "edit in place" model to the user and
     * making sure nothing is lost if there are not enough resources to start
     * the new activity without first killing this one.  This is also a good
     * place to do things like stop animations and other things that consume a
     * noticeable amount of CPU in order to make the switch to the next activity
     * as fast as possible, or to close resources that are exclusive access
     * such as the camera.
     * <p/>
     * <p>In situations where the system needs more memory it may kill paused
     * processes to reclaim resources.  Because of this, you should be sure
     * that all of your state is saved by the time you return from
     * this function.  In general {@link #onSaveInstanceState} is used to save
     * per-instance state in the activity and this method is used to store
     * global persistent data (in content providers, files, etc.)
     * <p/>
     * <p>After receiving this call you will usually receive a following call
     * to {@link #onStop} (after the next activity has been resumed and
     * displayed), however in some cases there will be a direct call back to
     * {@link #onResume} without going through the stopped state.
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onResume
     * @see #onSaveInstanceState
     * @see #onStop
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mp!= null)
            doStopKittyCity(null);
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
        mp = MediaPlayer.create(this, R.raw.kittycity);  //on completion should be in prepared state
        mp.start();
        mp.setOnCompletionListener(this);
        setMediaPlayerButtonState(false);
    }

    public void doStopKittyCity(View v) {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
        setMediaPlayerButtonState(true);
    }

    //toggles buttons
    private void setMediaPlayerButtonState(boolean bState) {
        bdoStart.setEnabled(bState);
        bdoStop.setEnabled(!bState);
    }
}
