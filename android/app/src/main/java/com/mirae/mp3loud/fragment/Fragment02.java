package com.mirae.mp3loud.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mirae.mp3loud.R;
import com.mirae.mp3loud.adapter.AdapterPlayList;
import com.mirae.mp3loud.caseclass.Mp3Info;
import com.mirae.mp3loud.helper.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Fragment02 extends Fragment {
    private MediaPlayer mediaPlayer = new MediaPlayer();

    private static Fragment02 instance = null;
    private LinearLayout linearLayoutLeft;
    private LinearLayout linearLayoutRight;
    private ImageView imageViewAlbumCover;
    private TextView textViewTitle;
    private TextView textViewArtist;
    private TextView textViewGenre;
    private ImageButton imageButtonLike;
    private ImageButton imageButtonToStart;
    private ImageButton imageButtonRepetition;

    private Fragment02() {

    }

    public static Fragment02 getInstance(){
        if (instance == null) {
            instance = new Fragment02();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment02, container, false);

        linearLayoutLeft = view.findViewById(R.id.linearLayoutLeft);
        linearLayoutRight = view.findViewById(R.id.linearLayoutRight);
        imageViewAlbumCover = view.findViewById(R.id.imageViewAlbumCover);
        textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewArtist = view.findViewById(R.id.textViewArtist);
        textViewGenre = view.findViewById(R.id.textViewGenre);
        imageButtonLike = view.findViewById(R.id.imageButtonLike);
        imageButtonToStart = view.findViewById(R.id.imageButtonToStart);
        imageButtonRepetition = view.findViewById(R.id.imageButtonRepetition);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.shared_preferences_file_key), Context.MODE_PRIVATE);
        int playedTimes = sharedPref.getInt(getString(R.string.shared_preferences_played_times_key), 0);
        int position = sharedPref.getInt(getString(R.string.shared_preferences_position_key), 0);
        String title = sharedPref.getString(getString(R.string.shared_preferences_title_key), "no title");
        String artist = sharedPref.getString(getString(R.string.shared_preferences_artist_key), "no artist");
        String genre = sharedPref.getString(getString(R.string.shared_preferences_genre_key), "no genre");
        String image = sharedPref.getString(getString(R.string.shared_preferences_image_key), "no image");

        setPlayer(new Mp3Info(Mp3Info.NOT_TAKEN_YET, genre, title, artist, image, playedTimes), position);
    }

    private void setPlayer(Mp3Info mp3Info, int position) {
        AdapterPlayList adapterPlayList = AdapterPlayList.getInstance();

        linearLayoutLeft.setOnTouchListener((v, e) -> {
            int previousPosition = position - 1;
            if (previousPosition < 0) {
                previousPosition = adapterPlayList.getItemCount() - 1;
            }
            setPlayer(adapterPlayList.getPlayList().get(previousPosition), previousPosition);
            return false;
        });

        linearLayoutRight.setOnTouchListener((v, e) -> {
            int nextPosition = position - 1;
            if (nextPosition < 0) {
                nextPosition = adapterPlayList.getItemCount() - 1;
            }
            setPlayer(adapterPlayList.getPlayList().get(nextPosition), nextPosition);
            return false;
        });

        byte[] imageBytes = Util.convertBase64StringToByteArray(mp3Info.getImage());
        imageViewAlbumCover.setImageBitmap(Util.convertByteArrayToBitmap(imageBytes));

        textViewArtist.setText(mp3Info.getArtist());
        textViewGenre.setText(mp3Info.getGenre());
        textViewTitle.setText(mp3Info.getTitle());

        imageButtonLike.setOnTouchListener((v, e) -> {

            return false;
        });

        imageButtonToStart.setOnTouchListener((v, e) -> {

            return false;
        });

        imageButtonRepetition.setOnTouchListener((v, e) -> {

            return false;
        });
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
