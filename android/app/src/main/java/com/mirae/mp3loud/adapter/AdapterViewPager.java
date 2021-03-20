package com.mirae.mp3loud.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.mirae.mp3loud.fragment.Fragment01;

public class AdapterViewPager extends FragmentPagerAdapter {

    public AdapterViewPager(@NonNull FragmentManager fm) {
        super(fm);
    }

    private final static int FRAGMENT_01 = 0;
    private final static int FRAGMENT_COUNT = 1;

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case FRAGMENT_01: return Fragment01.getInstance();
//            case FRAGMENT_03: return Fragment03Ver2.getInstance();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case FRAGMENT_01: return "📷";
//            case FRAGMENT_03: return "🌙";
            default: return null;
        }
    }
}
