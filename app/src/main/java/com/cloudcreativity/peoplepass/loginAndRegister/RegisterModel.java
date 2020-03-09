package com.cloudcreativity.peoplepass.loginAndRegister;

import android.databinding.ObservableField;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.RadioGroup;

import com.cloudcreativity.peoplepass.R;
import com.cloudcreativity.peoplepass.base.BaseDialogImpl;
import com.cloudcreativity.peoplepass.base.CommonWebActivity;
import com.cloudcreativity.peoplepass.databinding.ActivityRegisterBinding;
import com.cloudcreativity.peoplepass.utils.APIService;
import com.cloudcreativity.peoplepass.utils.DefaultObserver;
import com.cloudcreativity.peoplepass.utils.HttpUtils;
import com.cloudcreativity.peoplepass.utils.StrUtils;
import com.cloudcreativity.peoplepass.utils.ToastUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RegisterModel {

    public ObservableField<String> lastName = new ObservableField<>();
    public ObservableField<String> phone = new ObservableField<>();
    public ObservableField<String> verifyCode = new ObservableField<>();
    public ObservableField<String> password = new ObservableField<>();
    public ObservableField<String> btn_verify_text = new ObservableField<>();
    public ObservableField<Boolean> is_check_protocol = new ObservableField<>();
    public ObservableField<Boolean> is_enable = new ObservableField<>();
    private ObservableField<Boolean> sex = new ObservableField<>();

    private CountDownTimer timer;
    private RegisterActivity context;
    private BaseDialogImpl baseDialog;
    private ActivityRegisterBinding binding;

    RegisterModel(RegisterActivity context, ActivityRegisterBinding binding) {
        this.context = context;
        this.baseDialog = context;
        this.binding = binding;

        sex.set(true);

        this.binding.rgSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.cb_female){
                    sex.set(false);
                }else if(checkedId==R.id.cb_male){
                    sex.set(true);
                }
            }
        });

        btn_verify_text.set("发送验证码");
        is_check_protocol.set(true);
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
        //发送验证码
        HttpUtils.getInstance().sendSmsByRegister(s)
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
     * 查看用户协议
     */
    public void onLookProtocolClick(){
        CommonWebActivity.startActivity(context,"用户协议",APIService.HOST_APP+APIService.URL.URL_PROTOCOL);
    }

    /**
     * 注册点击
     */
    public void onRegisterClick(){
        if(TextUtils.isEmpty(lastName.get())){
            ToastUtils.showShortToast(context,"姓氏不能为空");
            return;
        }
        if(TextUtils.isEmpty(phone.get())||!StrUtils.isPhone(phone.get())){
            binding.tilPhone.setError("手机号格式不正确");
            return;
        }
        binding.tilPhone.setError(null);
        if(TextUtils.isEmpty(password.get())){
            binding.tilPwd.setError("密码不能为空");
            return;
        }
        binding.tilPwd.setError(null);
        if(TextUtils.isEmpty(verifyCode.get())){
            binding.tilVerifyCode.setError("验证码不能为空");
            return;
        }
        binding.tilVerifyCode.setError(null);
        if(!is_check_protocol.get()){
            ToastUtils.showShortToast(context,"请同意用户使用协议");
            return;
        }

        HttpUtils.getInstance().register(phone.get(),password.get(),verifyCode.get(),lastName.get(),sex.get()?1:2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<String>(baseDialog,true) {
                    @Override
                    public void onSuccess(String t) {
                        ToastUtils.showShortToast(context,t);
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
