package com.example.MusicPlayer;

import com.example.seekbar.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MusicPlayer extends Activity{
	private Button mControlButton;
	private TextView mPassTimeTextView;
	private TextView mAllTimeTextView;
	private SeekBar mMusicSeekBar;
	private MyOnclickListener mMyOnclickListener;
	public static String CHANGE_DURATION="MusicPlayerChangeDuration";
	private static int mMusicStatus = MusicService.UNKNOWN;
	public static String RESOURCE = "/mnt/sdcard/when you say nothing at all.mp3";
	public static String MUSIC_SOURCE = "MusicPlayer.SOURCE";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mMyOnclickListener = new MyOnclickListener();
		setContentView(R.layout.activity_main);
		findview();
		mControlButton.setOnClickListener(mMyOnclickListener);
	}
	private void findview(){
		mControlButton = (Button)findViewById(R.id.btcontrol);
		mPassTimeTextView = (TextView)findViewById(R.id.tvPasstime);
		mAllTimeTextView = (TextView)findViewById(R.id.tvtime);
		mMusicSeekBar = (SeekBar)findViewById(R.id.sbmusic);

	}
	public class MyOnclickListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			if(mMusicStatus == MusicService.UNKNOWN){
				intent.setClass(MusicPlayer.this, MusicService.class);
				Bundle bundle = new Bundle();
				bundle.putString(MUSIC_SOURCE, RESOURCE);
				intent.putExtras(bundle);
				startService(intent);
			}
		}
		
	}
	
}
