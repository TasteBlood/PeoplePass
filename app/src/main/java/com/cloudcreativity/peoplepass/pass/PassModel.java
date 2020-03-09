package com.cloudcreativity.peoplepass.pass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableField;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.cloudcreativity.peoplepass.R;
import com.cloudcreativity.peoplepass.base.BaseApp;
import com.cloudcreativity.peoplepass.location.LocationActivity;
import com.cloudcreativity.peoplepass.pass.fragment.DraftFragment;
import com.cloudcreativity.peoplepass.pass.fragment.UnTreatedFragment;
import com.cloudcreativity.peoplepass.utils.SPUtils;
import com.cloudcreativity.peoplepass.utils.UploadService;
import com.cloudcreativity.peoplepass.base.BaseDialogImpl;
import com.cloudcreativity.peoplepass.databinding.ActivityPassBinding;
import com.cloudcreativity.peoplepass.utils.DefaultObserver;
import com.cloudcreativity.peoplepass.utils.GlideUtils;
import com.cloudcreativity.peoplepass.utils.HttpUtils;
import com.cloudcreativity.peoplepass.utils.PlayAudioDialogUtils;
import com.cloudcreativity.peoplepass.utils.RecordVoiceDialogUtils;
import com.cloudcreativity.peoplepass.utils.ToastUtils;
import com.donkingliang.imageselector.PreviewActivity;
import com.donkingliang.imageselector.entry.Image;
import com.donkingliang.imageselector.utils.ImageSelector;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 举报ViewModel
 */
public class PassModel {
    public MyPicAdapter adapter;
    private PassActivity context;
    private BaseDialogImpl baseDialog;
    private ActivityPassBinding binding;

    private String localVoicePath = null;
    private long localVoiceTime = 0L;

    private String qiNiuToken = null;
    private String clickTag = null;

    public ObservableField<String> title = new ObservableField<>();
    public ObservableField<String> place = new ObservableField<>();
    public ObservableField<String> content = new ObservableField<>();

    static final int REQUEST_CODE = 0x1101;

    private LocationClient client;

