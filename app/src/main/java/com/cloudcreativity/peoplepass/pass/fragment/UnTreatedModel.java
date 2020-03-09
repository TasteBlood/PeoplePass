package com.cloudcreativity.peoplepass.pass.fragment;

import android.app.Activity;
import android.content.Intent;
import android.databinding.ObservableField;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.cloudcreativity.peoplepass.R;
import com.cloudcreativity.peoplepass.base.BaseBindingRecyclerViewAdapter;
import com.cloudcreativity.peoplepass.base.BaseDialogImpl;
import com.cloudcreativity.peoplepass.base.LazyFragment;
import com.cloudcreativity.peoplepass.databinding.FragmentUntreatedBinding;
import com.cloudcreativity.peoplepass.databinding.ItemLayoutUndreatedPassBinding;
import com.cloudcreativity.peoplepass.entity.PassEntity;
import com.cloudcreativity.peoplepass.entity.PassEntityWrapper;
import com.cloudcreativity.peoplepass.pass.PassPreviewActivity;
import com.cloudcreativity.peoplepass.utils.DefaultObserver;
import com.cloudcreativity.peoplepass.utils.HttpUtils;
import com.google.gson.Gson;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UnTreatedModel {

    private BaseDialogImpl baseDialog;
    private LazyFragment fragment;
    private FragmentUntreatedBinding binding;
    private int pageNum = 1;
    private Activity context;
    public ObservableField<Boolean> hasData = new ObservableField<>();
    public BaseBindingRecyclerViewAdapter<PassEntity,ItemLayoutUndreatedPassBinding> adapter;

    UnTreatedModel(BaseDialogImpl baseDialog, Activity context, LazyFragment fragment, FragmentUntreatedBinding binding) {
        this.baseDialog = baseDialog;
        this.fragment = fragment;
        this.binding = binding;
        this.context = context;
        this.binding.rcvUnTreated.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(context.getResources().getDrawable(R.drawable.ic_list_divider_5dp_transparant));
        this.binding.rcvUnTreated.addItemDecoration(itemDecoration);


        hasData.set(true);

        this.binding.refreshUnTreated.setOnRefreshListener(new RefreshListenerAdapter() {
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

        adapter = new BaseBindingRecyclerViewAdapter<PassEntity,ItemLayoutUndreatedPassBinding>(context) {
            @Override
            protected int getLayoutResId(int viewType) {
                return R.layout.item_layout_undreated_pass;
            }

            @Override
            protected void onBindItem(ItemLayoutUndreatedPassBinding binding, PassEntity item, int position) {
                binding.setEntity(item);
                initLayout(binding,item,position);
            }
        };

    }

    private void initLayout(ItemLayoutUndreatedPassBinding binding, final PassEntity item, int position){
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,PassPreviewActivity.class);
                intent.putExtra("entity",item);
                context.startActivity(intent);
            }
        });
    }

    private void loadData(final int page){
        HttpUtils.getInstance().getPageReport(1,page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<String>(baseDialog,false) {
                    @Override
                    public void onSuccess(String t) {
                        PassEntityWrapper entityWrapper = new Gson().fromJson(t, PassEntityWrapper.class);
                        if(entityWrapper.getData()==null||entityWrapper.getData().isEmpty()){
                            if(page==1){
                                binding.refreshUnTreated.finishRefreshing();
                                hasData.set(false);
                                adapter.getItems().clear();
                            }else{
                                binding.refreshUnTreated.finishLoadmore();
                            }
                        }else{
                            hasData.set(true);
                            if(page==1){
                                fragment.initialLoadDataSuccess();
                                adapter.getItems().clear();
                                binding.refreshUnTreated.finishRefreshing();
                            }else{
                                binding.refreshUnTreated.finishLoadmore();
                            }
                            adapter.getItems().addAll(entityWrapper.getData());
                            pageNum++;
                        }
                    }

                    @Override
                    public void onFail(ExceptionReason msg) {
                        if(page==1){
                            binding.refreshUnTreated.finishRefreshing();
                            adapter.getItems().clear();
                            hasData.set(false);
                        }else{
                            binding.refreshUnTreated.finishLoadmore();
                        }
                    }
                });
    }
}
