package com.mirae.mp3loud.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.mirae.mp3loud.R;
import com.mirae.mp3loud.fragment.Fragment01;
import com.mirae.mp3loud.fragment.Fragment02;
import com.mirae.mp3loud.fragment.FragmentDialogForLogin;
import com.mirae.mp3loud.fragment.FragmentDialogForMp3;

public class ActivityMain extends FragmentActivity {
    public final static int FRAGMENT_01 = 0;
    public final static int FRAGMENT_02 = 1;
    public final static int FRAGMENT_COUNT = 2;

    public static ViewPager2 viewPager2;
    private Adapter viewPagerAdapter;
    private String no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        no = String.valueOf(intent.getStringExtra("no"));

        Log.i("debug", "no : " + no);

        if (no == null) {
            throw new AssertionError();
        }

        viewPager2 = findViewById(R.id.viewPager);
        viewPagerAdapter = new Adapter(this);
        viewPager2.setAdapter(viewPagerAdapter);
    }

    private class Adapter extends FragmentStateAdapter {
        private Adapter(@NonNull FragmentActivity fa) {
            super(fa);
        }

        @Override
        public int getItemCount() {
            return FRAGMENT_COUNT;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case FRAGMENT_01: return Fragment01.getInstance();
                case FRAGMENT_02: return Fragment02.getInstance();
                default: return null;
            }
        }
    }

    /**
     * 로그인화면이 아니라 Home 화면으로 나가게 해준다.
     *
     * @author 송훈일(freean2468@gmail.com)
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public String getNo() { return no; }
}