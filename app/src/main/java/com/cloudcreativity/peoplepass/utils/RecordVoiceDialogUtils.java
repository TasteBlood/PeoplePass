package com.cloudcreativity.peoplepass.utils;

import android.app.Activity;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;

import com.cloudcreativity.peoplepass.R;
import com.cloudcreativity.peoplepass.databinding.DialogLayoutRecordVoiceBinding;
import com.github.lassana.recorder.AudioRecorder;
import com.github.lassana.recorder.AudioRecorderBuilder;

import java.io.File;

/**
 * 录音使用的对话框
 */
public class RecordVoiceDialogUtils implements AudioRecorder.OnStartListener, AudioRecorder.OnPauseListener {

    public ObservableField<Integer> progress = new ObservableField<>();
    public ObservableField<String> residueTime = new ObservableField<>();
    private long fileTime = 0L;

    private AudioRecorder recorder;
    private Activity context;
    private CountDownTimer timer;
    //获取声音分贝的timer
    private CountDownTimer volumeTimer;
    private Dialog dialog;
    private OnRecordFinishListener onRecordFinishListener;
    /**
     * 最大录音时长
     */
    private static final int MAX_TIME = 10 * 60 * 1000;

    /**
     * 显示对话框，要进行的工作是
     * 1、开始录制
     * 2、开启音量大小检测
     */
    public void show(Activity context, OnRecordFinishListener onRecordFinishListener){
        try{
            this.context = context;
            this.onRecordFinishListener = onRecordFinishListener;
            dialog = new Dialog(context, R.style.myProgressDialogStyle);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            DialogLayoutRecordVoiceBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_layout_record_voice, null, false);
            binding.setVoiceUtils(this);
            dialog.setContentView(binding.getRoot());
            Window window = dialog.getWindow();
            assert window != null;
            window.setGravity(Gravity.CENTER);
            int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
            window.getAttributes().width = widthPixels*6/7;
            dialog.show();
            start();
        }catch (Exception e){
            ToastUtils.showShortToast(context,"录音失败");
        }
    }

    /**
     * 停止录制按钮点击
     */
    public void onStopClick() {
        stop();
    }

    private void start(){
        try{
            String fileName = Environment.getExternalStorageDirectory()+File.separator+"record"
                    + File.separator + (System.currentTimeMillis() + ".mp3");
            this.recorder = AudioRecorderBuilder.with(this.context)
                    .fileName(fileName)
                    .config(AudioRecorder.MediaRecorderConfig.DEFAULT)
                    .loggable()
                    .build();
            this.recorder.start(this);
        }catch (Exception e){
            ToastUtils.showShortToast(context,"录制失败，请确保手机打开录音权限");
            dismiss();
        }
    }

    private void dismiss() {
        if(dialog!=null)
            dialog.dismiss();
    }

    private void stop() {
        stopTimer();
        if (recorder != null)
            this.recorder.pause(this);
    }

    private void startTimer() {
        timer = new CountDownTimer(MAX_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                fileTime = (MAX_TIME - millisUntilFinished)/1000;
                int minute = (int) (millisUntilFinished/(60*1000));
                int second = (int) ((millisUntilFinished)%(60*1000)/1000);
                residueTime.set(String.format(context.getResources().getString(R.string.str_residue_time),minute,second));
            }

            @Override
            public void onFinish() {
                fileTime = 30;
                stop();
            }
        };

        volumeTimer = new CountDownTimer(MAX_TIME,100) {
            @Override
            public void onTick(long millisUntilFinished) {
                //获取声音分贝
                progress.set(getDB(recorder.getMediaRecorder()));
            }

            @Override
            public void onFinish() {

            }
        };

        timer.start();
        volumeTimer.start();
    }

    private void stopTimer() {
        if (timer != null)
            timer.cancel();
        if(volumeTimer!=null)
            volumeTimer.cancel();
    }

    @Override
    public void onPaused(String activeRecordFileName) {
        //将声音文件返回
        if (onRecordFinishListener != null)
            onRecordFinishListener.onFinish(fileTime, activeRecordFileName);
        dismiss();
    }

    @Override
    public void onStarted() {
        //开启声音分贝获取
        startTimer();
    }

    @Override
    public void onException(Exception e) {
        e.printStackTrace();
        LogUtils.e("xuxiwu","录音时出错啦...");
        ToastUtils.showShortToast(context,"录音失败");
        dismiss();
    }

    /**
     * 录音结束后的回掉
     */
    public interface OnRecordFinishListener {
        void onFinish(long fileTime, String filePath);
    }

    /**     *
     * 获取录音的声音分贝值
     * * @return     */
    private int getDB(MediaRecorder mediaRecorder){
        double ratio = (double)mediaRecorder.getMaxAmplitude() /1;
        double db = 0;// 分贝
        if (ratio > 1)
        db = 20 * Math.log10(ratio);
        return (int) db;
    }
}
