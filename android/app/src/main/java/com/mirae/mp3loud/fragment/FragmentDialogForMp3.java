package com.mirae.mp3loud.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.mirae.mp3loud.R;
import com.mirae.mp3loud.activity.ActivityMain;
import com.mirae.mp3loud.caseclass.Mp3Info;
import com.mirae.mp3loud.helper.Util;
import com.mirae.mp3loud.object.ObjectMp3Player;
import com.mirae.mp3loud.object.ObjectVolley;

import static com.mirae.mp3loud.activity.ActivityMain.FRAGMENT_02;

public class FragmentDialogForMp3 extends DialogFragment {
    private Mp3Info mp3Info;
    int position;

    public FragmentDialogForMp3(Mp3Info mp3Info, int position) {
        this.mp3Info = mp3Info;
        this.position = position;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_for_progress, null);

        TextView textViewProgress = view.findViewById(R.id.textView);
        textViewProgress.setText("mp3 다운로드 중");
        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        ObjectVolley.getInstance(getContext()).requestMp3(mp3Info.getTitle(), mp3Info.getArtist(), new ObjectVolley.RequestMp3Listener() {
            @Override
            public void jobToDo() {
                ObjectMp3Player objectMp3Player = ObjectMp3Player.getInstance(getActivity());
                objectMp3Player.init(getContext(), Util.convertBase64StringToByteArray(this.getMp3()));
                dismiss();
                //ActivityMain.viewPager2.setCurrentItem(FRAGMENT_02);
            }
        }, new ObjectVolley.StandardErrorListener() {
            @Override
            public void jobToDo() {

            }
        });

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        // Create the AlertDialog object and return it
        return builder.create();
    }
}