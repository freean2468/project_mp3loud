package com.mirae.mp3loud.fragment;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mirae.mp3loud.R;
import com.mirae.mp3loud.adapter.AdapterPlayList;
import com.mirae.mp3loud.object.ObjectVolley;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Fragment01 extends Fragment {
    private RecyclerView recyclerView;
    private MediaPlayer mediaPlayer = new MediaPlayer();

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
//                        playMp3(Util.convertBase64StringToByteArray(this.getOrigin()));
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

    private void playMp3(Context context, byte[] mp3SoundByteArray) {
        try {
            // create temp file that will hold byte array
            File tempMp3 = File.createTempFile("kurchina", "mp3", context.getCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(mp3SoundByteArray);
            fos.close();

            // resetting mediaplayer instance to evade problems
            mediaPlayer.reset();

            // In case you run into issues with threading consider new instance like:
            // MediaPlayer mediaPlayer = new MediaPlayer();

            // Tried passing path directly, but kept getting
            // "Prepare failed.: status=0x1"
            // so using file descriptor instead
            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());

            mediaPlayer.prepare();
            mediaPlayer.start();

            Log.d("debug", "mediaPlayer started!");
        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }
}
