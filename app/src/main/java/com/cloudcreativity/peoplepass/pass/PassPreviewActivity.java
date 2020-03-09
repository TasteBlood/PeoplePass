package com.cloudcreativity.peoplepass.pass;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.cloudcreativity.peoplepass.R;
import com.cloudcreativity.peoplepass.base.BaseActivity;
import com.cloudcreativity.peoplepass.databinding.ActivityPassPreviewBinding;

/**
 * 举报预览
 */
public class PassPreviewActivity extends BaseActivity {
    private ActivityPassPreviewBinding binding;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pass_preview);
        Parcelable entity = getIntent().getParcelableExtra("entity");
        binding.setPassPreviewModel(new PassPreviewModel(this,binding,entity));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binding.wvContent!= null) {
            //这句话是停止js加载或者其他function timeout操作，所以注释，存在bug
            //webView.pauseTimers();
            binding.wvContent.clearFormData();
            binding.wvContent.clearCache(true);
            binding.wvContent.clearHistory();
            ((ViewGroup) binding.wvContent.getParent()).removeView(binding.wvContent);
            binding.wvContent.destroy();
            //binding.wvContent= null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(binding.wvContent!=null)
            binding.wvContent.pauseTimers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(binding.wvContent!=null)
            binding.wvContent.resumeTimers();
    }
}
