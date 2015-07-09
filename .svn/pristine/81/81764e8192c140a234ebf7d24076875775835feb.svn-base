package com.boge.android_0618_notebook.utils;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;

public class MyMediarecorder {
	private File file = null;
	private MediaRecorder recorder;
	private static final MyMediarecorder myRecorder = new MyMediarecorder();
	private static Context context;
	private boolean isRecord = true;// Â¼Òô×´Ì¬

	private MyMediarecorder() {
		if (!isExist()) {
			recorder = new MediaRecorder();
		}
	}

	public static MyMediarecorder getInstance(Context context2) {
		context = context2;
		return myRecorder;
	}

	/**
	 * ¿ªÊ¼Â¼Òô
	 */
	public void start(String path) {
		try {

			file = new File(path);
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			recorder.setOutputFile(file.getAbsolutePath());
			recorder.prepare();
			recorder.start();

			calRecordTime();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Í£Ö¹Â¼Òô
	 */
	public void stop() {
		if (isExist()) {
			recorder.stop();
			recorder.release();
			isRecord = false;
		}

	}

	public boolean isExist() {
		if (recorder != null) {
			return true;
		}
		return false;
	}

	/**
	 * ¼ÆËãÂ¼ÒôÊ±¼ä
	 */
	private void calRecordTime() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				int time = 0;
				while (isRecord) {
					time++;
					Intent intentTime = new Intent();
					intentTime.setAction("TIME");
					intentTime.putExtra("time", time);
					context.sendBroadcast(intentTime);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		}).start();

	}
}
