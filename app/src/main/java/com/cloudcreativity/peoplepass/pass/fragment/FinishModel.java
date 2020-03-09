package com.cloudcreativity.peoplepass.pass.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableField;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.cloudcreativity.peoplepass.R;
import com.cloudcreativity.peoplepass.base.BaseBindingRecyclerViewAdapter;
import com.cloudcreativity.peoplepass.base.BaseDialogImpl;
import com.cloudcreativity.peoplepass.base.LazyFragment;
import com.cloudcreativity.peoplepass.databinding.FragmentFinishBinding;
import com.cloudcreativity.peoplepass.databinding.ItemLayoutFinishPassBinding;
import com.cloudcreativity.peoplepass.entity.FinishPassEntity;
import com.cloudcreativity.peoplepass.entity.FinishPassEntityWrapper;
import com.cloudcreativity.peoplepass.pass.PassPreviewActivity;
import com.cloudcreativity.peoplepass.utils.DefaultObserver;
import com.cloudcreativity.peoplepass.utils.HttpUtils;
import com.google.gson.Gson;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FinishModel {

    private BaseDialogImpl baseDialog;
    private LazyFragment fragment;
    private FragmentFinishBinding binding;
    private Context context;
    private int pageNum = 1;
    public ObservableField<Boolean> hasData = new ObservableField<>();
    public BaseBindingRecyclerViewAdapter<FinishPassEntity,ItemLayoutFinishPassBinding> adapter;

    FinishModel(BaseDialogImpl baseDialog, Activity context, LazyFragment fragment, FragmentFinishBinding binding) {
        this.baseDialog = baseDialog;
        this.fragment = fragment;
        this.binding = binding;
        this.context = context;
        this.binding.rcvFinish.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(context.getResources().getDrawable(R.drawable.ic_list_divider_5dp_transparant));
        this.binding.rcvFinish.addItemDecoration(itemDecoration);


        hasData.set(true);

        this.binding.refreshFinish.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                pageNum = 1;
                loadData(pageNum);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                loadData(pageNum);
            }
        });

        adapter = new BaseBindingRecyclerViewAdapter<FinishPassEntity, ItemLayoutFinishPassBinding>(context) {
            @Override
            protected int getLayoutResId(int viewType) {
                return R.layout.item_layout_finish_pass;
            }

            @Override
            protected void onBindItem(ItemLayoutFinishPassBinding binding, FinishPassEntity item, int position) {
                binding.setEntity(item);
                initLayout(binding,item,position);
            }
        };

    }

    private void initLayout(ItemLayoutFinishPassBinding binding, final FinishPassEntity item, int position){
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到编辑页面
                Intent intent = new Intent(context,PassPreviewActivity.class);
                intent.putExtra("entity",item);
                context.startActivity(intent);
            }
        });
    }

    private void loadData(final int page){
        HttpUtils.getInstance().getFinishReport(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<String>(baseDialog,false) {
                    @Override
                    public void onSuccess(String t) {
                        List<FinishPassEntity> list = new Gson().fromJson(t, FinishPassEntityWrapper.class).getData();
                        if(list==null||list.isEmpty()){
                            if(page==1){
                                binding.refreshFinish.finishRefreshing();
                                hasData.set(false);
                                adapter.getItems().clear();
                            }else{
                                binding.refreshFinish.finishLoadmore();
                            }
                        }else{
                            hasData.set(true);
                            if(page==1){
                                fragment.initialLoadDataSuccess();
                                adapter.getItems().clear();
                                binding.refreshFinish.finishRefreshing();
                            }else{
                                binding.refreshFinish.finishLoadmore();
                            }
                            adapter.getItems().addAll(list);
                            pageNum++;
                        }
                    }

                    @Override
                    public void onFail(ExceptionReason msg) {
                        if(page==1){
                            binding.refreshFinish.finishRefreshing();
                            adapter.getItems().clear();
                            hasData.set(false);
                        }else{
                            binding.refreshFinish.finishLoadmore();
                        }
                    }
                });
    }
}
