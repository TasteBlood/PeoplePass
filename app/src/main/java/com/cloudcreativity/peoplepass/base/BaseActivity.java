package com.cloudcreativity.peoplepass.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;

import com.cloudcreativity.peoplepass.R;
import com.cloudcreativity.peoplepass.databinding.LayoutProgressDialogBinding;
import com.cloudcreativity.peoplepass.utils.AuthDialogUtils;
import com.cloudcreativity.peoplepass.utils.LogUtils;
import com.cloudcreativity.peoplepass.utils.ToastUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 这是Activity的基类，将retrofit的生命周期绑定在活动的生命周期上面
 */
public abstract class BaseActivity extends AppCompatActivity implements BaseDialogImpl{
    private CompositeDisposable disposableDestroy;
    private Dialog progressDialog;

    public static final int TAKE_PHOTO = 1;//启动相机标识
    public static final int SELECT_PHOTO = 2;//启动相册标识
    public static final int CROP_REQUEST =3;//启动裁剪
    private File tempFile;//这是临时文件

    //是否裁剪
    private boolean isCrop = true;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseApp.app.addActivity(this);
        if(disposableDestroy!=null){
            throw new IllegalStateException("onCreate called multiple times");
        }
        disposableDestroy = new CompositeDisposable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消显示的Toast
        ToastUtils.cancelToast();

        //销毁对话框，防止内存泄漏
        if(progressDialog!=null){
            dismissProgress();
        }

        if (disposableDestroy == null) {
            throw new IllegalStateException(
                    "onDestroy called multiple times or onCreate not called");
        }
        disposableDestroy.dispose();
        disposableDestroy = null;
    }

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
        progressDialog = new Dialog(this, R.style.myProgressDialogStyle);
        LayoutProgressDialogBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this),R.layout.layout_progress_dialog,null,false);
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

    /**
     * 显示用户在其他地方登录
     */
    @Override
    public void showUserAuthOutDialog() {
        //在这里显示用户权限出现的问题
        new AuthDialogUtils().show(this);
    }

    /**
     * 打开照片选择对话框
     */
    @Override
    public void openPictureDialog(boolean isCrop) {
        this.isCrop = isCrop;
        //这里不进行权限的检查，直接在闪屏页面进行权限检查就行
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            if(which==0){
                                openCamera();
                            }else if(which==1){
                                openGallery();
                            }
                    }
                }).setCancelable(true).create();
        dialog.show();
    }

    //这是相机操作
    private void openCamera(){
        //用于保存调用相机拍照后所生成的文件
        tempFile = new File(Environment.getExternalStorageDirectory().getPath(), System.currentTimeMillis() + ".jpg");
        //跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {   //如果在Android7.0以上,使用FileProvider获取Uri
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, getApplicationInfo().packageName+".fileprovider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {    //否则使用Uri.fromFile(file)方法获取Uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        startActivityForResult(intent, TAKE_PHOTO);
    }

    //这是相册操作
    private void openGallery(){
        //用于保存
        tempFile = new File(Environment.getExternalStorageDirectory().getPath(), System.currentTimeMillis() + ".jpg");
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_CANCELED)
            return;
        if(requestCode==TAKE_PHOTO){
            //用相机返回的照片去调用剪裁也需要对Uri进行处理
            if(isCrop){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri contentUri = FileProvider.getUriForFile(this, getApplicationInfo().packageName+".fileprovider", tempFile);
                    cropPhoto(contentUri);
                } else {
                    cropPhoto(Uri.fromFile(tempFile));
                }
            }else{
                //进行图片处理直接返回path
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                    bitmap.compress(Bitmap.CompressFormat.JPEG,90,new FileOutputStream(tempFile));
                    bitmap.recycle();
                    //回掉地址
                    onPhotoSuccess(tempFile.getAbsolutePath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }else if(requestCode==SELECT_PHOTO){
            Uri uri = data.getData();
            if(uri==null)
                return;
            if(isCrop){
                cropPhoto(uri);
            }else{
                //好像是android多媒体数据库的封装接口，具体的看Android文档
                if("file".equals(uri.getScheme())&&data.getType().contains("image")){
                    //处理特殊情况
                    onPhotoSuccess(uri.getPath());
                }else{
                    //获取图片的路径：
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor cursor = managedQuery(uri, proj, null, null, null);
                    //按我个人理解 这个是获得用户选择的图片的索引值
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    //将光标移至开头 ，这个很重要，不小心很容易引起越界
                    cursor.moveToFirst();
                    //最后根据索引值获取图片路径
                    String path = cursor.getString(column_index);
                    //进行图片处理直接返回path
                    onPhotoSuccess(path);
                    cursor.close();
                }
            }
        }else if(requestCode==CROP_REQUEST){
            //这是最终结果
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                //在这里获得了剪裁后的Bitmap对象，可以用于上传
                Bitmap image = bundle.getParcelable("data");
                if(image==null)
                    return;
                //设置到ImageView上
                try {
                    FileOutputStream stream = new FileOutputStream(tempFile);
                    image.compress(Bitmap.CompressFormat.JPEG,90,new FileOutputStream(tempFile));
                    stream.close();
                    //回掉地址
                    onPhotoSuccess(tempFile.getAbsolutePath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //图片处理成功的回掉
    protected void onPhotoSuccess(String filePath){LogUtils.e(getClass().getName(),filePath);}
    /**
     * 裁剪图片
     */
    private void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_REQUEST);
    }
    /**
     *
     * @param message 消息
     *                显示错误的网络请求消息
     */
    @Override
    public void showRequestErrorMessage(String message) {
        ToastUtils.showShortToast(this,message);
    }
}
