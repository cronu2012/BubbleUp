package com.example.bubbleup.Divepoint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.bubbleup.R;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.bubbleup.Diveshop.Diveshop;
import com.example.bubbleup.Main.Util;

import android.annotation.SuppressLint;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bubbleup.Task.CommonTask;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MarkpointActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MarkpointActivity";

    private GoogleMap map;
    private List<Diveshop> dsList;
    private List<Divepoint> dpList;
    private Button btnSetDp, btnSetDs;
    private boolean dpords = true;
    private List<LatLng> dpLatlngList, dsLatlngList;
    private LatLng latLng;
    private Marker mkdivepoint, mkdiveshop;

    // 各地標記：太魯閣、玉山、墾丁、陽明山
    private Marker marker_taroko, marker_yushan, marker_kenting, marker_yangmingshan;
    // 顯示標記被拖曳後的相關訊息，例如緯經度
    private TextView tvMarkerDrag;
    // 各地緯經度：太魯閣、玉山、墾丁、陽明山
    private LatLng taroko, yushan, kenting, yangmingshan;


    private CommonTask getAllDivepointTask, getAllDiveshopTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markpoint);
        initPoints();
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fmMap);
        mapFragment.getMapAsync(this);
        tvMarkerDrag = (TextView) findViewById(R.id.tvMarkerDrag);
    }

    // 初始化所有地點的緯經度
    private void initPoints() {
        if (dpords) {
            dpLatlngList = new ArrayList<>();
            for (Divepoint dp : dpList) {
                latLng = new LatLng(dp.getDp_lat(), dp.getDp_lng());
                dpLatlngList.add(latLng);
            }
        } else {
            for (Diveshop ds : dsList) {
                dsLatlngList.add(new LatLng(Double.parseDouble(ds.getDs_latlng().substring(1, 10)), Double.parseDouble(ds.getDs_latlng().substring(13, 23))));
            }
        }


    }


    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        setupMap();
    }

    // 完成地圖相關設定
    @SuppressLint("MissingPermission")
    private void setupMap() {
        map.setMyLocationEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                // 鏡頭焦點在玉山
                .target(yushan)
                // 地圖縮放層級定為7
                .zoom(7)
                .build();
        // 改變鏡頭焦點到指定的新地點
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.animateCamera(cameraUpdate);

        addMarkersToMap();

        // 如果不套用自訂InfoWindowAdapter會自動套用預設訊息視窗
        map.setInfoWindowAdapter(new MyInfoWindowAdapter());

        MyMarkerListener listener = new MyMarkerListener();
        // 註冊OnMarkerClickListener，當標記被點擊時會自動呼叫該Listener的方法
        map.setOnMarkerClickListener(listener);
        // 註冊OnInfoWindowClickListener，當標記訊息視窗被點擊時會自動呼叫該Listener的方法
        map.setOnInfoWindowClickListener(listener);
        // 註冊OnMarkerDragListener，當標記被拖曳時會自動呼叫該Listener的方法
        map.setOnMarkerDragListener(listener);

    }

    // 在地圖上加入多個標記
    private void addMarkersToMap() {
        if (dpords) {
            for (int i = 0; i < dpList.size(); i++) {
                mkdivepoint = map.addMarker(new MarkerOptions()
                        // 設定標記位置
                        .position(dpLatlngList.get(i))
                        // 設定標記標題
                        .title(dpList.get(i).getDp_name())
                        // 設定標記描述
                        .snippet(dpList.get(i).getDp_info()));
                // 設定標記圖示

            }
        } else {
            for (int i = 0; i < dsList.size(); i++) {
                mkdiveshop = map.addMarker(new MarkerOptions()
                        // 設定標記位置
                        .position(dsLatlngList.get(i))
                        // 設定標記標題
                        .title(dsList.get(i).getDs_name())
                        // 設定標記描述
                        .snippet(dsList.get(i).getDsinfo()));
            }
        }
    }
    // 自訂InfoWindowAdapter，當點擊標記時會跳出自訂風格的訊息視窗
    private class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View infoWindow;

        MyInfoWindowAdapter() {
            infoWindow = LayoutInflater.from(MarkpointActivity.this)
                    .inflate(R.layout.custom_infowindow, null);
        }

        @Override
