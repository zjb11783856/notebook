package com.boge.android_0618_notebook.utils;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

public class MyMediraPlayer {
	private static final MyMediraPlayer myPlayer = new MyMediraPlayer();
	private MediaPlayer player;
	private static Context context;
	private Thread thread;

	public MyMediraPlayer() {

	}

	public static MyMediraPlayer getInstance(Context context2) {
		context = context2;
		return myPlayer;
	}

	public void play(String path) {

		try {
			player = new MediaPlayer();
			player.reset();
			player.setDataSource(path);
			player.prepare();
			player.start();

			new Thread(new Runnable() {

				@Override
				public void run() {
					while (player.isPlaying()) {

					}
					// ·¢ËÍ¹ã²¥
					Intent intent = new Intent();
					intent.setAction("PLAY_END");
					context.sendBroadcast(intent);
				}
			}).start();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void stop() {
		if (isExist()) {
			Log.i("boge", "stop");
			player.stop();
			player.release();
		}

	}

	public boolean isExist() {
		if (player != null) {
			return true;
		}
		return false;
	}

	public boolean isPlaying() {
		Log.i("boge", "state="+player.isPlaying());
		if (player.isPlaying()) {
			return true;
		}

		return false;

	}

}
