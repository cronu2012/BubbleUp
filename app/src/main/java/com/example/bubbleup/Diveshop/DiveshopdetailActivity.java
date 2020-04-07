package com.example.bubbleup.Diveshop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bubbleup.Divepoint.DivepointdetailActivity;
import com.example.bubbleup.Lesson.LessonActivity;
import com.example.bubbleup.Main.Util;
import com.example.bubbleup.R;
import com.example.bubbleup.Task.ImageTask;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;



import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.example.bubbleup.Main.Util.PREF_FILE;


public class DiveshopdetailActivity extends AppCompatActivity {
    private final static String TAG = "DiveshopdetailActivity";
    private ImageTask dpicImageTask;
    private Button  btnAboutus, btnMap,btnDsLes,btnDsmap;
    private RecyclerView recyclerView;
    private  Diveshop diveshop ;


    private static final int MY_REQUEST_CODE = 0;
    private static final int REQUEST_CHECK_SETTINGS = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;  //行動裝置與Google Location Services 之間必須的物件
    private SettingsClient settingsClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;
    private LocationCallback locationCallback;
    private Location location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diveshopdetail);
        diveshop = (Diveshop) this.getIntent().getSerializableExtra("diveshop");
        if (diveshop == null) {
            Util.showToast(this, R.string.msg_DiveshopNotFound);
        } else {
            showDetail(diveshop);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);




        //取得FusedLocationProviderClient物件
        settingsClient = LocationServices.getSettingsClient(this);
        createLocationCallback(); //
        createLocationRequest();
        buildLocationSettingsRequest();


        btnAboutus = findViewById(R.id.btnAboutus);
        btnMap = findViewById(R.id.btnMap);
        btnDsLes =findViewById(R.id.btnDsLes);
        btnDsmap = findViewById(R.id.btnDsmap);

        btnAboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder  diveshopInfo = new AlertDialog.Builder(DiveshopdetailActivity.this);
                diveshopInfo.setTitle("潛店介紹")
                        .setMessage(diveshop.getDsinfo())
                        .setIcon(R.drawable.ic_done_black_24dp)
                        .setCancelable(true)
                        .show();
            }
        });


        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationName = diveshop.getAddress().trim();
                if (location == null || locationName.isEmpty())
                    return;

                Address address = getAddress(locationName);
                if (address == null) {
                    Toast.makeText(DiveshopdetailActivity.this, getString(R.string.msg_LocationNotAvailable), Toast.LENGTH_SHORT).show();
                    return;
                }
                // 取得自己位置與使用者輸入位置的緯經度
                double fromLat = location.getLatitude();
                double fromLng = location.getLongitude();
                double toLat = address.getLatitude();
                double toLng = address.getLongitude();

                direct(fromLat, fromLng, toLat, toLng);
            }
        });

        btnDsLes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiveshopdetailActivity.this, LessonActivity.class);
                intent.putExtra("diveshop",diveshop);
                startActivity(intent);
            }
        });

        btnDsmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inient = new Intent(DiveshopdetailActivity.this,DiveshopmapActivity.class);
                inient.putExtra("diveshop",diveshop);
                startActivity(inient);
            }
        });
    }



    // 開啟Google地圖應用程式來完成導航要求
    private void direct(double fromLat, double fromLng, double toLat, double toLng) {
        // 設定欲前往的Uri，saddr-出發地緯經度；daddr-目的地緯經度
        String uriStr = String.format(Locale.TAIWAN,
                "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f",
                fromLat, fromLng, toLat, toLng);
        Intent intent = new Intent();
        // 指定交由Google地圖應用程式接手
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        // ACTION_VIEW-呈現資料給使用者觀看
        intent.setAction(Intent.ACTION_VIEW);
        // 將Uri資訊附加到Intent物件上
        intent.setData(Uri.parse(uriStr));
        startActivity(intent);
    }

    //實作LocationCallback去定義每次一拿到位置資訊時想進行的處理
    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                location = locationResult.getLastLocation(); //獲取GPS位置資料
                if (location != null)   //決定做些什麼操作
                    updateLocationInfo();
            }
        };
    }

    //利用LocationRequest物件去設定對位置請求的編好設定
    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        // 10秒要一次位置資料 (但不一定, 有可能不到10秒, 也有可能超過10秒才要一次)
        locationRequest.setInterval(10000); //單位毫秒
        // 若有其他app也使用了LocationServices, 就會以此時間為取得位置資料的依據
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//優先取得高準確度的位置(GPS)
    }

    //建立LocationSettingsRequest物件用來檢查裝置是否已經打開位置功能
        private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }


    private void startLocationUpdates() {
        settingsClient.checkLocationSettings(locationSettingsRequest)
                //當位置設定正常，裝置也開始了位置功能，就會開始執行onSuccess方法
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                                locationCallback, Looper.myLooper());
                    }
                })

               // 當位置功能未開啟時則會執行onFailure方法
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.e(TAG, "Location settings are not satisfied. Attempting to upgrade location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(DiveshopdetailActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.e(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(DiveshopdetailActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

//

    private void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        Log.e(TAG, "Cancel location updates requested");
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void updateLocationInfo() {
//        TextView tvLastLocation = findViewById(R.id.tvLastLocation);
        StringBuilder msg = new StringBuilder();
        msg.append("自己位置相關資訊 \n");

        if (location == null) {
            Toast.makeText(this, getString(R.string.msg_LastLocationNotAvailable), Toast.LENGTH_SHORT).show();
            return;
        }

        // 取得定位時間
        Date date = new Date(location.getTime());
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String time = dateFormat.format(date);
        msg.append("定位時間: " + time + "\n");

        // 取得自己位置的緯經度、精準度、高度、方向與速度，不提供的資訊會回傳0.0
        msg.append("緯度: " + location.getLatitude() + "\n");
        msg.append("經度: " + location.getLongitude() + "\n");
        msg.append("精準度(公尺): " + location.getAccuracy() + "\n");
        msg.append("高度(公尺): " + location.getAltitude() + "\n");
        msg.append("方向(角度): " + location.getBearing() + "\n");
        msg.append("速度(公尺/秒): " + location.getSpeed() + "\n");

//        tvLastLocation.setText(msg);

    }
    // 將使用者輸入的地名或地址轉成Address物件
    private Address getAddress(String locationName) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addressList = null;
        try {
            // 解譯地名/地址後可能產生多筆位置資訊，但限定回傳1筆
            addressList = geocoder.getFromLocationName(locationName, 1);
        } catch (IOException ie) {
            Log.e(TAG, ie.toString());
        }

        if (addressList == null || addressList.isEmpty())
            return null;
        else
            // 因為當初限定只回傳1筆，所以只要取得第1個Address物件即可
            return addressList.get(0);
    }



    @Override
    protected void onStart() {
        super.onStart();
        askPermissions();
    }

    private void askPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        Set<String> permissionsRequest = new HashSet<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(permission);
            }
        }

        if (!permissionsRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
                    MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_CODE:
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        String text = getString(R.string.text_ShouldGrant);
                        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                break;
        }
    }


    public void showDetail(Diveshop diveshop) {
        recyclerView = findViewById(R.id.rvDpic);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        PagerSnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(new PicAdapter());

        String url = Util.URL + "DspicServletApp";
        String ds_no = diveshop.getDs_no();
        int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        Bitmap bitmap = null;

        //        try {
//            dpicImageTask = new ImageTask(url, ds_no, imageSize);
//            bitmap = dpicImageTask.execute().get();
//        } catch (Exception e) {
//            Log.e(TAG, e.toString());
//        }

//        if (bitmap != null) {
//            imageView.setImageBitmap (bitmap);
//        } else {
//            imageView.setImageResource(R.drawable.text_logo);
//        }

        TextView tvDiveshopName = findViewById(R.id.tvDsName);
        TextView tvDiveshopAddress = findViewById(R.id.tvDSadd);
        TextView tvDiveshopPhone = findViewById(R.id.tvDSphone);
        TextView tvDiveshopMail = findViewById(R.id.tvDSmail);

        tvDiveshopName.setText(diveshop.getDs_name());
        tvDiveshopAddress.setText(getString(R.string.col_dsaddress).trim()+diveshop.getAddress());
        tvDiveshopPhone.setText(getString(R.string.col_dsphone).trim()+diveshop.getTel());
        tvDiveshopMail.setText(getString(R.string.col_dsmail).trim()+diveshop.getDsmail());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dpicImageTask != null) {
            dpicImageTask.cancel(true);
        }
    }










    private class PicAdapter extends RecyclerView.Adapter<PicAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView ivDspic;

            ViewHolder(View view) {
                super(view);
                ivDspic = view.findViewById(R.id.ivDspic);
            }

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_diveshoppic, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String url = Util.URL + "DspicServletApp";
            String ds_no = diveshop.getDs_no();
            int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
            dpicImageTask = new ImageTask(url, ds_no, imageSize, position, holder.ivDspic);
            dpicImageTask.execute();

        }

        @Override
        public int getItemCount() {
            return diveshop.getCount();
        }
    }


}