    PassModel(PassActivity context, ActivityPassBinding binding) {
        this.context = context;
        this.baseDialog = context;
        this.binding = binding;
        adapter = new MyPicAdapter(context);
        binding.gvPassImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==adapter.getCount()-1&&position<=8&&!(adapter.getImages().get(position) instanceof String)){
                    //这就说明这是最后一个
                    ArrayList<String> list = new ArrayList<>();
                    for(Object o : adapter.getImages()){
                        if(o instanceof String)
                            list.add(String.valueOf(o));
                    }
                    ImageSelector.builder()
                            .useCamera(true)
                            .setSingle(false)
                            .setMaxSelectCount(9)
                            .setSelected(list)
                            .start(PassModel.this.context,100);
                }else{
                    //这不是最后一个就执行啥
                    ArrayList<Image> images = new ArrayList<>();
                    for(Object path : adapter.getImages()){
                        if(path instanceof String)
                            images.add(new Image(String.valueOf(path),System.currentTimeMillis(),"","image/jpeg"));
                    }
                    PreviewActivity.openActivity(PassModel.this.context, images,
                            images, true, 9, position,false);
                }
            }
        });

        getQiNiuToken();

        startLocation();

    }

    private void getQiNiuToken(){
        HttpUtils.getInstance().getQiNiuToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<String>(baseDialog,true) {
                    @Override
                    public void onSuccess(String t) {
                        qiNiuToken = t;
                    }

                    @Override
                    public void onFail(ExceptionReason msg) {
                        context.finish();
                    }
                });
    }

    public void onRecordClick(){

        if(!TextUtils.isEmpty(localVoicePath)&&localVoiceTime!=0){
            //开始播放录音
            try {
                new PlayAudioDialogUtils().show(context,localVoicePath);
            } catch (IOException e) {
                e.printStackTrace();
                ToastUtils.showShortToast(context,"音频播放失败");
            }
        }else{
            try{
                new RecordVoiceDialogUtils().show(context, new RecordVoiceDialogUtils.OnRecordFinishListener() {
                    @Override
                    public void onFinish(long fileTime, String filePath) {
                        localVoicePath = filePath;
                        localVoiceTime = fileTime;
                        //更新UI
                        binding.btnRecord.setText(filePath.substring(filePath.lastIndexOf("/")+1,filePath.length()));
                    }
                });
            }catch (Exception e){
                ToastUtils.showShortToast(context,"音频录制失败");
            }
        }

    }

    public boolean onRecordLongClick(){
        localVoiceTime = 0L;
        localVoicePath = null;
        binding.btnRecord.setText(context.getResources().getString(R.string.str_click_start_speak));
        return true;
    }

    public void onLocationClick(){
        context.startActivityForResult(new Intent(context,LocationActivity.class),REQUEST_CODE);
    }

    /**
     *
     * @param data 回掉的选择数据
     *        data 中包含以下数据s
     *             province,city,area,street,address,name,latitude,longitude
     */
    void onLocationResult(Map<String,Object> data){
        place.set(String.valueOf(data.get("address")).concat(" ").concat(String.valueOf(data.get("name"))));
    }

    public void onSaveClick(){
        //进行全部资料的提交
        if(TextUtils.isEmpty(title.get())){
            ToastUtils.showShortToast(context,"标题不能为空");
            return;
        }

        if(TextUtils.isEmpty(content.get())){
            ToastUtils.showShortToast(context,"内容不能为空");
            return;
        }

        if(TextUtils.isEmpty(place.get())){
            ToastUtils.showShortToast(context,"发生地点不可为空");
            return;
        }

        clickTag = "0";
        List<String> images = new ArrayList<>();
        for(Object o  : adapter.getImages()){

            if(o instanceof String)
                images.add(String.valueOf(o));
        }
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle("上传中");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        final AsyncTask<Void, Integer, String> execute = new UploadService(qiNiuToken, images, localVoicePath, dialog).execute();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                execute.cancel(true);
            }
        });

    }

    public void onSubmitClick(){

        //进行全部资料的提交
        if(TextUtils.isEmpty(title.get())){
            ToastUtils.showShortToast(context,"标题不能为空");
            return;
        }

        if(TextUtils.isEmpty(content.get())){
            ToastUtils.showShortToast(context,"内容不能为空");
            return;
        }

        clickTag = "1";
        List<String> images = new ArrayList<>();
        for(Object o  : adapter.getImages()){
            if(o instanceof String)
                images.add(String.valueOf(o));
        }
        new UploadService(qiNiuToken,images,localVoicePath,ProgressDialog.show(context,"处理文件中","请稍后",true)).execute();
    }

    //上传成功，进行其他资料的提交
    void uploadSuccess(Map<String,Object> data) {

        String voice = String.valueOf(data.get("voice"));
        StringBuilder images = new StringBuilder();
        List<String> temp = (List<String>) data.get("images");
        for(String s : temp){
            images.append(s).append(";");
        }
        HttpUtils.getInstance().addPass(SPUtils.get().getIMEI(),"0".equals(clickTag)?0:1,
                0,
                title.get(),
                content.get(),
                0,
                0,
                0,
                place.get(),
                voice,
                TextUtils.isEmpty(images)?"":images.substring(0,images.length()-1))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<String>(baseDialog,true) {
                    @Override
                    public void onSuccess(String t) {
                        EventBus.getDefault().post(DraftFragment.MSG_REFRESH);
                        EventBus.getDefault().post(UnTreatedFragment.MSG_REFRESH);
                        ToastUtils.showShortToast(context,"操作成功");
                        context.finish();
                    }

                    @Override
                    public void onFail(ExceptionReason msg) {

                    }
                });


    }

    public class MyPicAdapter extends BaseAdapter {
        private List<Object> images;
        private Activity context;
        private float gridWidth;

        MyPicAdapter(Activity context) {
            this.context = context;
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            //30 是从布局文件中计算的
            gridWidth = (metrics.widthPixels - 35 * metrics.density) / 4;
            this.images = new ArrayList<>();
            this.images.add(R.drawable.ic_add_img);
        }

        List<Object> getImages(){
            return this.images;
        }

        void update(ArrayList<String> images) {
            this.images.clear();
            this.images.addAll(images);
            if(this.images.size()<9){
                this.images.add(R.drawable.ic_add_img);
            }
            this.notifyDataSetChanged();
        }

        private void remove(int position){
            this.images.remove(position);
            List<Object> newImages = new ArrayList<>();
            for(Object o : this.images){
                if(o instanceof String)
                    newImages.add(o);
            }
            this.images.clear();
            this.images.addAll(newImages);
            this.images.add(R.drawable.ic_add_img);

            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return this.images.size();
        }

        @Override
        public Object getItem(int position) {
            return this.images.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_layout_img_extend_delete, null);
            ImageView iv_pic = convertView.findViewById(R.id.iv_pic);
            ImageView iv_delete = convertView.findViewById(R.id.iv_delete);
            if (this.images.get(position) instanceof Integer) {
                iv_pic.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add_img));
            } else {
                GlideUtils.load(context, new File(String.valueOf(this.images.get(position))),iv_pic);
            }
            convertView.setLayoutParams(new AbsListView.LayoutParams((int) gridWidth, (int) gridWidth));
            if(position==images.size()-1&&images.size()<9){
                iv_delete.setVisibility(View.GONE);
            }else{
                //这是预览的接口
                if(images.get(position) instanceof Integer){
                    iv_delete.setVisibility(View.GONE);
                }else{
                    iv_delete.setVisibility(View.VISIBLE);
                }
            }
            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showShortToast(context,"删除了");
                    remove(position);
                }
            });
            return convertView;
        }

    }

    private void startLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedLocationPoiList(true);
        option.setIsNeedAddress(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);
        client = new LocationClient(BaseApp.app,option);
        client.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(final BDLocation bdLocation) {
                if(bdLocation!=null){
                    //设置当前的位置
                    Snackbar snackBar = Snackbar.make(binding.tlbPass, "当前定位地址:\n" + bdLocation.getAddress().address, Snackbar.LENGTH_LONG);
                    snackBar.setAction("确定填入", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Map<String,Object> map = new HashMap<>();
                                    map.put("province",bdLocation.getProvince());
                                    map.put("city",bdLocation.getCity());
                                    map.put("area",bdLocation.getDistrict());
                                    map.put("street",bdLocation.getStreet());
                                    map.put("address",bdLocation.getAddress().address);
                                    map.put("latitude",bdLocation.getLatitude());
                                    map.put("longitude",bdLocation.getLongitude());
                                    map.put("name",bdLocation.getStreet());
                                    onLocationResult(map);
                                }
                            }).show();

                    //将当前的定位相关移除
                    client.unRegisterLocationListener(this);
                    client.stop();
                    client = null;

                }
            }
        });

        client.start();
    }

    void destroy(){
        if(client!=null){
            client.stop();
            client = null;
        }
    }
}
