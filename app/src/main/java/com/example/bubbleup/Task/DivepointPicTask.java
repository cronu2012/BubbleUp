package com.example.bubbleup.Task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.bubbleup.R;
import com.google.gson.JsonObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;


public class DivepointPicTask extends AsyncTask<Object, Integer, Bitmap> {
    private final static String TAG = "DivepointPicTask";
    private String url, dp_no;
    private int imageSize;
    private int position = -1;


    /* ImageTask的屬性strong reference到BookListAdapter內的imageView不好，
     * 會導致BookListAdapter進入背景時imageView被參考到而無法被釋放，
     * 而且imageView會參考到Context，也會導致Activity無法被回收。
     * 改採weak參照就不會阻止imageView被回收
     */
    private WeakReference<ImageView> imageViewWeakReference;

    public DivepointPicTask(String url, String dp_no, int imageSize, int position) {

        this(url, dp_no, imageSize,position, null);
    }


    public DivepointPicTask(String url, String dp_no, int imageSize, ImageView imageView) {
        this.url = url;
        this.dp_no = dp_no;
        this.imageSize = imageSize;
        this.imageViewWeakReference = new WeakReference<>(imageView);
    }

    public DivepointPicTask(String url, String dp_no, int imageSize, int position, ImageView imageView) {
        this.url = url;
        this.dp_no = dp_no;
        this.imageSize = imageSize;
        this.position = position;
        this.imageViewWeakReference = new WeakReference<>(imageView);
    }


    @Override
    protected Bitmap doInBackground(Object... objects) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getOnePic");
        jsonObject.addProperty("dp_no", dp_no);
        jsonObject.addProperty("imageSize", imageSize);

        return getRemoteImage(url, jsonObject.toString());
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        ImageView imageView = imageViewWeakReference.get();
        if (isCancelled() || imageView == null) {
            return;
        }
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.sample3);
        }
    }

    private Bitmap getRemoteImage(String url, String jsonOut) {
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true); // allow inputs
            connection.setDoOutput(true); // allow outputs
            connection.setUseCaches(false); // do not use a cached copy
            connection.setRequestMethod("POST");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bw.write(jsonOut);
            Log.d(TAG, "output: " + jsonOut);
            bw.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                bitmap = BitmapFactory.decodeStream(new BufferedInputStream(connection.getInputStream()));
            } else {
                Log.d(TAG, "response code: " + responseCode);
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return bitmap;
    }
}
