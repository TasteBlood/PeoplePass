package com.cloudcreativity.peoplepass.main;

import android.app.Activity;
import android.content.Intent;
import android.databinding.ObservableField;
import android.text.TextUtils;
import android.view.View;

import com.cloudcreativity.peoplepass.base.BaseDialogImpl;
import com.cloudcreativity.peoplepass.databinding.ActivityModifyPwdBinding;
import com.cloudcreativity.peoplepass.receiver.MyBusinessReceiver;
import com.cloudcreativity.peoplepass.utils.DefaultObserver;
import com.cloudcreativity.peoplepass.utils.HttpUtils;
import com.cloudcreativity.peoplepass.utils.SPUtils;
import com.cloudcreativity.peoplepass.utils.ToastUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ModifyPwdModel {
    public ObservableField<String> oldPwd = new ObservableField<>();
    public ObservableField<String> newPwd1 = new ObservableField<>();
    public ObservableField<String> newPwd2 = new ObservableField<>();

    private Activity context;
    private BaseDialogImpl baseDialog;
    private ActivityModifyPwdBinding binding;

    ModifyPwdModel(Activity context, BaseDialogImpl baseDialog, ActivityModifyPwdBinding binding) {
        this.context = context;
        this.baseDialog = baseDialog;
        this.binding = binding;

        this.binding.tlbForget.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifyPwdModel.this.context.finish();
            }
        });
    }

    public void onSubmitClick(){
        if(TextUtils.isEmpty(oldPwd.get())){
            binding.tilPwd1.setError("旧密码不能为空");
            return;
        }

        binding.tilPwd1.setError(null);

        if(TextUtils.isEmpty(newPwd1.get())){
            binding.tilPwd2.setError("新密码不能为空");
            return;
        }

        binding.tilPwd2.setError(null);

        if(TextUtils.isEmpty(oldPwd.get())||!newPwd1.get().equals(newPwd2.get())){
            binding.tilPwd3.setError("密码不一致");
            return;
        }
        binding.tilPwd3.setError(null);

        HttpUtils.getInstance().modifyPwd(SPUtils.get().getUser().getUserMobile(),
                oldPwd.get(),
                newPwd2.get())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<String>(baseDialog,true) {
                    @Override
                    public void onSuccess(String t) {
                        ToastUtils.showShortToast(context,"密码修改成功，需要重新登录");
                        context.sendBroadcast(new Intent(MyBusinessReceiver.ACTION_RE_LOGIN));
                    }

                    @Override
                    public void onFail(ExceptionReason msg) {

                    }
                });
    }


}
