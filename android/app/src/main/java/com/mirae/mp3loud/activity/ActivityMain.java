package com.mirae.mp3loud.activity;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;

import com.mirae.mp3loud.R;
import com.mirae.mp3loud.fragment.Fragment01;
import com.mirae.mp3loud.fragment.Fragment02;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ActivityMain extends FragmentActivity {
    public static ViewPager2 viewPager2;
    private Adapter viewPagerAdapter;

    private DrawerLayout drawerLayout;
    private View drawerView;
    private Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout=findViewById(R.id.drawerLayout);
        drawerView= findViewById(R.id.drawer);
        btnClose= findViewById(R.id.btnClose);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawers();
            }
        });

        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {
                //슬라이드 진행되었을 때 발생된 콜백함수
            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                Toast.makeText(ActivityMain.this, "열렸다 5반",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
                Toast.makeText(ActivityMain.this, "닫혔.5반",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDrawerStateChanged(int i) {
                //슬라이드 상태가 변화되었을 때 발생하는 콜백함수
            }
        });

        drawerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return true;
            }
        });

        viewPager2 = findViewById(R.id.viewPager);
        viewPagerAdapter = new Adapter(this);
        viewPager2.setAdapter(viewPagerAdapter);
    }

    private class Adapter extends FragmentStateAdapter {
        private Adapter(@NonNull FragmentActivity fa) {
            super(fa);
        }

        private final static int FRAGMENT_01 = 0;
        private final static int FRAGMENT_02 = 1;
        private final static int FRAGMENT_COUNT = 2;

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
}