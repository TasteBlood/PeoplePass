package com.cloudcreativity.peoplepass.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import com.cloudcreativity.peoplepass.R;
import com.cloudcreativity.peoplepass.databinding.LayoutProgressDialogBinding;
import com.cloudcreativity.peoplepass.utils.AuthDialogUtils;
import com.cloudcreativity.peoplepass.utils.ToastUtils;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 懒加载的Fragment
 */
public abstract class LazyFragment extends Fragment implements BaseDialogImpl{
    private boolean isViewCreated;
    private boolean isViewVisible;
    private boolean isDataLoaded;
    protected Activity context;
    private CompositeDisposable disposableDestroy;//这是网络请求RxJava需要的东西，跟生命周期绑定的

    private Dialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        if(disposableDestroy!=null){
            throw new IllegalStateException("onCreate called multiple times");
        }
        disposableDestroy = new CompositeDisposable();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposableDestroy == null) {
            throw new IllegalStateException(
                    "onDestroy called multiple times or onCreate not called");
        }
        disposableDestroy.dispose();
        disposableDestroy = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.isViewCreated = true;
        if(isViewVisible&&!isDataLoaded){
            initialLoadData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isViewVisible = isVisibleToUser;
        if(isVisibleToUser&&isViewCreated&&!isDataLoaded)
            initialLoadData();
    }

    /**
     * 初始化记载数据
     */
    public abstract void initialLoadData();

    /**
     * 数据记载成功就停止下次的加载
     */
    public void initialLoadDataSuccess(){
        this.isDataLoaded = true;
    }


    //这是BaseDialogImpl的方法
    @Override
    public boolean addRxDestroy(Disposable disposable) {
        if(disposable==null){
            throw new IllegalStateException("AddUtilDestroy should be called between onCreate and onDestroy");
        }
        disposableDestroy.add(disposable);
        return true;
    }

    @Override
    public void remove(Disposable disposable) {
        if(disposableDestroy!=null){
            disposableDestroy.remove(disposable);
        }
    }

    /**
     *
     * @param msg 显示加载框
     */
    @Override
    public void showProgress(String msg) {
        dialogMessage.set(msg);
        if(progressDialog!=null&&!progressDialog.isShowing()){
            progressDialog.show();
            return;
        }
        progressDialog = new Dialog(context, R.style.myProgressDialogStyle);
        LayoutProgressDialogBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.layout_progress_dialog,null,false);
        binding.setDialog(this);
        progressDialog.setContentView(binding.getRoot());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissProgress();
            }
        });
        progressDialog.show();
    }

    /**
     * 关闭加载框
     */
    @Override
    public void dismissProgress() {
        if(progressDialog!=null&&progressDialog.isShowing())
            progressDialog.dismiss();
        progressDialog = null;
    }


    @Override
    public void showUserAuthOutDialog() {
        //在这里显示用户权限出现的问题
        new AuthDialogUtils().show(context);
    }

    /**
     * 显示图片选择
     */
    @Override
    public void openPictureDialog(boolean isCrop) {

    }

    /**
     * 显示网络请求出错的信息
     * @param message 信息
     */
    @Override
    public void showRequestErrorMessage(String message) {
        ToastUtils.showShortToast(context,message);
    }
}
