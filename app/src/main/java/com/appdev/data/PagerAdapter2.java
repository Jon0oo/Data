package com.appdev.data;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class PagerAdapter2 extends FragmentStateAdapter {
    private List<Fragment> fragmentList;

    public PagerAdapter2(FragmentActivity fragmentActivity, List<Fragment> fragments) {
        super(fragmentActivity);
        this.fragmentList = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the fragment at the specified position
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        // Return the total number of fragments
        return fragmentList.size();
    }
}