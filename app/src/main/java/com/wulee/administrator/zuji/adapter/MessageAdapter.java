package com.wulee.administrator.zuji.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.entity.MessageInfo;
import com.wulee.recordingibrary.entity.Voice;
import com.wulee.recordingibrary.utils.VoiceManager;

import java.util.ArrayList;


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
    protected void convert(BaseViewHolder baseViewHolder, MessageInfo message) {

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
                final Voice voice = message.voice;
                if(voice != null){
                    baseViewHolder.setText(R.id.tv_length,voice.getStrLength());
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
                                voiceManager.startPlay(voice.getFilePath());
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
}
