package com.cloudcreativity.peoplepass.pass;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.cloudcreativity.peoplepass.R;
import com.cloudcreativity.peoplepass.base.BaseActivity;
import com.cloudcreativity.peoplepass.databinding.ActivityPassRecordBinding;
/**
 * 举报记录
 */
public class PassRecordActivity extends BaseActivity {

    private ActivityPassRecordBinding binding;
    private PassRecordModel recordModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pass_record);
        recordModel = new PassRecordModel(this, binding);
        binding.setRecordModel(recordModel);

        binding.tlbRecord.inflateMenu(R.menu.menu_pass_toolbar);
        binding.tlbRecord.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.menu_pass_add){
                    recordModel.onAddClick();
                    return true;
                }
                return false;
            }
        });
    }


}