//        // 回傳設計好的訊息視窗樣式
//        // 回傳null會自動呼叫getInfoContents(Marker)
        public View getInfoWindow(Marker marker) {
//            int logoId;
//            // 使用equals()方法檢查2個標記是否相同，千萬別用「==」檢查
//            if (marker.equals(marker_yangmingshan)) {
//                logoId = R.drawable.logo_yangmingshan;
//            } else if (marker.equals(marker_taroko)) {
//                logoId = R.drawable.logo_taroko;
//            } else if (marker.equals(marker_yushan)) {
//                logoId = R.drawable.logo_yushan;
//            } else if (marker.equals(marker_kenting)) {
//                logoId = R.drawable.logo_kenting;
//            } else {
//                // 呼叫setImageResource(int)傳遞0則不會顯示任何圖形
//                logoId = 0;
//            }

            // 顯示圖示
            ImageView ivLogo = infoWindow.findViewById(R.id.ivLogo);
            ivLogo.setImageResource(R.drawable.logo2);

            // 顯示標題
            String title = marker.getTitle();
            TextView tvTitle = infoWindow.findViewById(R.id.tvTitle);
            tvTitle.setText(title);

            // 顯示描述
            String snippet = marker.getSnippet();
            TextView tvSnippet = infoWindow.findViewById(R.id.tvSnippet);
            tvSnippet.setText(snippet);

            return infoWindow;
         }
//
        @Override
        // 當getInfoWindow(Marker)回傳null時才會呼叫此方法
        // 此方法如果再回傳null，代表套用預設視窗樣式
        public View getInfoContents(Marker marker) {
            return null;
        }
    }


    private class MyMarkerListener implements GoogleMap.OnMarkerClickListener,
            GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerDragListener {

        @Override
        // 點擊地圖上的標記
        public boolean onMarkerClick(Marker marker) {
            Toast.makeText(MarkpointActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        // 點擊標記的訊息視窗
        public void onInfoWindowClick(Marker marker) {
            Toast.makeText(MarkpointActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
        }

        @Override
        // 開始拖曳標記
        public void onMarkerDragStart(Marker marker) {
            tvMarkerDrag.setText("onMarkerDragStart");
        }

        @Override
        // 拖曳標記過程中會不斷呼叫此方法
        public void onMarkerDrag(Marker marker) {
            // 以TextView顯示標記的緯經度
            tvMarkerDrag.setText("onMarkerDrag.  Current Position: " + marker.getPosition());
        }

        @Override
        // 結束拖曳標記
        public void onMarkerDragEnd(Marker marker) {
            tvMarkerDrag.setText("onMarkerDragEnd");
        }
    }




    private List<Divepoint> getAllDivepoint(){
        List<Divepoint> list = new ArrayList<>();
        if(Util.networkConnected(this)){
            String url = Util.URL+"DivepointServletApp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","getAll");
            String jsonOut = jsonObject.toString();
            getAllDivepointTask = new CommonTask(url,jsonOut);
            try {
                String jsonIn = getAllDivepointTask.execute().get();
                Type type = new TypeToken<List<Divepoint>>(){}.getType();
                list = new Gson().fromJson(jsonIn,type);
            }catch(Exception e){
                Log.e(TAG,e.toString());
            }
        }else{
            Util.showToast(this,R.string.msg_NoNetwork);
        }
        return  list;
    }

    private List<Diveshop> getAllDiveshop(){
        List<Diveshop> list = new ArrayList<>();
        if(Util.networkConnected(this)){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","getAll");
            String jsonOut = jsonObject.toString();
            getAllDiveshopTask = new CommonTask(Util.URL+"DiveshopServletApp",jsonOut);
            try {
                String jsonIn = getAllDiveshopTask.execute().get();
                Type listType = new TypeToken<List<Diveshop>>(){}.getType();
                list = new Gson().fromJson(jsonIn, listType);
            }catch (Exception e){
                Log.e(TAG, e.toString());
            }

        }else{
            Util.showToast(this,R.string.msg_NoNetwork);
        }
        return list;
    }

}
