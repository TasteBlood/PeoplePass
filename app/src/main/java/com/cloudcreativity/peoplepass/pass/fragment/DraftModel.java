package com.cloudcreativity.peoplepass.pass.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableField;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.cloudcreativity.peoplepass.R;
import com.cloudcreativity.peoplepass.base.BaseBindingRecyclerViewAdapter;
import com.cloudcreativity.peoplepass.base.BaseDialogImpl;
import com.cloudcreativity.peoplepass.base.LazyFragment;
import com.cloudcreativity.peoplepass.databinding.FragmentDraftBinding;
import com.cloudcreativity.peoplepass.databinding.ItemLayoutDraftPassBinding;
import com.cloudcreativity.peoplepass.entity.PassEntity;
import com.cloudcreativity.peoplepass.entity.PassEntityWrapper;
import com.cloudcreativity.peoplepass.pass.EditPassActivity;
import com.cloudcreativity.peoplepass.pass.PassPreviewActivity;
import com.cloudcreativity.peoplepass.utils.DefaultObserver;
import com.cloudcreativity.peoplepass.utils.HttpUtils;
import com.google.gson.Gson;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DraftModel {

    private BaseDialogImpl baseDialog;
    private LazyFragment fragment;
    private FragmentDraftBinding binding;
    private Activity context;
    private int pageNum = 1;
    public ObservableField<Boolean> hasData = new ObservableField<>();
    public BaseBindingRecyclerViewAdapter<PassEntity,ItemLayoutDraftPassBinding> adapter;

    int currentPos = 0;

    DraftModel(BaseDialogImpl baseDialog, Activity context, LazyFragment fragment, FragmentDraftBinding binding) {
        this.baseDialog = baseDialog;
        this.fragment = fragment;
        this.binding = binding;
        this.context = context;
        this.binding.rcvDraft.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(context.getResources().getDrawable(R.drawable.ic_list_divider_5dp_transparant));
        this.binding.rcvDraft.addItemDecoration(itemDecoration);


        hasData.set(true);

        this.binding.refreshDraft.setOnRefreshListener(new RefreshListenerAdapter() {
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

        adapter = new BaseBindingRecyclerViewAdapter<PassEntity, ItemLayoutDraftPassBinding>(context) {
            @Override
            protected int getLayoutResId(int viewType) {
                return R.layout.item_layout_draft_pass;
            }

            @Override
            protected void onBindItem(ItemLayoutDraftPassBinding binding, PassEntity item, int position) {
                binding.setEntity(item);
                initLayout(binding,item,position);
            }
        };


    }

    private void initLayout(ItemLayoutDraftPassBinding binding, final PassEntity item, final int position) {
        binding.getRoot().findViewById(R.id.tv_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPos = position;
                //跳转到编辑页面
                Intent intent = new Intent(context,EditPassActivity.class);
                intent.putExtra("entity",item);
                context.startActivity(intent);
            }
        });

        binding.getRoot().findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("提示")
                        .setMessage("确定删除吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteObj(item);
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
            }
        });

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到预览页面
                Intent intent = new Intent(context,PassPreviewActivity.class);
                intent.putExtra("entity",item);
                context.startActivity(intent);
            }
        });
    }

    private void loadData(final int page){
        HttpUtils.getInstance().getPageReport(0,page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<String>(baseDialog,false) {
                    @Override
                    public void onSuccess(String t) {
                        PassEntityWrapper entityWrapper = new Gson().fromJson(t, PassEntityWrapper.class);
                        if(entityWrapper.getData()==null||entityWrapper.getData().isEmpty()){
                            if(page==1){
                                binding.refreshDraft.finishRefreshing();
                                hasData.set(false);
                                adapter.getItems().clear();
                            }else{
                                binding.refreshDraft.finishLoadmore();
                            }
                        }else{
                            hasData.set(true);
                            if(page==1){
                                fragment.initialLoadDataSuccess();
                                adapter.getItems().clear();
                                binding.refreshDraft.finishRefreshing();
                            }else{
                                binding.refreshDraft.finishLoadmore();
                            }
                            adapter.getItems().addAll(entityWrapper.getData());
                            pageNum++;
                        }
                    }

                    @Override
                    public void onFail(ExceptionReason msg) {
                        if(page==1){
                            binding.refreshDraft.finishRefreshing();
                            adapter.getItems().clear();
                            hasData.set(false);
                        }else{
                            binding.refreshDraft.finishLoadmore();
                        }
                    }
                });
    }

    void deleteObj(final PassEntity entity) {
        HttpUtils.getInstance().deletePassById(entity.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<String>(baseDialog,true) {
                    @Override
                    public void onSuccess(String t) {
                        adapter.getItems().remove(entity);
                    }

                    @Override
                    public void onFail(ExceptionReason msg) {

                    }
                });
    }
}
