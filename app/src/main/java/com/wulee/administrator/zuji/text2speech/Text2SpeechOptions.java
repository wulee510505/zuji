package com.wulee.administrator.zuji.text2speech;

/**
 * Text2SpeechOptions
 * 
 * @author LiangMaYong
 * @version 1.0
 */
public class Text2SpeechOptions {
	public static final String LANGUAGE_ZH = "zh";
	public static final String LANGUAGE_EN = "en";
	private String text;
	private boolean endDelete = false;
	private int spd = -1, readtime = 5000, outtime = 5000, chunkLength = -1;
	private boolean is_return_temp = true;

	public boolean isReturnTemp() {
		return is_return_temp;
	}

	public void setReturnTemp(boolean is_return_temp) {
		this.is_return_temp = is_return_temp;
	}

	public String getText() {
		return text;
	}

	public void setEndDelete(boolean endDelete) {
		this.endDelete = endDelete;
	}

	public boolean isEndDelete() {
		return endDelete;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getSpd() {
		return spd;
	}

	public void setSpd(int spd) {
		this.spd = spd;
	}

	public int getReadtime() {
		return readtime;
	}

	public void setReadtime(int readtime) {
		this.readtime = readtime;
	}

	public int getOuttime() {
		return outtime;
	}

	public void setOuttime(int outtime) {
		this.outtime = outtime;
	}

	public int getChunkLength() {
		return chunkLength;
	}

	public void setChunkLength(int chunkLength) {
		this.chunkLength = chunkLength;
	}
}
