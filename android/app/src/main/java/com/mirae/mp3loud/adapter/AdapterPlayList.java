package com.mirae.mp3loud.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mirae.mp3loud.R;
import com.mirae.mp3loud.activity.ActivityMain;
import com.mirae.mp3loud.caseclass.Mp3Info;
import com.mirae.mp3loud.fragment.Fragment02;
import com.mirae.mp3loud.helper.Util;
import com.mirae.mp3loud.object.ObjectMp3Player;
import com.mirae.mp3loud.object.ObjectVolley;

import java.util.ArrayList;
import java.util.List;

public class AdapterPlayList extends RecyclerView.Adapter<AdapterPlayList.RecyclableMusicInfoViewHolder> {
    private static final int MAX_LIST_RECORD = 8;
    private ArrayList<Mp3Info> playList = new ArrayList<>();
    private static AdapterPlayList instance;

    private AdapterPlayList() {
        for (int i = 0; i < MAX_LIST_RECORD; ++i) {
            playList.add(new Mp3Info());
        }
    }

    public static AdapterPlayList getInstance() {
        if (instance == null) {
            instance = new AdapterPlayList();
        }
        return instance;
    }

    public ArrayList<Mp3Info> getPlayList() { return playList; }

    @NonNull
    @Override
    public RecyclableMusicInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recyclable_mp3_info, parent, false);

        return new RecyclableMusicInfoViewHolder(view);
    }

    /**
     * Update only part of ViewHolder that you are interested in
     * Invoked before onBindViewHolder(ViewHolder holder, int position)
     *
     * onBindViewHolder(ViewHolder holder, int position) 후에 특정한 요소가 갱신될 때마다
     * 데이터를 갱신한 후 필요한 view를 갱신하는 부분
     *
     * @param holder
     * @param position
     * @param payloads NotifyItemChanged 함수를 통해 전달된 인자들
     */
    @Override
    public void onBindViewHolder(RecyclableMusicInfoViewHolder holder, int position, List<Object> payloads) {
//        if(!payloads.isEmpty()) {
//            if (payloads.get(0) instanceof String) {
//                holder.getTextViewAnswer().setText(String.valueOf((String)payloads.get(0)));
//            }
//            if (payloads.get(1) instanceof byte[]) {
//                holder.getImageViewPhoto().setImageBitmap(Util.convertByteArrayToBitmap((byte[])payloads.get(1)));
//            }
//        } else {
            super.onBindViewHolder(holder,position, payloads);
//        }
    }

    /**
     * 기존 view 디자인에 가장 기초적으로 뿌려져야 할 것들은 여기서 갱신
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclableMusicInfoViewHolder holder, int position) {
        Mp3Info mp3Info = playList.get(position);

        if (mp3Info.getState() == Mp3Info.NOT_TAKEN_YET) {
            holder.getLinearLayoutMp3Info().setVisibility(View.GONE);
        } else {
            holder.getLinearLayoutMp3Info().setVisibility(View.VISIBLE);

            holder.getTextViewArtist().setText(mp3Info.getArtist());
            holder.getTextViewGenre().setText(mp3Info.getGenre());
            holder.getTextViewTitle().setText(mp3Info.getTitle());
            holder.getTextViewPlayedTimes().setText(String.valueOf(mp3Info.getPlayedTimes()));

            if (mp3Info.isLike() == true) {
                holder.getImageViewLike().setImageResource(R.drawable.like_enabled);
            } else {
                holder.getImageViewLike().setImageResource(R.drawable.like_disabled);
            }

            String albumCover = mp3Info.getImage();

            if (albumCover != null && albumCover.length() > 0){
                byte[] albumCoverBytes = Util.convertBase64StringToByteArray(albumCover);
                Bitmap albumCoverBitmap = Util.convertByteArrayToBitmap(albumCoverBytes);
                holder.getImageViewAlbumCover().setImageBitmap(albumCoverBitmap);
            }
        }
    }

    public void addMp3(Mp3Info mp3Info) {
        boolean flag = false;

        for (int i = 0; i < playList.size(); ++i) {
            Mp3Info present = playList.get(i);
            if (present.getState() == Mp3Info.NOT_TAKEN_YET) {
                playList.set(i, mp3Info);
                flag = true;
                AdapterPlayList.getInstance().notifyItemChanged(i);
                break;
            }
        }

        if (flag == false) {
            playList.add(mp3Info);
            AdapterPlayList.getInstance().notifyItemInserted(playList.size()-1);
        }
    }

    @Override
    public int getItemCount() {
        return playList.size();
    }

    public class RecyclableMusicInfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageViewAlbumCover;
        private TextView textViewTitle;
        private TextView textViewArtist;
        private TextView textViewGenre;
        private ImageView imageViewLike;
        private TextView textViewPlayedTimes;
        private LinearLayout linearLayoutMp3Info;

        public RecyclableMusicInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewAlbumCover = itemView.findViewById(R.id.imageViewAlbumCover);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewArtist = itemView.findViewById(R.id.textViewArtist);
            textViewGenre = itemView.findViewById(R.id.textViewGenre);
            imageViewLike = itemView.findViewById(R.id.imageViewLike);
            textViewPlayedTimes = itemView.findViewById(R.id.textViewPlayedTimes);
            linearLayoutMp3Info = itemView.findViewById(R.id.linearLayoutMp3Info);

            itemView.setOnClickListener(this);
        }//end of CalendarViewHolder

        @Override
        public void onClick(View view) {
            Mp3Info mp3Info = playList.get(getAdapterPosition());
            Context context = view.getContext();

            Util.editSharedPreferences(context, mp3Info, getAdapterPosition());

            ObjectMp3Player objectMp3Player = ObjectMp3Player.getInstance(Util.getActivity(context));
            objectMp3Player.setClicked(true);
            ObjectVolley.getInstance(context).requestIncrementPlayedTimes(mp3Info.getTitle(), mp3Info.getArtist(),
                    new ObjectVolley.RequestPlayedTimesListener() {
                        @Override
                        public void jobToDo() {

                        }
                    },
                    new ObjectVolley.StandardErrorListener() {
                        @Override
                        public void jobToDo() {

                        }
                    });

            ActivityMain.viewPager2.setCurrentItem(1);
        }

        public ImageView getImageViewAlbumCover() { return imageViewAlbumCover; }
        public ImageView getImageViewLike() { return imageViewLike; }

        public TextView getTextViewTitle() { return textViewTitle; }
        public TextView getTextViewArtist() { return textViewArtist; }
        public TextView getTextViewGenre() { return textViewGenre; }
        public TextView getTextViewPlayedTimes() { return textViewPlayedTimes; }
        public LinearLayout getLinearLayoutMp3Info() { return linearLayoutMp3Info; }
    }//end of class
}
