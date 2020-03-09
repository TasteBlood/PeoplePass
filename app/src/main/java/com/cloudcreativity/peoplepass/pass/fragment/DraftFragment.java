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
import com.cloudcreativity.peoplepass.databinding.FragmentDraftBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 草稿
 */
public class DraftFragment extends LazyFragment {

    private FragmentDraftBinding binding;

    public static final String MSG_REFRESH = "msg_draft_refresh";
    public static final String MSG_DELETE = "msg_draft_delete";
    private DraftModel draftModel;

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
                //draftModel.deleteObj(draftModel.currentPos);
                break;
            case MSG_REFRESH:
                binding.refreshDraft.startRefresh();
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_draft,null,false);
        draftModel = new DraftModel(this, context, this, binding);
        binding.setDraftModel(draftModel);
        return binding.getRoot();
    }

    @Override
    public void initialLoadData() {
        binding.refreshDraft.startRefresh();
    }
}
