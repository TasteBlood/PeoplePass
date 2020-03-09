package com.cloudcreativity.peoplepass.loginAndRegister;

import android.databinding.ObservableField;
import android.os.CountDownTimer;
import android.text.TextUtils;

import com.cloudcreativity.peoplepass.base.BaseDialogImpl;
import com.cloudcreativity.peoplepass.databinding.ActivityForgetPwdBinding;
import com.cloudcreativity.peoplepass.utils.DefaultObserver;
import com.cloudcreativity.peoplepass.utils.HttpUtils;
import com.cloudcreativity.peoplepass.utils.StrUtils;
import com.cloudcreativity.peoplepass.utils.ToastUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ForgetPwdModel {
    public ObservableField<String> phone = new ObservableField<>();
    public ObservableField<String> pwd1 = new ObservableField<>();
    public ObservableField<String> pwd2 = new ObservableField<>();
    public ObservableField<String> verifyCode = new ObservableField<>();
    public ObservableField<String> btn_verify_text = new ObservableField<>();
    public ObservableField<Boolean> is_enable = new ObservableField<>();

    private CountDownTimer timer;
    private BaseDialogImpl baseDialog;
    private ForgetPwdActivity context;
    private ActivityForgetPwdBinding binding;

    ForgetPwdModel(BaseDialogImpl baseDialog, ForgetPwdActivity context, ActivityForgetPwdBinding binding) {
        this.baseDialog = baseDialog;
        this.context = context;
        this.binding = binding;
        btn_verify_text.set("发送验证码");
        is_enable.set(true);
    }

    /**
     * 发送验证码
     */
    public void onSendVerifyClick(){
        if(TextUtils.isEmpty(phone.get())||!StrUtils.isPhone(phone.get())){
            binding.tilPhone.setError("手机号格式不正确");
            return;
        }
        binding.tilPhone.setError(null);

        if(!is_enable.get())
            return;
        sendSms(phone.get());

    }

    /**
     *
     * @param s 手机号
     */
    private void sendSms(String s) {
        HttpUtils.getInstance().sendSmsByForget(s)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<String>(baseDialog,true) {
                    @Override
                    public void onSuccess(String t) {
                        ToastUtils.showShortToast(context,"验证码发送成功");
                        startTimer();
                    }

                    @Override
                    public void onFail(ExceptionReason msg) {

                    }
                });
    }

    /**
     * 注册点击
     */
    public void onSubmitClick(){
        if(TextUtils.isEmpty(phone.get())||!StrUtils.isPhone(phone.get())){
            binding.tilPhone.setError("手机号格式不正确");
            return;
        }
        binding.tilPhone.setError(null);
        if(TextUtils.isEmpty(pwd1.get())){
            binding.tilPwd1.setError("密码不能为空");
            return;
        }
        binding.tilPwd1.setError(null);
        if(TextUtils.isEmpty(pwd2.get())||!pwd1.get().equals(pwd2.get())){
            binding.tilPwd2.setError("密码不一致");
            return;
        }
        binding.tilPwd2.setError(null);
        if(TextUtils.isEmpty(verifyCode.get())){
            binding.tilVerifyCode.setError("验证码不能为空");
            return;
        }
        binding.tilVerifyCode.setError(null);

        HttpUtils.getInstance().editPwd(phone.get(),pwd2.get(),verifyCode.get())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<String>(baseDialog,true) {
                    @Override
                    public void onSuccess(String t) {
                        ToastUtils.showShortToast(context,"密码修改成功");
                        context.finish();
                    }

                    @Override
                    public void onFail(ExceptionReason msg) {

                    }
                });
    }

    /**
     * 开始计时
     */
    private void startTimer(){
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
        is_enable.set(false);
        timer = new CountDownTimer(60*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                btn_verify_text.set((millisUntilFinished/1000)+"S后");
            }

            @Override
            public void onFinish() {
                stopTimer();
            }
        };

        timer.start();

    }

    /**
     * 停止计时
     */
    void stopTimer(){
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
        is_enable.set(true);
        btn_verify_text.set("发送验证码");
    }
}
