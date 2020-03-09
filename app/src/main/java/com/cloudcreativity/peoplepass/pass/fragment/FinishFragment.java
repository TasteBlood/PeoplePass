package com.cloudcreativity.peoplepass.pass.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cloudcreativity.peoplepass.R;
import com.cloudcreativity.peoplepass.base.LazyFragment;
import com.cloudcreativity.peoplepass.databinding.FragmentFinishBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 已完成
 */
public class FinishFragment extends LazyFragment {

    private FragmentFinishBinding binding;

    private FinishModel finishModel;

    public static final String MSG_REFRESH = "msg_finish_refresh";
    public static final String MSG_DELETE = "msg_finish_delete";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(String msg){
        switch (msg){
            case MSG_DELETE:
                break;
            case MSG_REFRESH:
                binding.refreshFinish.startRefresh();
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_finish,null,false);
        binding.setFinishModel(finishModel = new FinishModel(this,context,this,binding));
        return binding.getRoot();
    }

    @Override
    public void initialLoadData() {
        binding.refreshFinish.startRefresh();
    }
}
