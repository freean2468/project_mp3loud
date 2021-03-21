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
import com.mirae.mp3loud.adapter.AdapterPlayList;
import com.mirae.mp3loud.caseclass.Mp3Info;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.mirae.mp3loud.caseclass.Mp3Info.TAKEN;

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
     * RequstMp3List 요청에 대한 응답 wrapper abstract class
     * jobToDo 내용만 구현하고, 필드가 null인지 아닌지만 확인해서 사용하면 된다.
     */
    abstract public static class RequestMp3ListListener implements Response.Listener<JSONArray> {
        @Override
        public void onResponse(JSONArray response) {
            try {
                for (int i = 0; i < response.length(); ++i) {
                    JSONObject mp3Info = response.getJSONObject(i);

                    if (mp3Info == null) {
                        Log.d("debug", "origin shouldn't be null!");
                        throw new AssertionError("origin shouldn't be null!");
                    }

                    String genre = mp3Info.get("genre").toString();
                    String title = mp3Info.get("title").toString();
                    String artist = mp3Info.get("artist").toString();
                    String image = mp3Info.get("image").toString();
                    int playedTimes = Integer.parseInt(mp3Info.get("playedTimes").toString());

                    Log.d("debug", "playedTimes : " + playedTimes);

                    if (genre == null) {
                        Log.d("debug", "genre shouldn't be null!");
                        throw new AssertionError("genre shouldn't be null!");
                    }

                    if (title == null) {
                        Log.d("debug", "title shouldn't be null!");
                        throw new AssertionError("title shouldn't be null!");
                    }

                    if (artist == null) {
                        Log.d("debug", "artist shouldn't be null!");
                        throw new AssertionError("artist shouldn't be null!");
                    }

                    if (image == null) {
                        Log.d("debug", "image shouldn't be null!");
                        throw new AssertionError("image shouldn't be null!");
                    }

                    AdapterPlayList.getInstance().addMp3(new Mp3Info(TAKEN, genre, title, artist, image, false, playedTimes));
                    Log.d(ctx.getString(R.string.tag_server), "base64 string image length : " + image.length());
                }
            } catch (JSONException je) {
                je.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            jobToDo();
        }

        public abstract void jobToDo();
    }






    public void requestMp3(String title, String artist, RequestMp3Listener listener, StandardErrorListener errorListener) {
        String url = hostName + ctx.getString(R.string.url_mp3) + "title=" + title + "&artist=" + artist;
        Log.d("debug", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);
        addToRequestQueue(request);
    }

    /**
     * RequstMp3 요청에 대한 응답 wrapper abstract class
     * jobToDo 내용만 구현하고, 필드가 null인지 아닌지만 확인해서 사용하면 된다.
     */
    abstract public static class RequestMp3Listener implements Response.Listener<JSONObject> {
        private String mp3;

        @Override
        public void onResponse(JSONObject response) {
            try {
                mp3 = response.get("mp3").toString();

                if (mp3 == null) {
                    Log.d("debug", "mp3 shouldn't be null!");
                    throw new AssertionError("mp3 shouldn't be null!");
                }
            } catch (JSONException je) {
                je.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            jobToDo();
        }

        public abstract void jobToDo();

        public String getMp3() {
            return mp3;
        }
    }





    public void requestLike(String no,  RequestLikeListener listener, StandardErrorListener errorListener) {
        String url = hostName + ctx.getString(R.string.url_like) + "no=" + no;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, listener, errorListener);
        addToRequestQueue(request);
    }

    /**
     * RequstLike 요청에 대한 응답 wrapper abstract class
     * jobToDo 내용만 구현하고, 필드가 null인지 아닌지만 확인해서 사용하면 된다.
     */
    abstract public static class RequestLikeListener implements Response.Listener<JSONArray> {
        @Override
        public void onResponse(JSONArray response) {
            try {
                for (int i = 0; i < response.length(); ++i) {
                    JSONObject like = response.getJSONObject(i);

                    if (like == null) {
                        Log.d("debug", "origin shouldn't be null!");
                        throw new AssertionError("origin shouldn't be null!");
                    }

                    String title = like.get("title").toString();
                    String artist = like.get("artist").toString();

                    if (title == null) {
                        Log.d("debug", "title shouldn't be null!");
                        throw new AssertionError("title shouldn't be null!");
                    }

                    if (artist == null) {
                        Log.d("debug", "artist shouldn't be null!");
                        throw new AssertionError("artist shouldn't be null!");
                    }

                    ArrayList<Mp3Info> playList =  AdapterPlayList.getInstance().getPlayList();
                    for (int j = 0; j < playList.size(); ++j) {
                        Mp3Info mp3Info = playList.get(j);
                        if (mp3Info.getTitle().equals(title) && mp3Info.getArtist().equals(artist)) {
                            mp3Info.setLike(true);
                            AdapterPlayList.getInstance().notifyItemChanged(j);
                            break;
                        }
                    }
                }
            } catch (JSONException je) {
                je.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            jobToDo();
        }

        public abstract void jobToDo();
    }

    public void requestInsertLike(String no, String title, String artist, RequestInsertLikeListener listener, StandardErrorListener errorListener) {
        String url = hostName + ctx.getString(R.string.url_like_insert) + "no=" + no + "&title=" + title + "&artist=" + artist;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null, listener, errorListener);
        addToRequestQueue(request);
    }

    /**
     * RequstLike 요청에 대한 응답 wrapper abstract class
     * jobToDo 내용만 구현하고, 필드가 null인지 아닌지만 확인해서 사용하면 된다.
     */
    abstract public static class RequestInsertLikeListener implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject response) {
            jobToDo();
        }

        public abstract void jobToDo();
    }

    public void requestDeleteLike(String no, String title, String artist, RequestDeleteLikeListener listener, StandardErrorListener errorListener) {
        String url = hostName + ctx.getString(R.string.url_like_delete) + "no=" + no + "&title=" + title + "&artist=" + artist;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null, listener, errorListener);
        addToRequestQueue(request);
    }

    /**
     * RequstLike 요청에 대한 응답 wrapper abstract class
     * jobToDo 내용만 구현하고, 필드가 null인지 아닌지만 확인해서 사용하면 된다.
     */
    abstract public static class RequestDeleteLikeListener implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject response) {
            jobToDo();
        }

        public abstract void jobToDo();
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
