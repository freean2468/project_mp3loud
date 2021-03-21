package com.mirae.mp3loud.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mirae.mp3loud.R;
import com.mirae.mp3loud.activity.ActivityMain;
import com.mirae.mp3loud.adapter.AdapterPlayList;
import com.mirae.mp3loud.caseclass.Mp3Info;
import com.mirae.mp3loud.helper.Util;
import com.mirae.mp3loud.object.ObjectMp3Player;
import com.mirae.mp3loud.object.ObjectVolley;

import java.util.ArrayList;

public class Fragment02 extends Fragment {
    private static Fragment02 instance = null;
    private LinearLayout linearLayoutLeft;
    private LinearLayout linearLayoutRight;
    private ImageView imageViewAlbumCover;
    private TextView textViewTitle;
    private TextView textViewArtist;
    private TextView textViewGenre;
    private ImageButton imageButtonTogglePlay;
    private ImageButton imageButtonLike;
    private ImageButton imageButtonToStart;
    private ImageButton imageButtonRepetition;

    private SeekBar seekBarPlay;
    private SeekBar seekBarVolume;
    private TextView textViewCurrentPosition;
    private TextView textViewRemainedPosition;

    private boolean toggleLike;

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
        imageButtonTogglePlay = view.findViewById(R.id.imageButtonTogglePlay);
        seekBarPlay = view.findViewById(R.id.seekBarPlay);
        seekBarVolume = view.findViewById(R.id.seekBarVolume);
        textViewCurrentPosition = view.findViewById(R.id.textViewCurrentPosition);
        textViewRemainedPosition = view.findViewById(R.id.textViewRemainedPosition);

        SharedPreferences sharedPref = getContext().getSharedPreferences(
                getContext().getString(R.string.shared_preferences_file_key), getContext().MODE_PRIVATE);

        if (sharedPref == null) {
            Util.editSharedPreferences(getContext(), AdapterPlayList.getInstance().getPlayList().get(0), 0);
        }

        ObjectMp3Player objectMp3Player = ObjectMp3Player.getInstance(getActivity());

        int playedTimes = sharedPref.getInt(getString(R.string.shared_preferences_played_times_key), 0);
        int position = sharedPref.getInt(getString(R.string.shared_preferences_position_key), 0);
        String title = sharedPref.getString(getString(R.string.shared_preferences_title_key), "no title");
        String artist = sharedPref.getString(getString(R.string.shared_preferences_artist_key), "no artist");
        String genre = sharedPref.getString(getString(R.string.shared_preferences_genre_key), "no genre");
        String image = sharedPref.getString(getString(R.string.shared_preferences_image_key), "no image");
        Boolean like = sharedPref.getBoolean(getString(R.string.shared_preferences_like_key), false);

        setPlayer(new Mp3Info(Mp3Info.NOT_TAKEN_YET, genre, title, artist, image, like, playedTimes), position);

