package com.mirae.mp3loud.object;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.collection.LruCache;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mirae.mp3loud.R;
import com.mirae.mp3loud.helper.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

/**
 * 웹서버와 통신을 담당하는 Volley Manager class
 * Singleton pattern 적용 
 *
 * @author 송훈일(freean2468@gmail.com)
 */
public class ObjectVolley {
    private static ObjectVolley instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static Context ctx;
    private String hostName;
    private final String hostNameForService;
    private final String hostNameForDevelopment;

    private ObjectVolley(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue,
            new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap>
                        cache = new LruCache<String, Bitmap>(20);

                @Override
                public Bitmap getBitmap(String url) {
                    return cache.get(url);
                }

                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                    cache.put(url, bitmap);
                }
            });

        /** 서비스 중인 웹서버 hostname */
        hostNameForService = ctx.getString(R.string.host_name_for_service);
        /** 개발 중인 local 서버 hostname */
        hostNameForDevelopment = ctx.getString(R.string.host_name_for_development);

        hostName = hostNameForDevelopment;
    }

    public static synchronized ObjectVolley getInstance(Context context) {
        if (instance == null) {
            instance = new ObjectVolley(context);
        }
        return instance;
    }

    /**
     * @return 현재 선택된 웹서버 hostname
     */
    public String getHostName() { return hostName; }

    /**
     * 연결할 웹서버 전환
     */
    public void toggleUseCase() {
        if (hostName.equals(hostNameForService)){
            hostName = hostNameForDevelopment;
        } else {
            hostName = hostNameForService;
        }
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    private <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }











    /**
     * 카카오 로그인 후 회원번호로 다시 자체 웹서버에 회원 정보를 요청하는 함수
     * @param no 카카오 회원 번호
     * @param listener 응답 성공 시 RequestLoginLister에서 jobToDo() 함수에서 로직을 구현할 것.
     * @param errorListener 응답 실패 시 Listener
     */
    public void requestKakaoLogin(String no, RequestLoginListener listener, StandardErrorListener errorListener) {
        String url = hostName + ctx.getString(R.string.url_login) + "no=" + no;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);
        addToRequestQueue(request);
    }

    /**
     * RequstLogin 요청에 대한 응답 wrapper abstract class
     * jobToDo 내용만 구현하고, 필드가 null인지 아닌지만 확인해서 사용하면 된다.
     */
    abstract public static class RequestLoginListener implements Response.Listener<JSONObject> {
        protected String no;
        protected int dayOfYear;
        protected String answer;
        protected byte[] photo = new byte[]{};

        @Override
        public void onResponse(JSONObject response) {
            jobToDo();
        }

        public abstract void jobToDo();
    }






    public void requestMp3List(RequestMp3ListListener listener, StandardErrorListener errorListener) {
        String url = hostName + ctx.getString(R.string.url_mp3_list);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, listener, errorListener);
        addToRequestQueue(request);
    }

    /**
     * RequstMp3 요청에 대한 응답 wrapper abstract class
     * jobToDo 내용만 구현하고, 필드가 null인지 아닌지만 확인해서 사용하면 된다.
     */
    abstract public static class RequestMp3ListListener implements Response.Listener<JSONArray> {
        private JSONArray jsonArray;
        private JSONObject mp3;
        private String origin;

        @Override
        public void onResponse(JSONArray response) {
            try {
                for (int i = 0; i < response.length(); ++i) {
                    mp3 = response.getJSONObject(i);
                    origin = mp3.get("converted").toString();
                    Log.d(ctx.getString(R.string.tag_server), "base64 string length : " + origin.length());
                }
                if (origin == null) {
                    Log.d("debug", "origin shouldn't be null!");
                    throw new AssertionError("origin shouldn't be null!");
                }
            } catch (JSONException je) {
                je.printStackTrace();
            }
            jobToDo();
        }

        public abstract void jobToDo();
        public JSONObject getMp3() { return mp3; }
        public String getOrigin() { return origin; }
    }







    /**
     *  에러 시 서버 응답 코드를 자동으로 알려주는 class
     */
    abstract public static class StandardErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i(ctx.getString(R.string.tag_server), error.toString() + ", STATUS_CODE : " + volleyResponseStatusCode(error));
            jobToDo();
        }

        public static int volleyResponseStatusCode(VolleyError error)
        {
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null) {
                return networkResponse.statusCode;
            }
            else{
                return 0;
            }
        }

        public abstract void jobToDo();
    }
}
