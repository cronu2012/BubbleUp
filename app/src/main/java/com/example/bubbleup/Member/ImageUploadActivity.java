package com.example.bubbleup.Member;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bubbleup.Main.Util;
import com.example.bubbleup.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.google.android.gms.common.internal.service.Common.API;

public class ImageUploadActivity extends AppCompatActivity {
    private static final String TAG = "ImageUploadActivity";
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_LOGIN = 1;

    private String mem_no;
    private Bitmap picture;
    private ProgressDialog progressDialog;
    private AsyncTask dataUploadTask;
    private TextView tvMessage;
    private ImageView ivTakePicture;
    private Button btLogout;
    private File file;


    private class DataUploadTask extends AsyncTask<Object, Integer, MemPic> {

        @Override
        // invoked on the UI thread immediately after the task is executed.
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ImageUploadActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }


        @Override
        // invoked on the background thread immediately after onPreExecute()
        protected MemPic doInBackground(Object... params) {
            String url = params[0].toString();
            byte[] image = (byte[]) params[1];
            String jsonIn;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("imageBase64", Base64.encodeToString(image, Base64.DEFAULT));
            try {
                jsonIn = getRemoteData(url, jsonObject.toString());
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                return null;
            }
            Gson gson = new Gson();
            JsonObject jObject = gson.fromJson(jsonIn, JsonObject.class);
            MemPic member = new MemPic(
                    mem_no,
                    Base64.decode(jObject.get("imageBase64").getAsString(), Base64.DEFAULT)
            );
            return member;
        }
        @Override
        /*
         * invoked on the UI thread after the background computation finishes.
         * The result of the background computation is passed to this step as a
         * parameter.
         */
        protected void onPostExecute(MemPic user) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(user.getImage(), 0,
                    user.getImage().length);
            ImageView ivResultImage = findViewById(R.id.ivResponseImage);
            ivResultImage.setImageBitmap(bitmap);
            progressDialog.cancel();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        tvMessage = findViewById(R.id.tvMessage);
        ivTakePicture = findViewById(R.id.ivTakePicture);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

       Member memInfo = (Member)getIntent().getSerializableExtra("member");
       mem_no = memInfo.getMem_no();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        // 從偏好設定檔中取得登入狀態來決定是否顯示「登出」
//        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
//        boolean login = pref.getBoolean("login", false);
//        if (login) {
//            btLogout.setVisibility(View.VISIBLE);
//        } else {
//            btLogout.setVisibility(View.INVISIBLE);
//
//        }
//    }


//    public void onLogoutClick(View view) {
//        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
//        pref.edit().putBoolean("login", false).apply();
//        view.setVisibility(View.INVISIBLE);
//    }

    // 點擊ImageView會拍照
    public void onTakePictureClick(View view) {
        takePicture();
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定存檔路徑
        file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture.jpg");
//         targeting Android 7.0 (API level 24) and higher,
//         storing images using a FileProvider.
//         passing a file:// URI across a package boundary causes a FileUriExposedException.
        Uri contentUri = FileProvider.getUriForFile(
                this, getPackageName() + ".provider", file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

        if (isIntentAvailable(this, intent)) {      //檢查此裝置有沒有可用的拍照裝置
            startActivityForResult(intent, REQ_TAKE_PICTURE);


        } else {
            showToast(this, R.string.msg_NoCameraApp);
        }
    }

    // 點擊上傳按鈕
    public void onUploadClick(View view) {
        if (picture == null) {
            showToast(this, R.string.msg_NotUploadWithoutPicture);
            return;
        }else {

        }
    }


    //檢查此裝置使否有可拍照的app之方法，回傳布林
    public boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // 手機拍照App拍照完成後可以取得照片圖檔

                //縮圖
                case REQ_TAKE_PICTURE:
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    // inSampleSize值即為縮放的倍數 (數字越大縮越多)
                    opt.inSampleSize = Util.getImageScale(file.getPath(), 640, 1280);
                    picture = BitmapFactory.decodeFile(file.getPath(), opt);
                    ivTakePicture.setImageBitmap(picture);
                    break;

                // 也可取得自行設計登入畫面的帳號密碼
                case REQ_LOGIN:
                    SharedPreferences pref = getSharedPreferences(Util.PREF_FILE,
                            MODE_PRIVATE);
                    byte[] image = Util.bitmapToPNG(picture);
                    if (networkConnected()) {
                        dataUploadTask = new DataUploadTask().execute(Util.URL, image);
                    } else {
                        showToast(this, R.string.msg_NoNetwork);
                    }
                    break;
            }
        }
    }

    // check if the device connect to the network
    private boolean networkConnected() {
        ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private String getRemoteData(String url, String jsonOut) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true); // allow inputs
        connection.setDoOutput(true); // allow outputs
        connection.setUseCaches(false); // do not use a cached copy
        connection.setRequestMethod("POST");
        connection.setRequestProperty("charset", "UTF-8");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        bw.write(jsonOut);
        Log.d(TAG, "jsonOut: " + jsonOut);
        bw.close();

        int responseCode = connection.getResponseCode();
        StringBuilder jsonIn = new StringBuilder();
        if (responseCode == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                jsonIn.append(line);
            }
        } else {
            Log.d(TAG, "response code: " + responseCode);
        }
        connection.disconnect();
        Log.d(TAG, "jsonIn: " + jsonIn);
        return jsonIn.toString();
    }


    @Override
    protected void onPause() {
        if (dataUploadTask != null) {
            dataUploadTask.cancel(true);
        }
        super.onPause();
    }

    private void showToast(Context context, int messageId) {
        Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show();
    }

    private final static int REQ_PERMISSIONS = 0;

    @Override
    protected void onStart() {
        super.onStart();
        askPermissions();
    }

    private void askPermissions() {
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int result = ContextCompat.checkSelfPermission(this, permissions[0]);
        if (result != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    REQ_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQ_PERMISSIONS:
                String text = "";
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        text += permissions[i] + "\n";
                    }
                }
                if (!text.isEmpty()) {
                    text += getString(R.string.text_NotGranted);
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
