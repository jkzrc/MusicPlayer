package com.example.MusicPlayer;

import com.example.seekbar.R;

import android.R.integer;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MusicPlayer extends Activity {
	private Button mControlButton;
	private TextView mPassTimeTextView;
	private TextView mAllTimeTextView;
	private SeekBar mMusicSeekBar;
	private MyOnclickListener mMyOnclickListener;
	public static String CHANGE_DURATION = "MusicPlayerChangeDuration";
	private static int mMusicStatus = MusicService.UNKNOWN;
	public static String RESOURCE = "/mnt/sdcard/when you say nothing at all.mp3";
	public static String MUSIC_SOURCE = "MusicPlayer.SOURCE";
	private int mDuration;
	private int mCurrentDuration;
	private String TAG = "MusicPlayer";
	private UpdateUiReceiver mUpdateUiReceiver;
	private MySeekbarChangeListener mSeekbarChangeListener;
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e(TAG, "onDestroy");
		Intent intent = new Intent();
		intent.setClass(MusicPlayer.this, MusicService.class);
		stopService(intent);
		mMusicStatus = MusicService.UNKNOWN;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.e(TAG, "onPause");
		unregisterReceiver(mUpdateUiReceiver);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.e(TAG, "onRestart");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setupReceiver();
		Log.e(TAG, "onResume");
		//mMusicSeekBar.setMax(mDuration);
		//updateUI();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.e(TAG, "onStart");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.e(TAG, "onStop");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		mMyOnclickListener = new MyOnclickListener();
		mSeekbarChangeListener = new MySeekbarChangeListener();
		setContentView(R.layout.activity_main);
		findview();
		mControlButton.setOnClickListener(mMyOnclickListener);
		mMusicSeekBar.setOnSeekBarChangeListener(mSeekbarChangeListener);
		

	}

	private void findview() {
		mControlButton = (Button) findViewById(R.id.btcontrol);
		mPassTimeTextView = (TextView) findViewById(R.id.tvPasstime);
		mAllTimeTextView = (TextView) findViewById(R.id.tvtime);
		mMusicSeekBar = (SeekBar) findViewById(R.id.sbmusic);

	}
	
	private void setupReceiver(){
		mUpdateUiReceiver = new UpdateUiReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MusicService.DURATION);
		intentFilter.addAction(MusicService.MUSIC_STATUS);
		intentFilter.addAction(MusicService.DURATION_CHANGE);
		registerReceiver(mUpdateUiReceiver, intentFilter);
	}

	public class MyOnclickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			if (mMusicStatus == MusicService.UNKNOWN) {
				intent.setClass(MusicPlayer.this, MusicService.class);
				Bundle bundle = new Bundle();
				bundle.putString(MUSIC_SOURCE, RESOURCE);
				intent.putExtras(bundle);
				startService(intent);
			}else if(mMusicStatus == MusicService.PLAY){
				sendBroadcastIntent(MusicService.MUSIC_CONTROL, MusicService.PAUSE);
			}else if(mMusicStatus == MusicService.PAUSE){
				sendBroadcastIntent(MusicService.MUSIC_CONTROL, MusicService.PLAY);
			}else if(mMusicStatus == MusicService.COMPLETED){
				sendBroadcastIntent(MusicService.MUSIC_CONTROL, MusicService.PLAY);
			}
		}

	}
	
	public class MySeekbarChangeListener implements OnSeekBarChangeListener{
		private int mProgress;
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			if(fromUser){
				mProgress = progress;
				mPassTimeTextView.setText(showTime(mProgress));
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			sendBroadcastIntent(MusicPlayer.CHANGE_DURATION,mProgress);
			mCurrentDuration = mProgress;
		}
		
	}

	private String showTime(int ms) {
		int second;
		int minute;
		int temp;
		temp = ms / 1000;
		second = temp % 60;
		temp = temp/60;
		minute = temp %60;
		return String.format("%02d:%02d", minute,second);
	}
	
	public void sendBroadcastIntent(String action,int value){
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.setAction(action);
		bundle.putInt(action, value);
		intent.putExtras(bundle);
		sendBroadcast(intent);
	}

	private void updateUI(){
		mAllTimeTextView.setText(showTime(mDuration));
		mMusicSeekBar.setProgress(mCurrentDuration);
		mPassTimeTextView.setText(showTime(mCurrentDuration));
		if(mMusicStatus == MusicService.PLAY){
			mControlButton.setText("ÔÝÍ£");
		}else {
			mControlButton.setText("²¥·Å");
		}
	}
	public class UpdateUiReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			Bundle bundle;
			if (action.equals(MusicService.DURATION)) {
				bundle = intent.getExtras();
				mDuration = bundle.getInt(MusicService.DURATION);
				mMusicSeekBar.setMax(mDuration);
			}else if(action.equals(MusicService.DURATION_CHANGE)){
				bundle = intent.getExtras();
				mCurrentDuration = bundle.getInt(MusicService.DURATION_CHANGE);
			}else if(action.equals(MusicService.MUSIC_STATUS)){
				bundle = intent.getExtras();
				mMusicStatus = bundle.getInt(MusicService.MUSIC_STATUS);
			}
			
			updateUI();
		}

	}

}
