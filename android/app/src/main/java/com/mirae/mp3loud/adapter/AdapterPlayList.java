package com.mirae.mp3loud.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mirae.mp3loud.R;
import com.mirae.mp3loud.caseclass.Mp3Info;
import com.mirae.mp3loud.helper.Util;

import java.util.ArrayList;
import java.util.List;

public class AdapterPlayList extends RecyclerView.Adapter<AdapterPlayList.RecyclableMusicInfoViewHolder> {
    private static final int MAX_LIST_RECORD = 8;
    private static ArrayList<Mp3Info> playList = new ArrayList<>();
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

    public static ArrayList<Mp3Info> getPlayList() { return playList; }

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
        holder.getTextViewArtist().setText(playList.get(position).getArtist());
        holder.getTextViewGenre().setText(playList.get(position).getGenre());
        holder.getTextViewTitle().setText(playList.get(position).getTitle());
        holder.getTextViewPlayedTimes().setText(String.valueOf(playList.get(position).getPlayedTimes()));

        String albumCover = playList.get(position).getImage();

        if (albumCover != null && albumCover.length() > 0){
            byte[] albumCoverBytes = Util.convertBase64StringToByteArray(albumCover);
            Bitmap albumCoverBitmap = Util.convertByteArrayToBitmap(albumCoverBytes);
            holder.getImageViewAlbumCover().setImageBitmap(albumCoverBitmap);
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

        public RecyclableMusicInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewAlbumCover = itemView.findViewById(R.id.imageViewAlbumCover);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewArtist = itemView.findViewById(R.id.textViewArtist);
            textViewGenre = itemView.findViewById(R.id.textViewGenre);
            imageViewLike = itemView.findViewById(R.id.imageViewLike);
            textViewPlayedTimes = itemView.findViewById(R.id.textViewPlayedTimes);

            itemView.setOnClickListener(this);
        }//end of CalendarViewHolder

        @Override
        public void onClick(View view) {
//            Log.d("debug", "clicked : " + getAdapterPosition());
//            view.setBackgroundColor(Color.CYAN);
//            TextView textViewAnswer = view.findViewById(R.id.textViewAnswer);
//            textViewAnswer.setText("clicked!");
//            ImageView imageViewPhoto = view.findViewById(R.id.imageViewPhoto);
//            imageViewPhoto.setImageResource(R.mipmap.ic_launcher_round);
        }

        public ImageView getImageViewAlbumCover() { return imageViewAlbumCover; }
        public ImageView getImageViewLike() { return imageViewLike; }

        public TextView getTextViewTitle() { return textViewTitle; }
        public TextView getTextViewArtist() { return textViewArtist; }
        public TextView getTextViewGenre() { return textViewGenre; }
        public TextView getTextViewPlayedTimes() { return textViewPlayedTimes; }
    }//end of class
}
