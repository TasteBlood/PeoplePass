package com.cloudcreativity.peoplepass.pass;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.cloudcreativity.peoplepass.databinding.ActivityPassRecordBinding;
import com.cloudcreativity.peoplepass.pass.fragment.DraftFragment;
import com.cloudcreativity.peoplepass.pass.fragment.FinishFragment;
import com.cloudcreativity.peoplepass.pass.fragment.UnTreatedFragment;

import java.util.ArrayList;
import java.util.List;

public class PassRecordModel {

    private List<String> title;
    private List<Fragment> fragments;
    private PassRecordActivity context;
    private ActivityPassRecordBinding binding;

    public MyPagerAdapter adapter;

    PassRecordModel(final PassRecordActivity context, ActivityPassRecordBinding binding) {
        this.context = context;
        this.binding = binding;
        title = new ArrayList<>();
        title.add("草稿");
        title.add("处理中");
        title.add("已完成");

        fragments = new ArrayList<>();

        fragments.add(new DraftFragment());
        fragments.add(new UnTreatedFragment());
        fragments.add(new FinishFragment());

        adapter = new MyPagerAdapter(context.getSupportFragmentManager(),title,fragments);
        binding.tlRecord.setupWithViewPager(binding.vpRecord);
        binding.tlbRecord.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
            }
        });

        binding.vpRecord.setOffscreenPageLimit(3);

    }

    public void onAddClick(){
        context.startActivity(new Intent(context,PassIndexActivity.class));
    }

    public class MyPagerAdapter extends FragmentPagerAdapter{

        private List<String> titles;
        private List<Fragment> fragments;

        MyPagerAdapter(FragmentManager supportFragmentManager, List<String> title, List<Fragment> fragments) {
            super(supportFragmentManager);
            this.titles = title;
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}
