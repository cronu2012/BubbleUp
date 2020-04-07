package com.example.bubbleup.Main;

import android.Manifest;
import android.app.Dialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.bubbleup.Divepoint.DivepointFragment;
import com.example.bubbleup.Diveshop.DiveshoplistFragment;
import com.example.bubbleup.Map.GolocationFragment;
import com.example.bubbleup.Member.LoginActivity;
import com.example.bubbleup.Member.Member;
import com.example.bubbleup.Member.MemberdetailFragment;
import com.example.bubbleup.OrderList.OrderListFragment;
import com.example.bubbleup.R;
import com.example.bubbleup.Task.CommonTask;
import com.example.bubbleup.Task.MempicTask;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import static com.example.bubbleup.Main.Util.PREF_FILE;

public class MainActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener{
    private static final int REQUEST_LOGIN = 1;
    private static final String TAG = "MainActivity";
    private static final String Tag_Divepoint = "fragment_dp";
    private static final String Tag_Diveshop = "fragment_ds";
    private static final String Tag_OrderList = "fragment_orderlist";
    private static final String Tag_Memgerinfo = "fragment_memberinfo";
    private static final String Tag_Golocation = "fragment_golocation";
    private static final String Tag_Light = "fragment_light";
    private final static int REQ_PERMISSIONS = 0;
    private static final int MY_REQUEST_CODE = 1;
    private CommonTask getMemnoTask;
    private CommonTask getMemTask;
    private MempicTask getMemPicTask;
    private static  ContextWrapper contextWrapper;
    private  Fragment  diveshoplistFragment, orderlistFragment, memberdetailFragment,divepointFragment,golocationFragment,lightFragment;
    private NavigationView navigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!Util.networkConnected(this)){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }






        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ImageView ivHeaderPic = navigationView.getHeaderView(0).findViewById(R.id.ivHeaderPic);
        TextView tvHeaderName = navigationView.getHeaderView(0).findViewById(R.id.tvHeaderName);

        SharedPreferences preferences = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
        String mem_id = preferences.getString("mem_id","");
        String mem_psw = preferences.getString("mem_psw","");
        Member member= findMem(mem_id,mem_psw);

        String url = Util.URL + "MemServletApp";
        int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        Bitmap bitmap = null;
        try {
            getMemPicTask = new MempicTask(url, member.getMem_no(), imageSize,ivHeaderPic);
            bitmap = getMemPicTask.execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            ivHeaderPic.setImageBitmap(bitmap);
        }
        else {
            ivHeaderPic.setImageResource(R.drawable.mempic_default);
        }

        tvHeaderName.setText(member.getMem_name()+"    你好！");


        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        diveshoplistFragment= new DiveshoplistFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.
                frameLayout,diveshoplistFragment, Tag_Diveshop).commit();



    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_LOGIN:
                    SharedPreferences preferences =
                            getSharedPreferences(PREF_FILE, MODE_PRIVATE);
                    boolean login = preferences.getBoolean("login", false);

                    if (!login) {
                        Util.showToast(MainActivity.this, "login failed");
                        onLogin();
                    }

            }
        }
    }
    private void onLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(loginIntent, REQUEST_LOGIN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 從偏好設定檔中取得登入狀態來決定是否顯示「登出」
        SharedPreferences pref = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        boolean login = pref.getBoolean("login", false);
        if (!login) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        askPermissions();
    }




    private void askPermissions() {
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION
        };

        int result = ContextCompat.checkSelfPermission(this, permissions[0]);
        if (result != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    REQ_PERMISSIONS);
        }


//        Set<String> permissionsRequest = new HashSet<>();
//        for (String permission : permissions) {
//            int result1 = ContextCompat.checkSelfPermission(this, permission);
//            if (result1 != PackageManager.PERMISSION_GRANTED) {
//                permissionsRequest.add(permission);
//            }
//        }
//
//        if (!permissionsRequest.isEmpty()) {
//            ActivityCompat.requestPermissions(this,
//                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
//                    MY_REQUEST_CODE);
//        }
    }


    @Override
    public  void  onBackPressed(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            =  new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()){
                case R.id.navigation_diveshop :
                    if(diveshoplistFragment==null){
                        diveshoplistFragment= new DiveshoplistFragment();
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.
                            frameLayout,diveshoplistFragment, Tag_Diveshop).commit();
                    return true;
                case R.id.navigation_divepiont:
                    if(divepointFragment==null){
                        divepointFragment= new DivepointFragment();
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.
                            frameLayout,divepointFragment, Tag_Divepoint).commit();
                    return true;
                case R.id.navigation_point:
                    if(golocationFragment==null){
                        golocationFragment= new GolocationFragment();
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.
                            frameLayout,golocationFragment, Tag_Golocation).commit();

                    return true;
                case R.id.navigation_chatRoom:
                    if(lightFragment==null){
                        lightFragment= new LightFragment();
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.
                            frameLayout,lightFragment, Tag_Light).commit();

                    return true;

            }
            return false;
        }
    };



    @Override
    public boolean onNavigationItemSelected(MenuItem item){

        switch(item.getItemId()){

            case R.id.nav_memInfo:
                if(memberdetailFragment==null){
                    memberdetailFragment = new MemberdetailFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout,memberdetailFragment,Tag_Memgerinfo).commit();
                break;
            case R.id.nav_ordQuery:
                if(orderlistFragment==null){
                    orderlistFragment = new OrderListFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout,orderlistFragment,Tag_OrderList).commit();
                break;

            case R.id.nav_logout:
                AlertFragment alertFragment = new AlertFragment();
                FragmentManager fm = getSupportFragmentManager();
                alertFragment.show(fm, "alert");
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public static class AlertFragment extends DialogFragment implements DialogInterface.OnClickListener{
        @Override
        public Dialog onCreateDialog(Bundle getInstanceState){
              AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                      .setIcon(R.drawable.ic_help_outline_black_48dp)
                      .setTitle(R.string.msg_sysmsg)
                      .setMessage(R.string.msg_underline)
                      .setPositiveButton(R.string.btn_sure,this)
                      .setNegativeButton(R.string.btn_cancel,this)
                      .create();
               return  alertDialog;
        }
        @Override
        public void onClick(DialogInterface dialog,int which){
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:

                    SharedPreferences preferences = getActivity().getSharedPreferences(PREF_FILE,MODE_PRIVATE);
                    preferences.edit().putBoolean("login",false).putString("mem_id","").putString("mem_psw","").apply();
//                    getView().setVisibility(getView().INVISIBLE);
                    getActivity().finish();
                    Intent intent = new Intent(getActivity(),LoginActivity.class);
                    startActivity(intent);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.cancel();
                    break;
                default:
                    break;
            }
        }
    }

    private Member findMem(String mem_id,String mem_psw){
        Member member = null;
        if (Util.networkConnected(this)) {
            String url = Util.URL + "MemServletApp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findOneByIdPsw");
            jsonObject.addProperty("mem_id", mem_id);
            jsonObject.addProperty("mem_psw", mem_psw);
            String jsonOut = jsonObject.toString();
            getMemTask = new CommonTask(url, jsonOut);
            member = new Member();
            try {
                String jsonIn = getMemTask.execute().get();
                Type type = new TypeToken<Member>(){}.getType();
                member = new GsonBuilder().setDateFormat("yyyy-MM-dd").create().fromJson(jsonIn, type);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (member == null) {
                Util.showToast(this, R.string.msg_ListNotFound);
            }
        } else {
            Util.showToast(this, R.string.msg_NoNetwork);
        }
        return member;
    }




}


























