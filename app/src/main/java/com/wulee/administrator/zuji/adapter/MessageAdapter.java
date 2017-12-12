package com.wulee.administrator.zuji.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.stetho.common.LogUtil;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.entity.Constant;
import com.wulee.administrator.zuji.entity.MessageInfo;
import com.wulee.recordingibrary.entity.Voice;
import com.wulee.recordingibrary.utils.VoiceManager;

import java.io.File;
import java.util.ArrayList;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;


public class MessageAdapter extends BaseMultiItemQuickAdapter<MessageInfo,BaseViewHolder> {

    private AnimationDrawable voiceAnimation;
    private VoiceManager voiceManager;
    private int lastPosition = -1;

    public MessageAdapter(ArrayList<MessageInfo> dataList, Context context) {
        super(dataList);
        voiceManager =  VoiceManager.getInstance(context);
        addItemType(MessageInfo.TYPE_TEXT, R.layout.message_board_text_item);
        addItemType(MessageInfo.TYPE_AUDIO, R.layout.message_board_audio_item);
    }
    @Override
    protected void convert(BaseViewHolder baseViewHolder, final MessageInfo message) {

        final int position = baseViewHolder.getAdapterPosition();
        switch (baseViewHolder.getItemViewType()) {
            case MessageInfo.TYPE_TEXT:
                baseViewHolder.setText(R.id.tv_content,message.getContent());
                if(message.piInfo != null){
                    if(!TextUtils.isEmpty(message.piInfo.getName())){
                        baseViewHolder.setText(R.id.tv_name,message.piInfo.getName());
                    }else{
                        baseViewHolder.setText(R.id.tv_name,message.piInfo.getUsername());
                    }
                }
                baseViewHolder.setText(R.id.tv_time , message.getCreatedAt().substring(0,16));
                break;
            case MessageInfo.TYPE_AUDIO:
                final Voice[] voice = {message.voice};

                if(!TextUtils.isEmpty(message.audioUrl)){
                    BmobFile bmobfile = new BmobFile(System.currentTimeMillis() + ".mp3","",message.audioUrl);

                    File saveFile = new File(Constant.SAVE_AUDIO, bmobfile.getFilename());
                    bmobfile.download(saveFile, new DownloadFileListener() {
                        @Override
                        public void onStart() {
                            LogUtil.i("开始下载...");
                        }
                        @Override
                        public void done(String savePath,BmobException e) {
                            if(e==null){
                                LogUtil.i("下载成功,保存路径:"+savePath);

                                voice[0] = new Voice(message.voice.getLength(),message.voice.getStrLength(),savePath);
                            }else{
                                LogUtil.i("下载失败："+e.getErrorCode()+","+e.getMessage());
                            }
                        }
                        @Override
                        public void onProgress(Integer value, long newworkSpeed) {
                            LogUtil.i("bmob","下载进度："+value+","+newworkSpeed);
                        }
                    });
                }

                if(voice[0] != null){
                    baseViewHolder.setText(R.id.tv_length, voice[0].getStrLength());
                    final ImageView ivVoice = baseViewHolder.getView(R.id.iv_voice);
                    RelativeLayout rlAudio = baseViewHolder.getView(R.id.rl_audio);
                    rlAudio.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (voiceAnimation != null) {
                                voiceAnimation.stop();
                                voiceAnimation.selectDrawable(0);
                            }
                            if (voiceManager.isPlaying()&& lastPosition == position) {
                                voiceManager.stopPlay();
                            }else{
                                voiceManager.stopPlay();
                                voiceAnimation = (AnimationDrawable) ivVoice.getBackground();
                                voiceAnimation.start();
                                voiceManager.setVoicePlayListener(new VoiceManager.VoicePlayCallBack() {
                                    @Override
                                    public void voiceTotalLength(long time, String strTime) {
                                    }
                                    @Override
                                    public void playDoing(long time, String strTime) {
                                    }
                                    @Override
                                    public void playPause() {
                                    }
                                    @Override
                                    public void playStart() {
                                    }
                                    @Override
                                    public void playFinish() {
                                        if (voiceAnimation != null) {
                                            voiceAnimation.stop();
                                            voiceAnimation.selectDrawable(0);
                                        }
                                    }
                                });
                                voiceManager.startPlay(voice[0].getFilePath());
                            }
                        }
                    });
                }
                if(message.piInfo != null){
                    if(!TextUtils.isEmpty(message.piInfo.getName())){
                        baseViewHolder.setText(R.id.tv_name,message.piInfo.getName());
                    }else{
                        baseViewHolder.setText(R.id.tv_name,message.piInfo.getUsername());
                    }
                }
                baseViewHolder.setText(R.id.tv_time , message.getCreatedAt().substring(0,16));
                break;
        }
    }


    public void stopPlayAudio(){
        if(voiceManager.isPlaying()){
            voiceManager.stopRecordAndPlay();
        }
    }
}
