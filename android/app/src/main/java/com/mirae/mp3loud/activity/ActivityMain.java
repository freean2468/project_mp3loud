package com.mirae.mp3loud.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import com.mirae.mp3loud.R;
import com.mirae.mp3loud.helper.Util;
import com.mirae.mp3loud.object.ObjectVolley;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ActivityMain extends AppCompatActivity {
    MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ObjectVolley objectVolley = ObjectVolley.getInstance(this);
        objectVolley.requestMp3List(
                new ObjectVolley.RequestMp3ListListener() {
                    @Override
                    public void jobToDo() {
                        playMp3(Util.convertBase64StringToByteArray(this.getOrigin()));
//                        PlayAudio(this.getOrigin());
                    }
                },
                new ObjectVolley.StandardErrorListener() {
                    @Override
                    public void jobToDo() {

                    }
                }
        );
    }

    private void playMp3(byte[] mp3SoundByteArray) {
        try {
            // create temp file that will hold byte array
            File tempMp3 = File.createTempFile("kurchina", "mp3", getCacheDir());
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