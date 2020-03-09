package com.cloudcreativity.peoplepass.pass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.databinding.ObservableField;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cloudcreativity.peoplepass.R;
import com.cloudcreativity.peoplepass.databinding.ActivityPassPreviewBinding;
import com.cloudcreativity.peoplepass.entity.FinishPassEntity;
import com.cloudcreativity.peoplepass.entity.PassEntity;
import com.cloudcreativity.peoplepass.utils.APIService;
import com.cloudcreativity.peoplepass.utils.GlideUtils;
import com.cloudcreativity.peoplepass.utils.PlayAudioDialogUtils;
import com.cloudcreativity.peoplepass.utils.RecordVoiceDialogUtils;
import com.cloudcreativity.peoplepass.utils.ToastUtils;
import com.donkingliang.imageselector.PreviewActivity;
import com.donkingliang.imageselector.entry.Image;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 举报预览
 */
public class PassPreviewModel {

    public PassPreviewModel.MyPicAdapter adapter;
    private PassPreviewActivity context;
    private ActivityPassPreviewBinding binding;

    private String localVoicePath ;
    private long localVoiceTime;

    public ObservableField<String> title = new ObservableField<>();
    public ObservableField<String> place = new ObservableField<>();
    public ObservableField<String> content = new ObservableField<>();
    public ObservableField<Boolean> isReply = new ObservableField<>();

//    private LocationClient client;

    /**
     *
     * @param context
     * @param binding
     * @param entity Parcelable 对象,具体判断
     */
    PassPreviewModel(final PassPreviewActivity context, ActivityPassPreviewBinding binding, Parcelable entity) {
        this.context = context;
        this.binding = binding;
        isReply.set(false);
        adapter = new PassPreviewModel.MyPicAdapter(context);
        binding.tlbPassPreview.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
            }
        });

        binding.gvPassImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //这不是最后一个就执行啥
                    ArrayList<Image> images = new ArrayList<>();
                    for(Object path : adapter.getImages()){
                        if(path instanceof String)
                            images.add(new Image(String.valueOf(path),System.currentTimeMillis(),"","image/jpeg"));
                    }
                    PreviewActivity.openActivity(PassPreviewModel.this.context, images,
                            images, true, 9-(adapter.getImages().size()-1), position,false);
            }
        });

        //getQiNiuToken();
        if(entity instanceof PassEntity){
            display_pass((PassEntity) entity);
        }else if(entity instanceof FinishPassEntity){
            display_finish_pass((FinishPassEntity) entity);
        }
        //startLocation();

    }
    private void display_pass(PassEntity entity){
        title.set(entity.getTitle());
        content.set(entity.getContent());
        place.set(entity.getDetailAddress());

        if(!TextUtils.isEmpty(entity.getReImgs())){
            String[] images = entity.getReImgs().split(";");
            ArrayList<String> list = new ArrayList<>();
            for(String str:images){
                list.add(APIService.QINIU_HOST.concat(str));
            }
            adapter.update(list);
        }

        localVoicePath = "";

        if(!TextUtils.isEmpty(entity.getVoiceFile())){
            //如何展示录音
            localVoicePath = APIService.QINIU_HOST.concat(entity.getVoiceFile());
            localVoiceTime = 10L;
            binding.btnRecord.setText(entity.getVoiceFile().toLowerCase());
        }else{
            //防止点击录音
            binding.btnRecord.setText("暂无录音信息");
            binding.btnRecord.setEnabled(false);
        }
    }

    @SuppressLint({"JavascriptInterface","SetJavaScriptEnabled"})
    private void display_finish_pass(FinishPassEntity entity){

        display_pass(entity.getReportPojo());
        isReply.set(true);

        WebSettings webSettings = binding.wvContent.getSettings();

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);

        //其他细节操作
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webSettings.setDomStorageEnabled(true);

        binding.wvContent.addJavascriptInterface(new JavaInterface(context),"listener");
        binding.wvContent.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //注入js代码
                 String js = "javascript:(function(){" +
                         "var objs = document.querySelectorAll('img');" +
                         "for(var i=0;i<objs.length;i++){" +
                         "objs[i].onclick = function(){" +
                         "window.listener.onClick(this.src)" +
                         "}" +
                         "}" +
                         "}())";

                 binding.wvContent.loadUrl(js);
            }
        });

        binding.wvContent.loadDataWithBaseURL(null,entity.getReplyContent(),"text/html","UTF-8",null);
        binding.tvTime.setText("(".concat(entity.formatTime()).concat(")"));
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
            new RecordVoiceDialogUtils().show(context, new RecordVoiceDialogUtils.OnRecordFinishListener() {
                @Override
                public void onFinish(long fileTime, String filePath) {
                    localVoicePath = filePath;
                    localVoiceTime = fileTime;
                    //更新UI
                    binding.btnRecord.setText(filePath.substring(filePath.lastIndexOf("/")+1,filePath.length()));
                }
            });
        }

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
            //this.images.add(R.drawable.ic_add_img);
        }

        List<Object> getImages(){
            return this.images;
        }

        void update(ArrayList<String> images) {
            //先将之前的http开头的图片过滤出来
            this.images.addAll(0,images);
            if(this.images.size()>9){
                this.images.remove(this.images.size()-1);
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
            iv_delete.setVisibility(View.GONE);
            if (this.images.get(position) instanceof Integer) {
                iv_pic.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add_img));
            } else {
                String path = String.valueOf(this.images.get(position));
                if(null!=path&&path.startsWith("http")){
                    GlideUtils.load(context,path,iv_pic);
                }else if(null!=path){
                    GlideUtils.load(context,new File(path),iv_pic);
                }
            }
            convertView.setLayoutParams(new AbsListView.LayoutParams((int) gridWidth, (int) gridWidth));
//            if(position==images.size()-1&&images.size()<9){
//                iv_delete.setVisibility(View.GONE);
//            }else{
//                //这是预览的接口
//                if(images.get(position) instanceof Integer){
//                }else{
//                    iv_delete.setVisibility(View.VISIBLE);
//                }
//            }
//            iv_delete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ToastUtils.showShortToast(context,"删除了");
//                    remove(position);
//                }
//            });
            return convertView;
        }

    }

    public class JavaInterface{
        private Activity context;

        JavaInterface(Activity context) {
            this.context = context;
        }

        @JavascriptInterface
        public void onClick(String imgs){
            ArrayList<Image> list = new ArrayList<>();
            list.add(new Image(imgs,0,null,null));
            PreviewActivity.openActivity(context,list,list,true,1,0,false);
        }
    }

}
