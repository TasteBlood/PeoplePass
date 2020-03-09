package com.cloudcreativity.peoplepass.pass;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.internal.ParcelableSparseArray;
import android.view.View;

import com.cloudcreativity.peoplepass.R;
import com.cloudcreativity.peoplepass.base.BaseActivity;
import com.cloudcreativity.peoplepass.databinding.ActivityEditPassBinding;
import com.cloudcreativity.peoplepass.entity.PassEntity;
import com.donkingliang.imageselector.utils.ImageSelector;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 编辑民情反映
 */
public class EditPassActivity extends BaseActivity {
    private EditPassModel model;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        ActivityEditPassBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_pass);
        setSupportActionBar(binding.tlbPass);
        binding.tlbPass.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        PassEntity entity = getIntent().getParcelableExtra("entity");

        binding.setEditPassModel(model = new EditPassModel(this,binding,entity));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=RESULT_OK)
            return;
        if(requestCode==100&&data!=null){
            ArrayList<String> listExtra = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
            model.adapter.update(listExtra);

            //地址选择
        }else if(requestCode==PassModel.REQUEST_CODE&&data!=null){
            String address = data.getStringExtra("address");
            String province = data.getStringExtra("province");
            String city = data.getStringExtra("city");
            String area = data.getStringExtra("area");
            String street = data.getStringExtra("street");
            String name = data.getStringExtra("name");
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);

            HashMap<String,Object> result = new HashMap<>();
            result.put("address",address);
            result.put("province",province);
            result.put("city",city);
            result.put("area",area);
            result.put("street",street);
            result.put("latitude",latitude);
            result.put("longitude",longitude);
            result.put("name",name);

            model.onLocationResult(result);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
//        model.destroy();
    }

    @Subscribe
    public void onEvent(Map<String,Object> data){
        if(data!=null){
            model.uploadSuccess(data);
        }
    }
}
