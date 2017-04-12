package com.example.sound;

import com.example.sound.R;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController.MediaPlayerControl;

public class TestService extends Activity implements OnClickListener{

    Button startPlaybackButton, stopPlaybackButton;
    Intent playbackServiceIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);

	startPlaybackButton = (Button) this.findViewById(R.id.StartPlaybackButton);
	stopPlaybackButton = (Button) this.findViewById(R.id.StopPlaybackButton);

	startPlaybackButton.setOnClickListener(this);
	stopPlaybackButton.setOnClickListener(this);

	playbackServiceIntent = new Intent(this, BackgroundAudioService.class);
    }

    public void onClick(View v) {
	if (v == startPlaybackButton) {
	    //the following call starts the service 
	    startService(playbackServiceIntent);
	} else if (v == stopPlaybackButton) {
	    stopService(playbackServiceIntent);
	}
    }
}