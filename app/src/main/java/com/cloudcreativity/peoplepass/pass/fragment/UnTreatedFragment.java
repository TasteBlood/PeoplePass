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
import com.cloudcreativity.peoplepass.databinding.FragmentUntreatedBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static com.cloudcreativity.peoplepass.pass.fragment.DraftFragment.MSG_DELETE;
import static com.cloudcreativity.peoplepass.pass.fragment.DraftFragment.MSG_REFRESH;

/**
 * 未处理
 */
public class UnTreatedFragment extends LazyFragment {

    private FragmentUntreatedBinding binding;


    private UnTreatedModel unTreatedModel;

    public static final String MSG_REFRESH = "msg_untreated_refresh";
    public static final String MSG_DELETE = "msg_untreated_delete";

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
                binding.refreshUnTreated.startRefresh();
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_untreated,null,false);
        binding.setUntreatedModel(unTreatedModel = new UnTreatedModel(this,context,this,binding));
        return binding.getRoot();
    }

    @Override
    public void initialLoadData() {
        binding.refreshUnTreated.startRefresh();
    }
}
