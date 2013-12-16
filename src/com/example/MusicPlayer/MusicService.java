package com.example.MusicPlayer;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class MusicService extends Service {
	public static final String MUSIC_CONTROL = "MainActivity.ACTION_CONTROL";
	public static final String DURATION="MusicService.DURATION";
	public static final String DURATION_CHANGE="MusicService.CURRENT_DURATION";
	public static final String MUSIC_STATUS="MusicService.MUSIC_STATUS";

	public static final int UNKNOWN = -1;
	public static final int PLAY = 0;
	public static final int PAUSE = 1;
	public static final int COMPLETED = 2;
	
	final String TAG = "MusicService";
	private MediaPlayer mp;
	private int mDuration;
	private int mCurrentDuration;
	private Timer mTimer;
	private TimerTask mTask;
	private CommandReceiver mCommandReceiver;
	private int mMusicStatus = MusicService.UNKNOWN;
	//private UpdateReceiver upReceiver;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		setupBroadcastReceiver();
		mp = new MediaPlayer();
		mp.reset();
		createTimerTask();
		mTimer = new Timer();
		
		mp.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				//mTimer.cancel();
				sendBroadcastIntent(MusicService.MUSIC_STATUS, COMPLETED);
				mMusicStatus = MusicService.COMPLETED;
			}
		});
	}
	
	private void setupBroadcastReceiver(){
		mCommandReceiver = new CommandReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MusicService.MUSIC_CONTROL);
		filter.addAction(MusicPlayer.CHANGE_DURATION);
		registerReceiver(mCommandReceiver, filter);
	}
	private void createTimerTask() {
		mTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mCurrentDuration = mp.getCurrentPosition();
				Log.e(TAG, "mCurrentDuration is "+mCurrentDuration);
				sendBroadcastIntent(MusicService.DURATION_CHANGE, mCurrentDuration);
			}
		};
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(mp!=null){
			if(mp.isPlaying()){
				mp.pause();
			}else{
				mp.start();
			}
		}
		super.onDestroy();
	}

	public void sendBroadcastIntent(String action,int value){
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.setAction(action);
		bundle.putInt(action, value);
		intent.putExtras(bundle);
		sendBroadcast(intent);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Bundle bdBundle;
		bdBundle = intent.getExtras();
		String string;
		string = bdBundle.getString(MusicPlayer.MUSIC_SOURCE);
		Log.e(TAG, string);
		try {
			mp.setDataSource(string);
			mp.prepare();
			mp.start();
		} catch (Exception e) {
			// TODO: handle exception
		}

		mMusicStatus = MusicService.PLAY;
		mTimer.schedule(mTask, 500, 500);
		mDuration = mp.getDuration();
		sendBroadcastIntent(MusicService.DURATION,mDuration);
		sendBroadcastIntent(MusicService.MUSIC_STATUS,mMusicStatus);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public class CommandReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Bundle bdBundle;
			String action = intent.getAction();
			
			if(action.equals(MusicPlayer.CHANGE_DURATION)){
				bdBundle = intent.getExtras();
				mCurrentDuration = bdBundle.getInt(MusicPlayer.CHANGE_DURATION);
				if(mp!=null){
					mp.seekTo(mCurrentDuration);
				}
			}else if(action.equals(MusicService.MUSIC_CONTROL)){
				bdBundle = intent.getExtras();
				switch (bdBundle.getInt(MusicService.MUSIC_CONTROL)) {
				case MusicService.PLAY:
					if(mMusicStatus == MusicService.PAUSE && (!mp.isPlaying())){
						mp.start();
						sendBroadcastIntent(MusicService.MUSIC_STATUS, MusicService.PLAY);
						mMusicStatus = MusicService.PLAY;
					}
					break;
					
				case MusicService.PAUSE:
					if(mMusicStatus == MusicService.PLAY && mp.isPlaying()){
						mp.pause();
						sendBroadcastIntent(MusicService.MUSIC_STATUS, MusicService.PAUSE);
						mMusicStatus = MusicService.PAUSE;
					}
					break;
					
				default:
					break;
				} 
			}
		}

	}

}