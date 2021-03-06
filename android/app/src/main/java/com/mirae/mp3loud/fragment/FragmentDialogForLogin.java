package com.mirae.mp3loud.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.mirae.mp3loud.R;
import com.mirae.mp3loud.activity.ActivityMain;
import com.mirae.mp3loud.object.ObjectVolley;

public class FragmentDialogForLogin extends DialogFragment {
    private String no;

    public FragmentDialogForLogin(String no) {
        this.no = no;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_for_progress, null);

        TextView textViewProgress = view.findViewById(R.id.textView);
        textViewProgress.setText("서버 접속 중");
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFFFFFFFF, android.graphics.PorterDuff.Mode.MULTIPLY);

        /**
         * ActivityLogin으로부터 회원번호를 받아 웹서버에 전달, 회원 정보를 가져온다.
         * 이 과정이 성공하면 ActivityQA로 넘어가나
         * 실패하면 서버 접속 실패 메시지를 보여주고 현재 화면에 남는다.
         *
         * @author 송훈일(freean2468@gmail.com)
         */
        ObjectVolley.getInstance(getContext()).requestKakaoLogin(
                no,
                new ObjectVolley.RequestLoginListener() {
                    @Override
                    public void jobToDo() {
                        progressBar.setVisibility(View.GONE);
                        textViewProgress.setText(getString(R.string.login_ok));
                        textViewProgress.setVisibility(View.VISIBLE);
                        Intent intent = new Intent(getContext(), ActivityMain.class);
                        Log.i("debug", "in kakaoLogin no : " + no);
                        intent.putExtra("no", no);
                        startActivity(intent);
                    }
                },
                new ObjectVolley.StandardErrorListener() {
                    @Override
                    public void jobToDo() {
                        Log.d(getString(R.string.tag_server), "RequestLogin error");
                        progressBar.setVisibility(View.GONE);
                        textViewProgress.setText(getString(R.string.login_failure));
                        textViewProgress.setVisibility(View.VISIBLE);
                    }
                }
        );

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return view;
    }
}