        ObjectVolley objectVolley = ObjectVolley.getInstance(getContext());
        objectVolley.requestMp3(title, artist, new ObjectVolley.RequestMp3Listener() {
            @Override
            public void jobToDo() {
                ObjectMp3Player objectMp3Player = ObjectMp3Player.getInstance(getActivity());
                objectMp3Player.init(getContext(), Util.convertBase64StringToByteArray(this.getMp3()));
                objectMp3Player.setUI(seekBarPlay, textViewCurrentPosition, textViewRemainedPosition);
                if (objectMp3Player.isClicked() == true) {
                    objectMp3Player.play();
                }
            }
        }, new ObjectVolley.StandardErrorListener() {
            @Override
            public void jobToDo() {

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ObjectMp3Player objectMp3Player = ObjectMp3Player.getInstance(getActivity());
        if (objectMp3Player.isClicked() == true) {
            objectMp3Player.pause();

            SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.shared_preferences_file_key), Context.MODE_PRIVATE);
            int playedTimes = sharedPref.getInt(getString(R.string.shared_preferences_played_times_key), 0);
            int position = sharedPref.getInt(getString(R.string.shared_preferences_position_key), 0);
            String title = sharedPref.getString(getString(R.string.shared_preferences_title_key), "no title");
            String artist = sharedPref.getString(getString(R.string.shared_preferences_artist_key), "no artist");
            String genre = sharedPref.getString(getString(R.string.shared_preferences_genre_key), "no genre");
            String image = sharedPref.getString(getString(R.string.shared_preferences_image_key), "no image");
            String no = ((ActivityMain) getActivity()).getNo();
            Boolean like = sharedPref.getBoolean(getString(R.string.shared_preferences_like_key), false);

            setPlayer(new Mp3Info(Mp3Info.NOT_TAKEN_YET, genre, title, artist, image, like, playedTimes), position);

            ObjectVolley objectVolley = ObjectVolley.getInstance(getContext());
            objectVolley.requestMp3(title, artist, new ObjectVolley.RequestMp3Listener() {
                @Override
                public void jobToDo() {
                    ObjectMp3Player objectMp3Player = ObjectMp3Player.getInstance(getActivity());
                    objectMp3Player.init(getContext(), Util.convertBase64StringToByteArray(this.getMp3()));
                    objectMp3Player.setUI(seekBarPlay, textViewCurrentPosition, textViewRemainedPosition);
                    objectMp3Player.play();
                }
            }, new ObjectVolley.StandardErrorListener() {
                @Override
                public void jobToDo() {

                }
            });
        }

        objectMp3Player.setClicked(false);
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

        imageButtonTogglePlay.setOnClickListener(v -> {
            ObjectMp3Player objectMp3Player = ObjectMp3Player.getInstance(getActivity());
            if (objectMp3Player.getMediaPlayer() != null) {
                if (objectMp3Player.getMediaPlayer().isPlaying() == true) {
                    objectMp3Player.pause();
                } else {
                    objectMp3Player.play();
                }
            }
        });

        if (mp3Info.isLike() == true) {
            imageButtonLike.setImageResource(R.drawable.like_enabled);
        } else {
            imageButtonLike.setImageResource(R.drawable.like_disabled);
        }
        imageButtonLike.setOnClickListener(v -> {
            ObjectVolley objectVolley = ObjectVolley.getInstance(getContext());
            if (mp3Info.isLike() == true) {
                imageButtonLike.setImageResource(R.drawable.like_disabled);
                mp3Info.setLike(!mp3Info.isLike());
                objectVolley.requestDeleteLike(((ActivityMain) getActivity()).getNo(), textViewTitle.getText().toString(), textViewArtist.getText().toString(),
                        new ObjectVolley.RequestDeleteLikeListener() {
                            @Override
                            public void jobToDo() {

                            }
                        },
                        new ObjectVolley.StandardErrorListener() {
                            @Override
                            public void jobToDo() {

                            }
                        });
                ArrayList<Mp3Info> playList = AdapterPlayList.getInstance().getPlayList();
                for (int i = 0; i < playList.size(); i++) {
                    Mp3Info mp3 = playList.get(i);
                    if (mp3.getTitle().equals(mp3Info.getTitle()) && mp3.getArtist().equals(mp3Info.getArtist())) {
                        mp3.setLike(mp3Info.isLike());
                        AdapterPlayList.getInstance().notifyItemChanged(i);
                        break;
                    }
                }
            } else {
                imageButtonLike.setImageResource(R.drawable.like_enabled);
                mp3Info.setLike(!mp3Info.isLike());

                objectVolley.requestInsertLike(((ActivityMain) getActivity()).getNo(), textViewTitle.getText().toString(), textViewArtist.getText().toString(),
                        new ObjectVolley.RequestInsertLikeListener() {
                            @Override
                            public void jobToDo() {

                            }
                        },
                        new ObjectVolley.StandardErrorListener() {
                            @Override
                            public void jobToDo() {

                            }
                        });
                ArrayList<Mp3Info> playList = AdapterPlayList.getInstance().getPlayList();
                for (int i = 0; i < playList.size(); i++) {
                    Mp3Info mp3 = playList.get(i);
                    if (mp3.getTitle().equals(mp3Info.getTitle()) && mp3.getArtist().equals(mp3Info.getArtist())) {
                        mp3.setLike(mp3Info.isLike());
                        AdapterPlayList.getInstance().notifyItemChanged(i);
                        break;
                    }
                }
            }
        });

        imageButtonToStart.setOnClickListener(v -> {
            ObjectMp3Player objectMp3Player = ObjectMp3Player.getInstance(getActivity());
            if (objectMp3Player.getMediaPlayer() != null) {
             objectMp3Player.getMediaPlayer().seekTo(0);
            }
        });

        imageButtonRepetition.setOnClickListener(v -> {
            ObjectMp3Player objectMp3Player = ObjectMp3Player.getInstance(getActivity());
            if (objectMp3Player.getMediaPlayer() != null) {
                if (objectMp3Player.getMediaPlayer().isLooping() == true) {
                    objectMp3Player.getMediaPlayer().setLooping(false);
                } else {
                    objectMp3Player.getMediaPlayer().setLooping(true);
                }
            }
        });

        seekBarPlay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ObjectMp3Player objectMp3Player = ObjectMp3Player.getInstance(getActivity());
                MediaPlayer mediaPlayer = objectMp3Player.getMediaPlayer();

                if (seekBarPlay.isPressed() == true && mediaPlayer != null) {
                    textViewCurrentPosition.setText(Util.secondsTommssFormat(progress));
                    textViewRemainedPosition.setText(Util.secondsTommssFormat(mediaPlayer.getDuration() - progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                ObjectMp3Player.getInstance(getActivity()).interruptUiThread();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ObjectMp3Player objectMp3Player = ObjectMp3Player.getInstance(getActivity());
                MediaPlayer mediaPlayer = objectMp3Player.getMediaPlayer();
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                    objectMp3Player.updateUI();
                }
            }
        });
    }
}
