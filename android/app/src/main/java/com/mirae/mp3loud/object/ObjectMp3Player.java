package com.mirae.mp3loud.object;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mirae.mp3loud.helper.Util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ObjectMp3Player {
    private static ObjectMp3Player objectMp3Player = new ObjectMp3Player();
    private static Activity activity;

    private MediaPlayer mediaPlayer;
    private boolean clicked;
    private FileDescriptor fileDescriptor;
    private Thread threadUi;

    private SeekBar seekBarPlay;
    private TextView textViewCurrentPosition;
    private TextView textViewRemainedPosition;

    private ObjectMp3Player(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(mp -> {
            initUI();
        });
        clicked = false;
    }

    public static ObjectMp3Player getInstance(Activity a) {
        activity = a;
        return objectMp3Player;
    }

    public void init(Context context, byte[] mp3SoundByteArray) {
        try {
            // create temp file that will hold byte array
            File tempMp3 = File.createTempFile("kurchina", "mp3", context.getCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(mp3SoundByteArray);
            fos.close();

            // resetting mediaplayer instance to evade problems
            mediaPlayer.reset();

            // Tried passing path directly, but kept getting
            // "Prepare failed.: status=0x1"
            // so using file descriptor instead
            FileInputStream fis = new FileInputStream(tempMp3);
            fileDescriptor = fis.getFD();
            mediaPlayer.setDataSource(fileDescriptor);
            mediaPlayer.prepare();
//            Log.d("debug", "mediaPlayer started!");
        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }

    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            updateUI();
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void updateUI() {
        if (seekBarPlay != null) {
            threadUi = new Thread(null, null, "playerUIThread") {

                public void run() {
                    seekBarPlay.setMax(mediaPlayer.getDuration());
                    while (mediaPlayer.isPlaying()) {
                        if (this.isInterrupted() == true) {
                            break;
                        }
                        activity.runOnUiThread(() -> {
                            seekBarPlay.setProgress((mediaPlayer.getCurrentPosition()));
                            textViewCurrentPosition.setText(Util.secondsTommssFormat(mediaPlayer.getCurrentPosition()));
                            textViewRemainedPosition.setText(Util.secondsTommssFormat(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()));
                        });
                        SystemClock.sleep(200);
                    }
                }
            };

            threadUi.start();
        }
    }

    public void initUI() {
        if (seekBarPlay != null) {
            seekBarPlay.setProgress(0);
            textViewCurrentPosition.setText("00:00");
            if (mediaPlayer != null) {
                textViewRemainedPosition.setText("00:00");
            }
            else
                textViewRemainedPosition.setText(Util.secondsTommssFormat(mediaPlayer.getDuration()));
        }
    }

    public void setUI(SeekBar sb, TextView tvCurrentPosition, TextView tvRemainedPosition) {
        this.seekBarPlay = sb;
        this.textViewCurrentPosition = tvCurrentPosition;
        this.textViewRemainedPosition = tvRemainedPosition;
    }

    public void interruptUiThread() {
        threadUi.interrupt();
    }

    public MediaPlayer getMediaPlayer() { return mediaPlayer; }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }
}
