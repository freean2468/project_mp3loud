package com.mirae.mp3loud.fragment;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mirae.mp3loud.R;
import com.mirae.mp3loud.activity.ActivityMain;
import com.mirae.mp3loud.adapter.AdapterPlayList;
import com.mirae.mp3loud.object.ObjectVolley;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Fragment01 extends Fragment {
    private RecyclerView recyclerView;

    private static Fragment01 instance = null;

    private Fragment01() {

    }

    public static Fragment01 getInstance(){
        if (instance == null) {
            instance = new Fragment01();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment01, container, false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(AdapterPlayList.getInstance());

        ObjectVolley objectVolley = ObjectVolley.getInstance(view.getContext());
        objectVolley.requestMp3List(
                new ObjectVolley.RequestMp3ListListener() {
                    @Override
                    public void jobToDo() {

                    }
                },
                new ObjectVolley.StandardErrorListener() {
                    @Override
                    public void jobToDo() {

                    }
                }
        );

        return view;
    }
}
