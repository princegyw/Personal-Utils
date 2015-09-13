package com.example.videoactivity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoActivity extends Activity implements OnPreparedListener {
	VideoView videoView;
	ProgressBar pb_buffer;
	TextView tv_buffer;
	private Uri uri = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video);
		
		uri = getIntent().getData();
		if (uri == null) {
			return;
		}
		
		pb_buffer = (ProgressBar) findViewById(R.id.progressBar_buffer);
		tv_buffer = (TextView) findViewById(R.id.textView_buffer);
		
		videoView = (VideoView) findViewById(R.id.videoView1);
		MediaController mc = new MediaController(this);
		videoView.setMediaController(mc);
		videoView.setVideoURI(uri);
		videoView.requestFocus();
		videoView.setOnPreparedListener(this);
		
		videoView.start();
		
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		pb_buffer.setVisibility(View.GONE);
		tv_buffer.setVisibility(View.GONE);
	}
}
