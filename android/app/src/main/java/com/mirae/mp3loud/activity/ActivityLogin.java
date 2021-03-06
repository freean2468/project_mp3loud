package com.mirae.mp3loud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.mirae.mp3loud.R;
import com.mirae.mp3loud.fragment.FragmentDialogForLogin;
import com.mirae.mp3loud.object.ObjectVolley;

import static com.kakao.util.helper.Utility.getKeyHash;

/**
 * 앱의 첫 화면이자, 로그인 화면. 카카오 SDK를 이용해 카카오 회원번호를 웹서버에 전달, 자체 회원 정보를 가져온다.
 *
 * @author 송훈일(freean2468@gmail.com)
 */
public class ActivityLogin extends AppCompatActivity {
    /**
     * 로그인 액티비티에서만 카카오 세션을 유지한다.
     */
    private ISessionCallback sessionCallback = new ISessionCallback() {

        /** 로그인에 성공한 상태 */
        @Override
        public void onSessionOpened() {
            requestMe();
        }

        /** 로그인에 실패한 상태 */
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());
        }

        /** 사용자 정보 요청 */
        public void requestMe() {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Log.e("KAKAO_API", "세션이 닫혀 있음: " + errorResult);
                }

                @Override
                public void onFailure(ErrorResult errorResult) {
                    Log.e("KAKAO_API", "사용자 정보 요청 실패: " + errorResult);
                }

                @Override
                public void onSuccess(MeV2Response result) {
                    Log.i("debug", "사용자 아이디: " + result.getId());

                    /**
                     * 성공했으니 회원번호를 가지고 다음 Activity인 ActivityQA로 이동
                     *
                     * @author 송훈일(freean2468@gmail.com)
                     */
                    FragmentDialogForLogin fragmentDialogForLogin = new FragmentDialogForLogin(String.valueOf(result.getId()));
                    fragmentDialogForLogin.show(getSupportFragmentManager(), "login");
                }
            });
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        deleteSharedPreferences(getString(R.string.shared_preferences_file_key));

        com.kakao.usermgmt.LoginButton buttonLogin = findViewById(R.id.buttonLogin);
        TextView textViewHostName = findViewById(R.id.textViewHostName);
        ObjectVolley objectVolley = ObjectVolley.getInstance(this);
        textViewHostName.setText(objectVolley.getHostName());

        Switch switchToggleServer = findViewById(R.id.switchToggleServer);
        switchToggleServer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            objectVolley.toggleUseCase();
            textViewHostName.setText(objectVolley.getHostName());
        });

        Session session = Session.getCurrentSession();
        session.addCallback(sessionCallback);

        buttonLogin.setOnClickListener(v -> {
            session.open(AuthType.KAKAO_LOGIN_ALL, ActivityLogin.this);
        });

        /**
         * 카카오 인증에 필요한 앱 해시값 가져오기
         */
        Log.d("MyHashKey", getKeyHash(getApplicationContext()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /**
         * 세션 콜백 삭제
         */
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        /**
         * 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
         */
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
