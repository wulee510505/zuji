package com.wulee.administrator.zuji.text2speech;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.liangmayong.text2speech.OnText2SpeechListener;
import com.liangmayong.text2speech.Text2SpeechOptions;
import com.liangmayong.text2speech.Text2SpeechService;

/**
 * Text2Speech
 * 
 * @author LiangMaYong
 * @version 1.0
 */
public class Text2Speech {

	public static final String ACTION_TEXT2SPEECH = "com.liangmayong.text2speech";
	private static boolean isInit = false;
	public static String TEXT2SPEECH_TEMP_DIR = "/text2speech/temp/";
	public static String TEXT2SPEECH_SAVE_DIR = "/text2speech/";
	public static boolean isListener = false;

	private static void init(Context context) {
		if (isInit) {
			return;
		}
		isInit = true;
		IntentFilter intentFilter = new IntentFilter(ACTION_TEXT2SPEECH);
		Text2SpeechReceiver receiver = new Text2SpeechReceiver();
		context.getApplicationContext().registerReceiver(receiver, intentFilter);
	}

	/**
	 * isSpeeching
	 * 
	 * @return boolean
	 */
	public static boolean isSpeeching() {
		return Text2SpeechService.isSpeeching();
	}

	private static class Text2SpeechReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (ACTION_TEXT2SPEECH.equals(intent.getAction())) {
				String type = intent.getStringExtra("action_type");
				if (type.equals("error")) {
					Exception exception = null;
					try {
						exception = (Exception) intent.getSerializableExtra("action_exception");
					} catch (Exception e) {
					}
					String info = intent.getStringExtra("action_info");
					defListener.onError(exception, info);
				} else if (type.equals("start")) {
					defListener.onStart();
				} else if (type.equals("completion")) {
					defListener.onCompletion();
				} else if (type.equals("prepared")) {
					defListener.onPrepared();
				} else if (type.equals("playprogress")) {
					int action_currentposition = intent.getIntExtra("action_currentposition", 0);
					int action_duration = intent.getIntExtra("action_duration", 0);
					defListener.onPlayProgress(action_currentposition, action_duration);
				} else if (type.equals("loadprogress")) {
					int action_progresslenght = intent.getIntExtra("action_progresslenght", 0);
					int action_lenght = intent.getIntExtra("action_lenght", 0);
					defListener.onLoadProgress(action_progresslenght, action_lenght);
				}
			}
		}
	}

	private static List<OnText2SpeechListener> listeners = new ArrayList<OnText2SpeechListener>();
	private static OnText2SpeechListener defListener = new OnText2SpeechListener() {
		@Override
		public void onStart() {
			if (listeners != null && !listeners.isEmpty()) {
				for (int i = 0; i < listeners.size(); i++) {
					try {
						listeners.get(i).onStart();
					} catch (Exception e) {
					}
				}
			}
			if (text2SpeechListener != null) {
				text2SpeechListener.onStart();
			}
			isListener = true;
		}

		@Override
		public void onLoadProgress(int progressLenght, int lenght) {
			if (!isListener) {
				return;
			}
			if (listeners != null && !listeners.isEmpty()) {
				for (int i = 0; i < listeners.size(); i++) {
					try {
						listeners.get(i).onLoadProgress(progressLenght, lenght);
					} catch (Exception e) {
					}
				}
			}
			if (text2SpeechListener != null) {
				text2SpeechListener.onLoadProgress(progressLenght, lenght);
			}
		}

		@Override
		public void onError(Exception e, String info) {
			if (!isListener) {
				return;
			}
			if (listeners != null && !listeners.isEmpty()) {
				for (int i = 0; i < listeners.size(); i++) {
					try {
						listeners.get(i).onError(e, info);
					} catch (Exception e2) {
					}
				}
			}
			if (text2SpeechListener != null) {
				text2SpeechListener.onError(e, info);
			}
			isListener = false;
		}

		@Override
		public void onCompletion() {
			if (!isListener) {
				return;
			}
			if (listeners != null && !listeners.isEmpty()) {
				for (int i = 0; i < listeners.size(); i++) {
					try {
						listeners.get(i).onCompletion();
					} catch (Exception e) {
					}
				}
			}
			if (text2SpeechListener != null) {
				text2SpeechListener.onCompletion();
			}
			isListener = false;
		}

		@Override
		public void onPrepared() {
			if (!isListener) {
				return;
			}
			if (listeners != null && !listeners.isEmpty()) {
				for (int i = 0; i < listeners.size(); i++) {
					try {
						listeners.get(i).onPrepared();
					} catch (Exception e) {
					}
				}
			}
			if (text2SpeechListener != null) {
				text2SpeechListener.onPrepared();
			}
		}

		@Override
		public void onPlayProgress(int currentPosition, int duration) {
			if (!isListener) {
				return;
			}
			if (listeners != null && !listeners.isEmpty()) {
				for (int i = 0; i < listeners.size(); i++) {
					try {
						listeners.get(i).onPlayProgress(currentPosition, duration);
					} catch (Exception e) {
					}
				}
			}
			if (text2SpeechListener != null) {
				text2SpeechListener.onPlayProgress(currentPosition, duration);
			}
		}
	};
	private static OnText2SpeechListener text2SpeechListener;

	public static void setOnText2SpeechListener(OnText2SpeechListener text2SpeechListener) {
		Text2Speech.text2SpeechListener = text2SpeechListener;
	}

	/**
	 * addTTSListener
	 * 
	 * @param ttsListener
	 *            ttsListener
	 */
	public static void addText2SpeechListener(OnText2SpeechListener ttsListener) {
		if (ttsListener != null && !listeners.contains(ttsListener)) {
			listeners.add(ttsListener);
		}
	}

	/**
	 * removeTTSListener
	 * 
	 * @param ttsListener
	 *            ttsListener
	 */
	public static void removeText2SpeechListener(OnText2SpeechListener ttsListener) {
		if (listeners.contains(ttsListener)) {
			listeners.remove(ttsListener);
		}
	}

	/**
	 * speech
	 * 
	 * @param context
	 *            context
	 * @param message
	 *            message
	 */
	public static void speech(Context context, Text2SpeechOptions message) {
		init(context);
		Intent intent = new Intent(context, Text2SpeechService.class);
		intent.putExtra("text", message.getText());
		intent.putExtra("spd", message.getSpd());
		intent.putExtra("readtime", message.getReadtime());
		intent.putExtra("outtime", message.getOuttime());
		intent.putExtra("is_return_temp", message.isReturnTemp());
		intent.putExtra("palyenddelete", message.isEndDelete());
		intent.putExtra("chunkLength", message.getChunkLength());
		context.startService(intent);
	}

	/**
	 * speech
	 * 
	 * @param context
	 *            context
	 * @param msg
	 *            msg
	 * @param spd
	 *            spd
	 * @param playdelete
	 *            playdelete
	 */
	public static void speech(Context context, String msg, int spd, boolean afterDelete) {
		init(context);
		Intent intent = new Intent(context, Text2SpeechService.class);
		intent.putExtra("text", msg);
		intent.putExtra("spd", spd);
		intent.putExtra("palyenddelete", afterDelete);
		context.startService(intent);
	}

	/**
	 * load
	 * 
	 * @param context
	 *            context
	 * @param msg
	 *            msg
	 * @param spd
	 *            spd
	 */
	public static void load(Context context, String msg, int spd) {
		init(context);
		Intent intent = new Intent(context, Text2SpeechService.class);
		intent.putExtra("text", msg);
		intent.putExtra("spd", spd);
		intent.putExtra("unplay", 1);
		intent.putExtra("palyenddelete", false);
		context.startService(intent);
	}

	/**
	 * load
	 * 
	 * @param context
	 *            context
	 * @param msg
	 *            msg
	 */
	public static void load(Context context, String msg) {
		init(context);
		Intent intent = new Intent(context, Text2SpeechService.class);
		intent.putExtra("text", msg);
		intent.putExtra("unplay", 1);
		intent.putExtra("palyenddelete", false);
		context.startService(intent);
	}

	/**
	 * speech
	 * 
	 * @param context
	 *            context
	 * @param msg
	 *            msg
	 * @param playdelete
	 *            playdelete
	 */
	public static void speech(Context context, String msg, boolean afterDelete) {
		init(context);
		Intent intent = new Intent(context, Text2SpeechService.class);
		intent.putExtra("text", msg);
		intent.putExtra("palyenddelete", afterDelete);
		context.startService(intent);
	}

	/**
	 * shutUp
	 * 
	 * @param context
	 *            context
	 */
	public static void shutUp(Context context) {
		init(context);
		Intent intent = new Intent(context, Text2SpeechService.class);
		intent.putExtra("action", "stop");
		context.startService(intent);
	}

	/**
	 * pause
	 * 
	 * @param context
	 *            context
	 */
	public static void pause(Context context) {
		init(context);
		Intent intent = new Intent(context, Text2SpeechService.class);
		intent.putExtra("action", "paused");
		context.startService(intent);
	}

	/**
	 * replay
	 * 
	 * @param context
	 *            context
	 */
	public static void replay(Context context) {
		init(context);
		Intent intent = new Intent(context, Text2SpeechService.class);
		intent.putExtra("action", "replay");
		context.startService(intent);
	}

	/**
	 * clearText2SpeechCache
	 * 
	 * @param context
	 *            context
	 */
	public static void clearText2SpeechCache(Context context) {
		File temp = new File(context.getCacheDir() + TEXT2SPEECH_TEMP_DIR);
		deleteFile(temp);
		File save = new File(context.getCacheDir() + TEXT2SPEECH_SAVE_DIR);
		deleteFile(save);
	}

	private static void deleteFile(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFile(files[i]);
				}
			}
			file.delete();
		} else {
		}
	}
}
