package com.mirae.mp3loud.helper;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.inputmethod.InputMethodManager;

import com.mirae.mp3loud.R;
import com.mirae.mp3loud.caseclass.Mp3Info;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Util {
    public static int getDayOfYear() { return Calendar.getInstance().get(Calendar.DAY_OF_YEAR); }
    public static String convertByteArrayToBase64String(byte[] ba) { return Base64.encodeToString(ba, Base64.DEFAULT); }
    public static byte[] convertBase64StringToByteArray(String str) { return Base64.decode(str, Base64.DEFAULT); }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static Bitmap convertByteArrayToBitmap(byte[] byteArr) {
        return BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static String secondsTommssFormat(int seconds) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
        return timeFormat.format(seconds);
    }

    public static void editSharedPreferences(Context context, Mp3Info mp3Info, int position) {
        SharedPreferences sharedPref =
                context.getSharedPreferences(context.getString(R.string.shared_preferences_file_key), context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.putString(context.getString(R.string.shared_preferences_title_key), mp3Info.getTitle());
        editor.putString(context.getString(R.string.shared_preferences_artist_key), mp3Info.getArtist());
        editor.putString(context.getString(R.string.shared_preferences_genre_key), mp3Info.getGenre());
        editor.putInt(context.getString(R.string.shared_preferences_played_times_key), mp3Info.getPlayedTimes());
        editor.putInt(context.getString(R.string.shared_preferences_position_key), position);
        editor.putString(context.getString(R.string.shared_preferences_image_key), mp3Info.getImage());
        editor.putBoolean(context.getString(R.string.shared_preferences_like_key), mp3Info.isLike());

        /**
         * synchronous
         */
        editor.commit();
    }

    public static Activity getActivity(Context context)
    {
        if (context == null)
        {
            return null;
        }
        else if (context instanceof ContextWrapper)
        {
            if (context instanceof Activity)
            {
                return (Activity) context;
            }
            else
            {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }

        return null;
    }
}
