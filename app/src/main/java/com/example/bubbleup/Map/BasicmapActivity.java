package com.example.bubbleup.Map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.os.Bundle;

import com.example.bubbleup.Divepoint.Divepoint;
import com.example.bubbleup.Divepoint.DivepointFragment;
import com.example.bubbleup.Diveshop.Diveshop;
import com.example.bubbleup.Diveshop.DiveshoplistFragment;
import com.example.bubbleup.Main.Util;
import com.example.bubbleup.Member.Member;
import com.example.bubbleup.Task.CommonTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bubbleup.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BasicmapActivity extends AppCompatActivity  implements OnMapReadyCallback, LocationListener {
    private static final String TAG = "BasicmapActivity";
    private GoogleMap map;
    private Toolbar toolbar;
    static final int MIN_TIME = 5000;
    static final float MIN_DIST = 0;
    LocationManager manager;
    private List<Diveshop> diveshopList;
    private List<Divepoint> divepointList;
    private LatLng ds1,ds2,ds3,ds4,ds5 ;
    private List<LatLng> dsLatLngList,dpLatLngList;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    private CommonTask getDiveshopTask,getDivepointTask;

    LatLng currrPoint;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basicmap);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Member member = (Member)getIntent().getSerializableExtra("member");

        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        textView = findViewById(R.id.txv);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(member.getMem_name()+"的定位功能");
        setSupportActionBar(toolbar);



        divepointList = getAllDivepoint();
        diveshopList = getAllDiveshop();
        initDsPoints();
        initDpPoints();
        checkPermission();

    }



    private void initDsPoints() {
        dsLatLngList = new ArrayList<>();
        Double[] d =null;
        for(Diveshop diveshops:diveshopList){
            String strLatLng = diveshops.getDs_latlng().substring(1,diveshops.getDs_latlng().length()-1);
            String[] token = strLatLng.split(",");
            d = new Double[2];
            for(int i=0;i<2;i++){
               d[i] = Double.valueOf(token[i]);
            }
            dsLatLngList.add(new LatLng(d[0],d[1]));
        }


//        ds1 = new LatLng(25.0489774, 121.5655241);
//        ds2 = new LatLng(24.1769764, 120.6424333);
//        ds3 = new LatLng(21.948763, 120.7405298);
//        ds4 = new LatLng(25.0792018, 121.5427093);
//        ds5 = new LatLng(24.96842, 121.1956658);
//        dsLatLngList.add(ds1);
//        dsLatLngList.add(ds2);
//        dsLatLngList.add(ds3);
//        dsLatLngList.add(ds4);
//        dsLatLngList.add(ds5);

    }
    private  void initDpPoints(){
        dpLatLngList = new ArrayList<>();
        for(Divepoint divepoints:divepointList){
            dpLatLngList.add(new LatLng(divepoints.getDp_lat(),divepoints.getDp_lng()));
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(googleMap.MAP_TYPE_NORMAL);
        map.moveCamera(CameraUpdateFactory.zoomTo(7));
    }

    @Override
    protected void onResume(){
        super.onResume();
        enableLocationUpdates(true);
    }

    @Override
    protected  void onPause(){
        super.onPause();
        enableLocationUpdates(false);
    }

    @Override
    public void onLocationChanged(Location location){
        if(location!=null){
            textView.setText(String.format("緯度 %.4f, 經度 %.4f (%s 定位)",location.getLatitude(),location.getLongitude(),
                    location.getProvider()));
            currrPoint = new LatLng(location.getLatitude(),location.getLongitude());

            if( map!=null){
                map.animateCamera(CameraUpdateFactory.newLatLng(currrPoint));

                map.addMarker(new MarkerOptions().position(currrPoint).title("目前位置"));
            }

        }else {
            textView.setText("糟糕！無法取得定位資訊....");
        }
    }

    @Override
    public void onStatusChanged(String provider,int status,Bundle extras){

    }

    @Override
    public void onProviderEnabled(String provider){

    }

    @Override
    public void onProviderDisabled(String provider){

    }

    private void enableLocationUpdates(boolean isTurnOn){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if(isTurnOn){
                isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if(!isGPSEnabled&&!isNetworkEnabled){
                    Toast.makeText(BasicmapActivity.this,"請確定開啟定位功能",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(BasicmapActivity.this,"取得定位資訊中",Toast.LENGTH_SHORT).show();
                    if(isGPSEnabled){
                        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME,MIN_DIST,this);
                    }
                    if(isNetworkEnabled){
                        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME,MIN_DIST,this);
                    }
                }
            }else {
                manager.removeUpdates(this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode==200){
            if(grantResults.length>=1 && grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                Toast.makeText(BasicmapActivity.this,"需要定位權限",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},200);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_map,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.menu_settings:
                map.clear();
                map.addMarker(new MarkerOptions().position(map.getCameraPosition().target).title("我在這裡！"));

                break;
            case R.id.currLocation:
                map.animateCamera(CameraUpdateFactory.newLatLng(currrPoint));
                break;
            case R.id.satellite:
                menuItem.setChecked(!menuItem.isChecked());
                if(menuItem.isChecked()){
                    map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }else {
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                break;
            case R.id.traffic:
                menuItem.setChecked(!menuItem.isChecked());
                map.setTrafficEnabled(menuItem.isChecked());
                break;
            case R.id.setGPS:
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                break;
            case R.id.showDiveshop:
                menuItem.setChecked(!menuItem.isChecked());
                if(menuItem.isChecked()) {
                    for (int i = 0; i<diveshopList.size(); i++) {
                        map.addMarker(new MarkerOptions()
                                // 設定標記位置
                                .position(dsLatLngList.get(i))
                                // 設定標記標題
                                .title(diveshopList.get(i).getDs_name())
                                // 設定標記描述
                                .snippet(diveshopList.get(i).getDsinfo())
                                // 設定標記圖示
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.diveshop)));
                    }
                }else {
                    map.clear();
                }
                break;
            case R.id.showDivepoint:
                menuItem.setChecked(!menuItem.isChecked());
                if(menuItem.isChecked()) {
                    for (int i = 0; i < divepointList.size(); i++) {
                        map.addMarker(new MarkerOptions()
                                // 設定標記位置
                                .position(dpLatLngList.get(i))
                                // 設定標記標題
                                .title(divepointList.get(i).getDp_name())
                                // 設定標記描述
                                .snippet(divepointList.get(i).getDp_info())
                                // 設定標記圖示
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.divespoint)));
                    }
                }else {
                    map.clear();
                }
                break;
        }
        return true;
    }


    private List<Diveshop> getAllDiveshop(){
        List<Diveshop> diveshopList = null;
        if(Util.networkConnected(this)){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","getAll");
            String jsonOut = jsonObject.toString();
            getDiveshopTask = new CommonTask(Util.URL+"DiveshopServletApp",jsonOut);
            try {
                String jsonIn = getDiveshopTask.execute().get();
                Type listType = new TypeToken<List<Diveshop>>(){}.getType();
                diveshopList = new Gson().fromJson(jsonIn, listType);
            }catch (Exception e){
                Log.e(TAG, e.toString());
            }
        }else{
            Util.showToast(this,R.string.msg_NoNetwork);
        }
        return  diveshopList;
    }

    private List<Divepoint> getAllDivepoint(){
        List<Divepoint> divepointList = null;
        if(Util.networkConnected(this)){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","getAll");
            String jsonOut = jsonObject.toString();
            getDivepointTask = new CommonTask(Util.URL+"DivepointServletApp",jsonOut);

            try {
                String jsonIn = getDivepointTask.execute().get();
                Type listType = new TypeToken<List<Divepoint>>(){}.getType();
                divepointList = new Gson().fromJson(jsonIn, listType);
            }catch (Exception e){
                Log.e(TAG, e.toString());
            }

        }else{
            Util.showToast(this,R.string.msg_NoNetwork);
        }
         return divepointList;
    }

}
