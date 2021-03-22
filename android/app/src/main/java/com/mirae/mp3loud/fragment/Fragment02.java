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
    private TextView textViewCurrentPosition;
    private TextView textViewRemainedPosition;

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
        textViewCurrentPosition = view.findViewById(R.id.textViewCurrentPosition);
        textViewRemainedPosition = view.findViewById(R.id.textViewRemainedPosition);

        imageButtonLike.setOnClickListener(v -> {
            ObjectVolley objectVolley = ObjectVolley.getInstance(getContext());

            objectVolley.requestToggleLike(((ActivityMain) getActivity()).getNo(), textViewTitle.getText().toString(), textViewArtist.getText().toString(),
                    new ObjectVolley.RequestToggleLikeListener() {
                        @Override
                        public void jobToDo() {
                            boolean result;
                            if (this.getResult() == INSERTED) {
                                imageButtonLike.setImageResource(R.drawable.like_enabled);
                                result = true;
                            } else {
                                imageButtonLike.setImageResource(R.drawable.like_disabled);
                                result = false;
                            }
                            ArrayList<Mp3Info> playList = AdapterPlayList.getInstance().getPlayList();
                            syncUi(playList, result);
                        }

                        private void syncUi(ArrayList<Mp3Info> playList, boolean result) {
                            for (int i = 0; i < playList.size(); i++) {
                                Mp3Info mp3 = playList.get(i);
                                if (mp3.getTitle().equals(this.getTitle()) && mp3.getArtist().equals(this.getArtist())) {
                                    mp3.setLike(result);
                                    AdapterPlayList.getInstance().notifyItemChanged(i);
                                    break;
                                }
                            }
                        }
                    },
                    new ObjectVolley.StandardErrorListener() {
                        @Override
                        public void jobToDo() {

                        }
                    });
        });

        ObjectMp3Player objectMp3Player = ObjectMp3Player.getInstance(getActivity());

        if (objectMp3Player.isInitialized() == false) {
            Util.editSharedPreferences(getContext(), AdapterPlayList.getInstance().getPlayList().get(0), 0);
            objectMp3Player.setInitialized(true);
        }

        SharedPreferences sharedPref = getContext().getSharedPreferences(
                getContext().getString(R.string.shared_preferences_file_key), getContext().MODE_PRIVATE);

        int playedTimes = sharedPref.getInt(getString(R.string.shared_preferences_played_times_key), 0);
        int position = sharedPref.getInt(getString(R.string.shared_preferences_position_key), 0);
        String title = sharedPref.getString(getString(R.string.shared_preferences_title_key), "no title").trim();
        String artist = sharedPref.getString(getString(R.string.shared_preferences_artist_key), "no artist").trim();
        String genre = sharedPref.getString(getString(R.string.shared_preferences_genre_key), "no genre").trim();
        String image = sharedPref.getString(getString(R.string.shared_preferences_image_key), "no image").trim();

        setPlayer(new Mp3Info(Mp3Info.NOT_TAKEN_YET, genre, title, artist, image, false, playedTimes), position);

        objectMp3Player.setUI(seekBarPlay, textViewCurrentPosition, textViewRemainedPosition, imageButtonTogglePlay);
        if (objectMp3Player.isClicked() == true) {
            objectMp3Player.play();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.shared_preferences_file_key), Context.MODE_PRIVATE);

        ObjectVolley objectVolley = ObjectVolley.getInstance(getContext());
        ObjectMp3Player objectMp3Player = ObjectMp3Player.getInstance(getActivity());
        String no = ((ActivityMain) getActivity()).getNo();

        if (objectMp3Player.isClicked() == true) {
            int playedTimes = sharedPref.getInt(getString(R.string.shared_preferences_played_times_key), 0);
            int position = sharedPref.getInt(getString(R.string.shared_preferences_position_key), 0);
            String title = sharedPref.getString(getString(R.string.shared_preferences_title_key), "no title").trim();
            String artist = sharedPref.getString(getString(R.string.shared_preferences_artist_key), "no artist").trim();
            String genre = sharedPref.getString(getString(R.string.shared_preferences_genre_key), "no genre").trim();
            String image = sharedPref.getString(getString(R.string.shared_preferences_image_key), "no image").trim();

            if (textViewArtist.getText().toString().equals(artist) && textViewTitle.getText().toString().equals(title)) {
                Log.d("debug", "artist : '" + artist + "', title : '" + title+"'");
            } else {
                objectMp3Player.pause();

                setPlayer(new Mp3Info(Mp3Info.NOT_TAKEN_YET, genre, title, artist, image, false, playedTimes), position);
                objectVolley.requestMp3(title, artist, new ObjectVolley.RequestMp3Listener() {
                    @Override
                    public void jobToDo() {
                        objectMp3Player.init(getContext(), Util.convertBase64StringToByteArray(this.getMp3()));
                        objectMp3Player.setUI(seekBarPlay, textViewCurrentPosition, textViewRemainedPosition, imageButtonTogglePlay);
                        objectMp3Player.play();
                    }
                }, new ObjectVolley.StandardErrorListener() {
                    @Override
                    public void jobToDo() {

                    }
                });
            }
        }

        objectVolley.requestLike(no, textViewTitle.getText().toString(), textViewArtist.getText().toString(),
                new ObjectVolley.requestLikeListener() {
                    @Override
                    public void jobToDo() {
                        imageButtonLike.setImageResource(R.drawable.like_enabled);
                    }
                },
                new ObjectVolley.StandardErrorListener() {
                    @Override
                    public void jobToDo() {
                        imageButtonLike.setImageResource(R.drawable.like_disabled);
                    }
                });

        objectMp3Player.setClicked(false);
    }

    private void setPlayer(Mp3Info mp3Info, int position) {
        AdapterPlayList adapterPlayList = AdapterPlayList.getInstance();

//        linearLayoutLeft.setOnTouchListener((v, e) -> {
//            int previousPosition = position - 1;
//            if (previousPosition < 0) {
//                previousPosition = adapterPlayList.getItemCount() - 1;
//            }
//            setPlayer(adapterPlayList.getPlayList().get(previousPosition), previousPosition);
//            return false;
//        });
//
//        linearLayoutRight.setOnTouchListener((v, e) -> {
//            int nextPosition = position - 1;
//            if (nextPosition < 0) {
//                nextPosition = adapterPlayList.getItemCount() - 1;
//            }
//            setPlayer(adapterPlayList.getPlayList().get(nextPosition), nextPosition);
//            return false;
//        });

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

//        imageButtonToStart.setOnClickListener(v -> {
//            ObjectMp3Player objectMp3Player = ObjectMp3Player.getInstance(getActivity());
//            if (objectMp3Player.getMediaPlayer() != null) {
//             objectMp3Player.getMediaPlayer().seekTo(0);
//            }
//        });
//
//        imageButtonRepetition.setOnClickListener(v -> {
//            ObjectMp3Player objectMp3Player = ObjectMp3Player.getInstance(getActivity());
//            if (objectMp3Player.getMediaPlayer() != null) {
//                if (objectMp3Player.getMediaPlayer().isLooping() == true) {
//                    objectMp3Player.getMediaPlayer().setLooping(false);
//                } else {
//                    objectMp3Player.getMediaPlayer().setLooping(true);
//                }
//            }
//        });

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
                try {
                    ObjectMp3Player.getInstance(getActivity()).interruptUiThread();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
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
