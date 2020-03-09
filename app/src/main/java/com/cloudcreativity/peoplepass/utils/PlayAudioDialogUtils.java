package com.cloudcreativity.peoplepass.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;

import com.cloudcreativity.peoplepass.R;
import com.cloudcreativity.peoplepass.databinding.DialogLayoutPlayAudioBinding;
import java.io.IOException;

/**
 * 播放音乐对话框
 */
public class PlayAudioDialogUtils implements MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener {
    public ObservableField<String> title = new ObservableField<>();
    public ObservableField<Integer> progress = new ObservableField<>();
    public ObservableField<Boolean> isPlay = new ObservableField<>();

    private Dialog dialog;
    private MediaPlayer player;
    private long total = 0L;
    private CountDownTimer timer;

    public void show(Context context,String audioPath) throws IOException {

        isPlay.set(false);
        title.set(audioPath);
        dialog = new Dialog(context, R.style.myProgressDialogStyle);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        DialogLayoutPlayAudioBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.dialog_layout_play_audio,null,false);
        binding.setAudioUtils(this);
        dialog.setContentView(binding.getRoot());
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                stop();
            }
        });
        Window window = dialog.getWindow();
        assert window != null;
        window.setGravity(Gravity.CENTER);
        int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        window.getAttributes().width = widthPixels*9/10;
        dialog.show();


        player = new MediaPlayer();
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setDataSource(audioPath);
        player.prepareAsync();
    }

    public void onPlayClick(){
        if(isPlay.get()){
            pause();
        }else{
            play();
        }
    }

    public void onCloseClick(){
        stop();
        dialog.dismiss();
    }

    private void play(){
        isPlay.set(true);
        if(player!=null&&!player.isPlaying()){
            player.start();
            startTimer();
        }
    }

    private void pause(){
        isPlay.set(false);
        stopTimer();
        if(player!=null){
            player.pause();
        }
    }

    private void stop(){
        isPlay.set(false);
        stopTimer();
        if(player!=null){
            player.pause();
            player.stop();
            player = null;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        progress.set(100);
        pause();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        total = mp.getDuration();
        play();
    }

    private void startTimer(){
        timer = new CountDownTimer(total,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(isPlay.get()){
                    //LogUtils.e("xuxiwu","duration is "+player.getCurrentPosition());
                    progress.set((int)Math.floor((player.getCurrentPosition()*100/total)));
                }
            }

            @Override
            public void onFinish() {
                stopTimer();
            }
        };

        timer.start();
    }

    private void stopTimer(){
        if(timer!=null){
            timer.cancel();
        }
        timer = null;
    }
}
