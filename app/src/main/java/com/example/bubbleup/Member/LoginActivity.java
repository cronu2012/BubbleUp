package com.example.bubbleup.Member;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bubbleup.Diveshop.Diveshop;
import com.example.bubbleup.Diveshop.DiveshopMasterActivity;
import com.example.bubbleup.Lesson.CreditcardActivity;
import com.example.bubbleup.Main.MainActivity;
import com.example.bubbleup.Main.Util;
import com.example.bubbleup.OrderList.LessonOrderlistActivity;
import com.example.bubbleup.R;
import com.example.bubbleup.Task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.bubbleup.Main.Util.PREF_FILE;
import static java.lang.Thread.sleep;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText etId, etPassword;
    private Button btnRegister,btnLogin,btnLogo,btnDsLogin,btnMem, btnDs;
    private ImageView ivLogo;
    private CommonTask isMemberTask, getMemnoTask,isDiveshopMemTask,getAllMemberTask,getAllDiveshopTask;
    private List<Member> list;
    private List<Diveshop> diveshopList;
    private int memberIndex=0;
    private int diveshopIndex=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences preferences =
                getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        boolean login = preferences.getBoolean("login", false);

        if (login) {
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }
        findViews();
        setResult(RESULT_CANCELED);
    }


    public void findViews() {
        etId = findViewById(R.id.etDsaccount);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnDslogin);
        btnLogo = findViewById(R.id.btnLogo);
        btnDsLogin = findViewById(R.id.btnDsLogin);
        btnMem = findViewById(R.id.btnMem);
        btnDs = findViewById(R.id.btnDs);


        btnMem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list=getAllMember();
                etId.setText(list.get(memberIndex).getMem_id());
                etPassword.setText(list.get(memberIndex
                ).getMem_psw());
                memberIndex++;
                if(memberIndex>list.size()-1){
                    memberIndex=0;
                }
            }
        });

        btnDs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diveshopList = getAllDiveshop();
                etId.setText(diveshopList.get(diveshopIndex).getDsaccount());
                etPassword.setText(diveshopList.get(diveshopIndex).getDspaw());
                diveshopIndex++;
                if(diveshopIndex>diveshopList.size()-1){
                   diveshopIndex=0;
                }
            }
        });




        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mem_id = etId.getText().toString().trim();
                String mem_psw = etPassword.getText().toString().trim();
                if(mem_id.isEmpty()){
                    etId.setError("不可為空白");
                }
                if(mem_psw.isEmpty()){
                    etPassword.setError("不可為空白");
                }
                if (mem_id.length() <= 0 || mem_psw.length() <= 0) {
                    showToast(R.string.msg_InvalidUserOrPassword);
                    return;
                }

                if (isMember(mem_id, mem_psw)) {
                    SharedPreferences preferences = getSharedPreferences(
                            Util.PREF_FILE, MODE_PRIVATE);
                    preferences.edit().putBoolean("login", true)
                            .putString("mem_id", mem_id)
                            .putString("mem_psw", mem_psw).apply();

                    setResult(RESULT_OK);

                    SweetAlertDialog pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("歡迎登入");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    Timer timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            finish();
                            startActivity(intent);
                        }
                    };
                    timer.schedule(timerTask,1500);


                } else {
                    showToast(R.string.msg_InvalidUserOrPassword);
                }

            }
        });

        btnDsLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dsaccount = etId.getText().toString().trim();
                String dspaw = etPassword.getText().toString().trim();
                if(dsaccount.isEmpty()){
                    etId.setError("不可為空白");
                }
                if(dspaw.isEmpty()){
                    etPassword.setError("不可為空白");
                }
                if (dsaccount.length() <= 0 || dspaw.length() <= 0) {
                    showToast(R.string.msg_InvalidUserOrPassword);
                    return;
                }

                if(isDiveshopMem(dsaccount,dspaw)){
                    SharedPreferences preferences = getSharedPreferences(
                            Util.PREF_FILE, MODE_PRIVATE);
                    preferences.edit().putBoolean("Dslogin", true)
                            .putString("dsaccount", dsaccount)
                            .putString("dspaw", dspaw).apply();
                    setResult(RESULT_OK);

                    SweetAlertDialog pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#0072E3"));
                    pDialog.setTitleText("歡迎登入");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    final Intent intent = new Intent(LoginActivity.this, DiveshopMasterActivity.class);
                    Timer timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            finish();
                            startActivity(intent);
                        }
                    };
                    timer.schedule(timerTask,1500);

                }else {
                    showToast(R.string.msg_InvalidUserOrPassword);
                }
            }
        });


    }

    private  boolean isDiveshopMem(String dsaccount,String dspaw){
        boolean isDiveshopMem = false;
        if(Util.networkConnected(this)){
            String url = Util.URL+ "DiveshopServletApp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","isDiveshopMem");
            jsonObject.addProperty("dsaccount",dsaccount);
            jsonObject.addProperty("dspaw",dspaw);
            String jsonOut = jsonObject.toString();
            isDiveshopMemTask = new CommonTask(url,jsonOut);
            try{
                String result = isDiveshopMemTask.execute().get();
                isDiveshopMem = Boolean.valueOf(result);
            }catch(Exception e){
                Log.e(TAG, e.toString());
                isDiveshopMem = false;
            }
        }else {
            Util.showToast(this, R.string.msg_NoNetwork);
        }
        return isDiveshopMem;
    }



    private boolean isMember(final String mem_id, final String mem_psw) {
        boolean isMember = false;
        if (Util.networkConnected(this)) {
            String url = Util.URL + "MemServletApp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "isMember");
            jsonObject.addProperty("mem_id", mem_id);
            jsonObject.addProperty("mem_psw", mem_psw);
            String jsonOut = jsonObject.toString();
            isMemberTask = new CommonTask(url, jsonOut);
            try {
                String result = isMemberTask.execute().get();
                isMember = Boolean.valueOf(result);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                isMember = false;
            }
        } else {
            Util.showToast(this, R.string.msg_NoNetwork);
        }
        return isMember;
    }

    private List<Diveshop> getAllDiveshop(){
        List<Diveshop> list = new ArrayList<>();
        if(Util.networkConnected(this)){
            String url = Util.URL+"DiveshopServletApp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","getAll");
            String jsonOut = jsonObject.toString();
            getAllDiveshopTask = new CommonTask(url,jsonOut);
            try {
                String jsonIn = getAllDiveshopTask.execute().get();
                Type  type = new TypeToken<List<Diveshop>>(){}.getType();
                list = new Gson().fromJson(jsonIn,type);
            }catch(Exception e){
                Log.e(TAG, e.toString());
            }
        }else {
        Util.showToast(this, R.string.msg_NoNetwork);
        }
        return list;
    }

    private List<Member> getAllMember(){
        List<Member> list = new ArrayList<>();
        if(Util.networkConnected(this)){
            String url = Util.URL+"MemServletApp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","getAll");
            String jsonOut = jsonObject.toString();
            getAllMemberTask = new CommonTask(url,jsonOut);
            try {
                String jsonIn = getAllMemberTask.execute().get();
                Type  type = new TypeToken<List<Member>>(){}.getType();
                list = new GsonBuilder().setDateFormat("yyyy-MM-dd").create().fromJson(jsonIn,type);
            }catch(Exception e){
                Log.e(TAG, e.toString());
            }
        }else {
            Util.showToast(this, R.string.msg_NoNetwork);
        }
        return list;
    }


    @Override
    protected void onResume() {
        super.onResume();
        // 從偏好設定檔中取得登入狀態來決定是否顯示「登出」
        SharedPreferences pref = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        boolean login = pref.getBoolean("Dslogin", false);
        if (login) {
            Intent intent = new Intent(LoginActivity.this, DiveshopMasterActivity.class);
            startActivity(intent);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = getSharedPreferences(Util.PREF_FILE,
                MODE_PRIVATE);
        boolean login = preferences.getBoolean("login", false);
        if (login) {
            String mem_id = preferences.getString("mem_id", "");
            String mem_psw = preferences.getString("mem_psw", "");
            if (isMember(mem_id, mem_psw)) {
                setResult(RESULT_OK);
                finish();
            }
        }

    }


    private void showToast(int msgResId) {
        Toast.makeText(LoginActivity.this,msgResId,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isMemberTask != null) {
            isMemberTask.cancel(true);
        }
    }




}
