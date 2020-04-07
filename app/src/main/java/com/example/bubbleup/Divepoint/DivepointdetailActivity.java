package com.example.bubbleup.Divepoint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bubbleup.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class DivepointdetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView tvDptitle;
    private GoogleMap map;
    private Divepoint divepoint;
    private LatLng latLng;
    // 顯示標記被拖曳後的相關訊息，例如緯經度
    private TextView tvMarkerDrag;
    private Marker marker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_divepointdetail);

        divepoint = (Divepoint) this.getIntent().getSerializableExtra("divepoint");


        tvDptitle = findViewById(R.id.tvDptitle);


        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.fmMap);

        tvDptitle.setText(divepoint.getDp_name());

        mapFragment.getMapAsync(this);


        latLng = new LatLng(divepoint.getDp_lat(), divepoint.getDp_lng());

    }




    @Override
    public void onMapReady(GoogleMap googleMap){
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        setupMap();

    }

    @SuppressLint("MissingPermission")
    private void setupMap() {
        map.setMyLocationEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                // 鏡頭焦點在玉山
                .target(latLng)
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
        marker = map.addMarker(new MarkerOptions()
                // 設定標記位置
                .position(latLng)
                // 設定標記標題
                .title(divepoint.getDp_name())
                // 設定標記描述
                .snippet(divepoint.getDp_info())
                // 設定標記圖示
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.point2)));


    }

    private class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View infoWindow;

        MyInfoWindowAdapter() {
            infoWindow = LayoutInflater.from(DivepointdetailActivity.this)
                    .inflate(R.layout.custom_infowindow, null);
        }

        @Override
        // 回傳設計好的訊息視窗樣式
        // 回傳null會自動呼叫getInfoContents(Marker)
        public View getInfoWindow(Marker marker) {
            int logoId;
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

//            // 顯示圖示
//            ImageView ivLogo = infoWindow.findViewById(R.id.ivLogo);
//            ivLogo.setImageResource(logoId);

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
            Toast.makeText(DivepointdetailActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        // 點擊標記的訊息視窗
        public void onInfoWindowClick(Marker marker) {
            Toast.makeText(DivepointdetailActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
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



}
