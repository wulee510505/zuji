package com.wulee.administrator.zuji.text2speech;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Text2SpeechService
 * 
 * @author LiangMaYong
 * @version 1.0
 */
@SuppressLint({ "SdCardPath", "HandlerLeak" })
public class Text2SpeechService extends Service {

	private final String apipath = "?ahbo0t4iatad4p1uc:fa4/721/ftbtbxdt7e0s5tb.f/fbfm1a6o7i4cad0.1u1";
	private DownHandler downHandler;
	private MediaPlayer mediaPlayer;
	private boolean palyenddelete = false;
	private String pathString = "";
	private String tempRoot = "";
	private String saveRoot = "";
	private int unplay = -1;
	private static boolean isSpeeching = false;

	public static boolean isSpeeching() {
		return isSpeeching;
	}

	private List<String> Stringtotrings(String string, int count) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < string.length(); i = i + count) {
			String a = "";
			if (i + count >= string.length()) {
				a = string.substring(i, string.length());
			} else {
				a = string.substring(i, i + count);
			}
			list.add(a);
		}
		return list;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	private final String StringBySHA1(String plain) {
		String re_sha1 = new String();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			md.update(plain.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

			re_sha1 = buf.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return re_sha1;
	}

	private OnCompletionListener onCompletionListener = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
			Intent intentBroad = new Intent(com.liangmayong.text2speech.Text2Speech.ACTION_TEXT2SPEECH);
			intentBroad.putExtra("action_type", "playprogress");
			intentBroad.putExtra("action_currentposition", mp.getDuration());
			intentBroad.putExtra("action_duration", mp.getDuration());
			sendBroadcast(intentBroad);
			if (palyenddelete) {
				File file = new File(pathString);
				if (file.exists()) {
					try {
						downHandler.deleteFile(file);
					} catch (Exception e) {
					}
				}
			}
			Intent intent = new Intent(com.liangmayong.text2speech.Text2Speech.ACTION_TEXT2SPEECH);
			intent.putExtra("action_type", "completion");
			isSpeeching = false;
			sendBroadcast(intent);
			onDestroy();
		}
	};
	private OnPreparedListener onPreparedListener = new OnPreparedListener() {
		@Override
		public void onPrepared(MediaPlayer mp) {
			isSpeeching = true;
			Intent intent = new Intent(com.liangmayong.text2speech.Text2Speech.ACTION_TEXT2SPEECH);
			intent.putExtra("action_type", "prepared");
			sendBroadcast(intent);
		}
	};

	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {
		public void run() {
			if (mediaPlayer == null) {
				handler.removeCallbacks(runnable);
				return;
			}
			try {
				if (mediaPlayer.isPlaying()) {
					isSpeeching = true;
					Intent intentBroad = new Intent(com.liangmayong.text2speech.Text2Speech.ACTION_TEXT2SPEECH);
					intentBroad.putExtra("action_type", "playprogress");
					intentBroad.putExtra("action_currentposition", mediaPlayer.getCurrentPosition());
					intentBroad.putExtra("action_duration", mediaPlayer.getDuration());
					sendBroadcast(intentBroad);
					handler.postDelayed(runnable, 1000);
				} else {
					isSpeeching = false;
					handler.removeCallbacks(runnable);
				}
			} catch (Exception e) {
			}
		}
	};

	public int onStartCommand(Intent intent, int flags, int startId) {
		tempRoot = getCacheDir() + com.liangmayong.text2speech.Text2Speech.TEXT2SPEECH_TEMP_DIR;
		saveRoot = getCacheDir() + com.liangmayong.text2speech.Text2Speech.TEXT2SPEECH_SAVE_DIR;
		if (intent != null) {
			try {
				if (downHandler == null) {
					downHandler = new DownHandler();
				}
				try {
					downHandler.stop();
				} catch (Exception e) {
					Intent intentBroad = new Intent(com.liangmayong.text2speech.Text2Speech.ACTION_TEXT2SPEECH);
					intentBroad.putExtra("action_type", "error");
					intentBroad.putExtra("action_exception", e);
					intentBroad.putExtra("action_info", "mediaPlayer stop error");
					sendBroadcast(intentBroad);
				}
				String action = intent.getStringExtra("action");
				if (action == null) {
					action = "";
				}
				if (action.equals("stop")) {
					if (mediaPlayer != null) {
						if (mediaPlayer.isPlaying()) {
							mediaPlayer.stop();
							mediaPlayer.release();
							mediaPlayer = null;
						}
					}

				} else if (action.equals("paused")) {
					try {
						mediaPlayer.pause();
					} catch (Exception e) {
						Intent intentBroad = new Intent(com.liangmayong.text2speech.Text2Speech.ACTION_TEXT2SPEECH);
						intentBroad.putExtra("action_type", "error");
						intentBroad.putExtra("action_exception", e);
						intentBroad.putExtra("action_info", "mediaPlayer pause error");
						sendBroadcast(intentBroad);
					}
				} else if (action.equals("replay")) {
					try {
						mediaPlayer.start();
					} catch (Exception e) {
						Intent intentBroad = new Intent(com.liangmayong.text2speech.Text2Speech.ACTION_TEXT2SPEECH);
						intentBroad.putExtra("action_type", "error");
						intentBroad.putExtra("action_exception", e);
						intentBroad.putExtra("action_info", "mediaPlayer start error");
						sendBroadcast(intentBroad);
					}
				} else {
					String text = intent.getStringExtra("text");
					boolean is_return_temp = intent.getBooleanExtra("is_return_temp", true);
					spd = intent.getIntExtra("spd", -1);
					int readtime = intent.getIntExtra("readtime", -1);
					int outtime = intent.getIntExtra("outtime", -1);
					unplay = intent.getIntExtra("unplay", -1);
					int chunkLength = intent.getIntExtra("chunkLength", -1);
					palyenddelete = intent.getBooleanExtra("palyenddelete", false);
					downHandler.setChunkedStreamingMode(chunkLength);
					downHandler.setConnectTimeout(outtime);
					downHandler.setReadTimeout(readtime);
					if (downHandler == null) {
						downHandler = new DownHandler();
					}
					downHandler.setIsReturnTemp(is_return_temp);
					if (spd == -1) {
						spd = 4;
					}
					speech(text);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return START_STICKY;
	};

	private int spd = 4;

	private void speech(String text) {
		String filename = StringBySHA1(text + "/" + spd);
		List<String> speechList = Stringtotrings(text, 500);
		List<URL> urls = new ArrayList<URL>();
		for (int i = 0; i < speechList.size(); i++) {
			List<Map<String, String>> maps = fenchi(speechList.get(i));
			for (int j = 0; j < maps.size(); j++) {
				String type = maps.get(j).get("type");
				String value = maps.get(j).get("value");
				if (type == "num") {
					try {
						urls.add(new URL(getUrl(value, "zh", "utf-8", spd, 0)));
					} catch (MalformedURLException e) {
					} catch (UnsupportedEncodingException e) {
					}
				} else if (type == "zh") {
					try {
						urls.add(new URL(getUrl(value, "zh", "utf-8", spd, 0)));
					} catch (MalformedURLException e) {
					} catch (UnsupportedEncodingException e) {
					}
				} else if (type == "en") {
					try {
						urls.add(new URL(getUrl(value, "en", "utf-8", spd, 0)));
					} catch (MalformedURLException e) {
					} catch (UnsupportedEncodingException e) {
					}
				}
			}
		}
		try {
			File file = new File(saveRoot, filename);
			if (file.exists()) {
				if (unplay != 1) {
					play(file);
				}
			} else {
				downHandler.download(urls, filename, onDownListener);
			}
		} catch (Exception e) {
		}
	}

	private void play(File file) {
		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setOnCompletionListener(onCompletionListener);
			mediaPlayer.setOnPreparedListener(onPreparedListener);
		}
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
		mediaPlayer.reset();
		try {
			pathString = file.getAbsolutePath();
			mediaPlayer.setDataSource(pathString);
			mediaPlayer.prepare();
			mediaPlayer.start();
			Intent intentBroad = new Intent(com.liangmayong.text2speech.Text2Speech.ACTION_TEXT2SPEECH);
			intentBroad.putExtra("action_type", "start");
			sendBroadcast(intentBroad);
			handler.post(runnable);
		} catch (IOException e) {
			file.delete();
			Intent intentBroad = new Intent(com.liangmayong.text2speech.Text2Speech.ACTION_TEXT2SPEECH);
			intentBroad.putExtra("action_type", "error");
			intentBroad.putExtra("action_exception", e);
			intentBroad.putExtra("action_info", "mediaPlayer error");
			sendBroadcast(intentBroad);
		}
	}

	private static boolean isChaia(String string) {
		return string == null ? false : string.matches("[\u4E00-\u9FA5]");
	}

	private static boolean isEnglish(String string) {
		return string == null ? false : string.matches("[a-z]") || string.matches("[A-Z]");
	}

	private static List<String> getStrings(String content) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < content.length(); i++) {
			list.add(content.substring(i, i + 1));
		}
		return list;
	}

	private static List<Map<String, String>> fenchi(String content) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		List<String> strings = getStrings(content);
		String start = "";
		for (int i = 0; i < strings.size(); i++) {
			if (isChaia(start)) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("type", "zh");
				String temp = start;
				while (i < strings.size() && !isEnglish(strings.get(i))) {
					temp += strings.get(i);
					i++;
				}
				map.put("value", temp);
				list.add(map);
				try {
					start = strings.get(i);
				} catch (Exception e) {
				}
			} else if (isChaia(strings.get(i))) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("type", "zh");
				String temp = "";
				if (isChaia(start)) {
					temp += start;
				}
				while (i < strings.size() && !isEnglish(strings.get(i))) {
					temp += strings.get(i);
					i++;
				}
				map.put("value", temp);
				list.add(map);
				try {
					start = strings.get(i);
				} catch (Exception e) {
				}
			} else if (isEnglish(strings.get(i))) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("type", "en");
				String temp = "";
				if (isEnglish(start)) {
					temp += start;
				}
				while (i < strings.size() && !isChaia(strings.get(i))) {
					temp += strings.get(i);
					i++;
				}
				map.put("value", temp);
				list.add(map);
				try {
					start = strings.get(i);
				} catch (Exception e) {
				}
			}
		}
		return list;
	}

	private OnDownListener onDownListener = new OnDownListener() {
		@Override
		public void onStop() {
		}

		@Override
		public void onResult(File file, int index, int count) {
			if (index == count) {
				File savefile = new File(saveRoot, file.getName());
				try {
					file.renameTo(savefile);
				} catch (Exception e) {
				}
				if (unplay != 1) {
					play(savefile);
				}
			}
		}

		@Override
		public void onProgress(int progressLenght, int lenght) {
			Intent intentBroad = new Intent(com.liangmayong.text2speech.Text2Speech.ACTION_TEXT2SPEECH);
			intentBroad.putExtra("action_type", "loadprogress");
			intentBroad.putExtra("action_progresslenght", progressLenght);
			intentBroad.putExtra("action_lenght", lenght);
			sendBroadcast(intentBroad);
		}

		@Override
		public void onLoading() {
		}

		@Override
		public void onError(Exception e, String info) {
			Intent intentBroad = new Intent(com.liangmayong.text2speech.Text2Speech.ACTION_TEXT2SPEECH);
			intentBroad.putExtra("action_type", "error");
			intentBroad.putExtra("action_exception", e);
			intentBroad.putExtra("action_info", info);
			sendBroadcast(intentBroad);
		}
	};

	private String getUrl(String msg, String lan, String ie, int spd, int per) throws UnsupportedEncodingException {
		String urlString = decryption(apipath);
		urlString += "lan";
		urlString += "=";
		urlString += lan;
		urlString += "&";
		urlString += "ie";
		urlString += "=";
		urlString += ie;
		urlString += "&";
		urlString += "per";
		urlString += "=";
		urlString += per;
		urlString += "&";
		urlString += "spd";
		urlString += "=";
		urlString += spd;
		urlString += "&";
		urlString += "text";
		urlString += "=";
		urlString += URLEncoder.encode(msg, "utf-8");
		return urlString;
	}

	private String decryption(String string) {
		String head = "";
		String reStringL = "";
		String reStringR = "";
		String[] str = string.split("_");
		String decon = "";
		if (str.length == 2) {
			head = str[0] + "_";
			decon = str[1];
		} else {
			decon = string;
		}
		String uString = decon;
		try {
			int count = 0;
			for (int i = 0; i < uString.length(); i++) {
				count++;
				uString = uString.substring(1);
			}
			uString = decon;
			for (int i = 0; i < count; i++) {
				if ((i % 2) == 0) {
					reStringR = uString.substring(0, 1) + reStringR;
					uString = uString.substring(2);
				} else {
					reStringL = reStringL + uString.substring(0, 1);
					uString = uString.substring(2);
				}
			}
		} catch (Exception e) {
		}
		return head + reStringL + reStringR;
	}

	private interface OnDownListener {
		public void onLoading();

		public void onResult(File file, int index, int count);

		public void onProgress(int progressLenght, int lenght);

		public void onError(Exception e, String info);

		public void onStop();
	}

	private enum State {
		init, stop, downloading, complete, error
	}

	
	private class DownHandler {

		private int outtime = 60000;
		private int readtime = -1;
		private int chunkLength = -1;
		@SuppressWarnings("unused")
		private final String TAG = "DownHandler";
		private OnDownListener downListener;
		private boolean is_return_temp = true;
		private State state = State.init;
		private boolean isstop = false;

		public void stop() {
			isstop = true;
		}

		private DownHandler() {
		}

		@SuppressWarnings("unused")
		public State getState() {
			return state;
		}

		public void setIsReturnTemp(boolean isReturnTemp) {
			this.is_return_temp = isReturnTemp;
		}

		public void setConnectTimeout(int timeoutMillis) {
			this.outtime = timeoutMillis;
		}

		public void setReadTimeout(int timeoutMillis) {
			this.readtime = timeoutMillis;
		}

		public void setChunkedStreamingMode(int chunkLength) {
			this.chunkLength = chunkLength;
		}

		private File getFile(String filename) {
			return new File(tempRoot, filename);
		}

		private Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					state = State.init;
					if (downListener != null) {
						downListener.onLoading();
					}
					break;
				case 1:
					try {
						DException exception = (DException) msg.obj;
						if (downListener != null) {
							downListener.onError(exception.e, exception.info);
						}
					} catch (Exception e2) {
					}
					break;
				case 2:
					state = State.downloading;
					try {
						Temp temp = (Temp) msg.obj;
						if (temp.index == temp.count) {
							state = State.complete;
						}
						if (downListener != null) {
							downListener.onResult(temp.file, temp.index, temp.count);
						}
					} catch (Exception e) {
					}
					break;
				case 4:
					state = State.error;
					try {
						Progress progress = (Progress) msg.obj;
						if (downListener != null) {
							downListener.onProgress(progress.progressLenght, progress.lenght);
						}
					} catch (Exception e) {
					}
					break;
				case 5:
					state = State.stop;
					try {
						if (downListener != null) {
							downListener.onStop();
						}
					} catch (Exception e) {
					}
					break;
				}
			}
		};

		private class Temp {
			public File file;
			public int count, index;
		}

		private class DException {
			public Exception e;
			public String info;
		}

		private class Progress {
			public int lenght;
			public int progressLenght;
		}

		public void download(final List<URL> url, final String filename, final OnDownListener listener) {
			isstop = false;
			this.downListener = listener;
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						doDownload(url, filename, listener);
					} catch (SocketTimeoutException e) {
						DException exception = new DException();
						exception.e = e;
						exception.info = "OUT_TIME";
						Message msg = new Message();
						msg.obj = exception;
						msg.what = 1;
						handler.sendMessage(msg);
					} catch (NetworkErrorException e) {
						DException exception = new DException();
						exception.e = e;
						exception.info = "NETWORK_ERROR";
						Message msg = new Message();
						msg.obj = exception;
						msg.what = 1;
						handler.sendMessage(msg);
					} catch (UnsupportedEncodingException e) {
						DException exception = new DException();
						exception.e = e;
						exception.info = "UN_ENCODE";
						Message msg = new Message();
						msg.obj = exception;
						msg.what = 1;
						handler.sendMessage(msg);
					} catch (IOException e) {
						DException exception = new DException();
						exception.e = e;
						exception.info = "IO_ERROR";
						Message msg = new Message();
						msg.obj = exception;
						msg.what = 1;
						handler.sendMessage(msg);
					} catch (Exception e) {
						DException exception = new DException();
						exception.e = e;
						exception.info = "UNKOWN_ERROR";
						Message msg = new Message();
						msg.obj = exception;
						msg.what = 1;
						handler.sendMessage(msg);
					}
				}
			});
			thread.start();
		}

		private int getLenghts(List<URL> urls) throws IOException {
			int len = 0;
			for (int i = 0; i < urls.size(); i++) {
				len += getLenght(urls.get(i));
			}
			return len;
		}

		private int getLenght(URL url) throws IOException {
			int lenght = 0;
			HttpURLConnection localHttpURLConnection = null;
			localHttpURLConnection = (HttpURLConnection) url.openConnection();
			if (chunkLength > 0) {
				localHttpURLConnection.setChunkedStreamingMode(chunkLength);
			}
			if (outtime > 0) {
				localHttpURLConnection.setConnectTimeout(outtime);
			}
			if (readtime > 0) {
				localHttpURLConnection.setReadTimeout(readtime);
			}
			localHttpURLConnection.setRequestMethod("GET");
			localHttpURLConnection.setDoOutput(false);
			localHttpURLConnection.setDoInput(true);
			localHttpURLConnection.connect();
			lenght = localHttpURLConnection.getContentLength();
			localHttpURLConnection.disconnect();
			return lenght;
		}

		public int getInputStream(int lenght, int readlenght, URL url, BufferedOutputStream bis)
				throws SocketTimeoutException, NetworkErrorException, UnsupportedEncodingException, IOException,
				Exception {
			HttpURLConnection localHttpURLConnection = null;
			localHttpURLConnection = (HttpURLConnection) url.openConnection();
			if (chunkLength > 0) {
				localHttpURLConnection.setChunkedStreamingMode(chunkLength);
			}
			if (outtime > 0) {
				localHttpURLConnection.setConnectTimeout(outtime);
			}
			if (readtime > 0) {
				localHttpURLConnection.setReadTimeout(readtime);
			}
			localHttpURLConnection.setRequestMethod("GET");
			localHttpURLConnection.setDoOutput(false);
			localHttpURLConnection.setDoInput(true);
			localHttpURLConnection.connect();
			int code = localHttpURLConnection.getResponseCode();
			if (code != 200) {
				throw new IOException();
			} else {
				int len = localHttpURLConnection.getContentLength();
				InputStream inputStream = localHttpURLConnection.getInputStream();
				int re = 0;
				while ((re = inputStream.read(data)) != -1) {
					readlenght += re;
					bis.write(data, 0, re);
					Message msg = new Message();
					Progress progress = new Progress();
					progress.lenght = lenght;
					progress.progressLenght = readlenght;
					msg.obj = progress;
					msg.what = 4;
					handler.sendMessage(msg);
					if (isstop) {
						break;
					}
				}
				return len;
			}
		}

		byte[] data = new byte[2048];

		private void doDownload(List<URL> urls, String fileName, OnDownListener listener) throws SocketTimeoutException,
				NetworkErrorException, UnsupportedEncodingException, IOException, Exception {
			handler.sendEmptyMessage(0);
			Exception exception = null;
			int lenght = 0;
			try {
				lenght = getLenghts(urls);
			} catch (Exception e) {
				exception = e;
			}
			if (is_return_temp) {
				File file = getFile(fileName);
				if (file.exists()) {
					if (file.length() == lenght || (lenght == 0 && file.length() != 0)) {
						Temp temp = new Temp();
						temp.count = 1;
						temp.file = file;
						temp.index = 1;
						Message msg2 = new Message();
						msg2.obj = temp;
						msg2.what = 2;
						handler.sendMessage(msg2);// onResult
						return;
					}
				}
			}else {
				File file = getFile(fileName);
				if (file.exists()) {
					deleteFile(file);
				}
			}
			if (exception != null) {
				throw exception;
			}
			File filetemp = getFile(fileName);
			if (!filetemp.exists()) {
				filetemp.getParentFile().mkdirs();
			}
			FileOutputStream out = new FileOutputStream(filetemp);
			BufferedOutputStream bis = new BufferedOutputStream(out);
			int readlenght = 0;
			for (int i = 0; i < urls.size(); i++) {
				readlenght += getInputStream(lenght, readlenght, urls.get(i), bis);
			}
			bis.flush();
			bis.close();
			Message msg = new Message();
			Temp temp = new Temp();
			temp.count = 1;
			temp.file = filetemp;
			temp.index = 1;
			msg.obj = temp;
			msg.what = 2;
			handler.sendMessage(msg);
		}

		private void deleteFile(File file) {
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
}
