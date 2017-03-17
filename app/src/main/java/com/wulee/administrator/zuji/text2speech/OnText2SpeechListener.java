package com.wulee.administrator.zuji.text2speech;

/**
 * OnText2SpeechListener
 * 
 * @author LiangMaYong
 * @version 1.0
 */
public interface OnText2SpeechListener {

	/**
	 * play Completion
	 */
	public void onCompletion();

	/**
	 * play Prepared
	 */
	public void onPrepared();

	/**
	 * play Error
	 */
	public void onError(Exception e, String info);

	/**
	 * play Start
	 */
	public void onStart();

	/**
	 * play onLoadProgress
	 */
	public void onLoadProgress(int progressLenght, int lenght);

	/**
	 * play onPlayProgress
	 */
	public void onPlayProgress(int currentPosition, int duration);
}
