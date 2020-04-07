package com.example.bubbleup.Member;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bubbleup.Main.Util;
import com.example.bubbleup.R;
import com.example.bubbleup.Task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private EditText etUserId,etUserPsw,etUserPswAgain, etName, etPhone, etMail, etAddress;
    private RadioGroup radioGroupSex;
    private Spinner spCitys,spYear,spMonth,spDay;

    private ArrayList<String> dateYear = new ArrayList<>();
    private ArrayList<String> dateMonth = new ArrayList<>();
    private ArrayList<String> dateDay = new ArrayList<>();
    private ArrayAdapter<CharSequence>  adapterSpCity;
    private ArrayAdapter<String> adapterSpYear;
    private ArrayAdapter<String> adapterSpMonth;
    private ArrayAdapter<String> adapterSpDay;

    private Button btnSubmit, btnClear, btnConfirmId,btnMagic;
    private boolean memExist = false;
    private CommonTask memExistTask, memRegisterTask,getMemnoTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findSpinner();
        findView();

    }
    public void findView(){
        etUserId = findViewById(R.id.etUserId);
        etUserPsw = findViewById(R.id.etUserPsw);
        etUserPswAgain = findViewById(R.id.etUserPswAgain);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etMail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        radioGroupSex = findViewById(R.id.radioGroupSex);
        btnSubmit = findViewById(R.id.btnFindLes);
        btnClear = findViewById(R.id.btnClear);
        btnConfirmId = findViewById(R.id.btnConfirmId);
        btnMagic = findViewById(R.id.btnMagic);

        btnMagic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUserId.setText("cronu2020");
                etUserPsw.setText("123456");
                etUserPswAgain.setText("123456");
                etName.setText("芬哥芬哥");
                etPhone.setText("0910123123");
                etMail.setText("cronu2020@gmail.com");
                etAddress.setText("中華路三段100號");
                radioGroupSex.check(R.id.rdMale);
            }
        });



        btnConfirmId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mem_id = etUserId.getText().toString().trim();
                if(mem_id.isEmpty()){
                    etUserId.setError("不可空白！");
                }else if(isMemIdExist(mem_id)){
                    etUserId.setError("此帳號已被建立！");
                }else if(!mem_id.matches("[a-zA-Z0-9]{6,14}")){
                    etUserId.setError("帳號格式不正確！");
                }else if(!isMemIdExist(mem_id)){
                    Toast.makeText(RegisterActivity.this,R.string.UserIdOK,Toast.LENGTH_LONG).show();
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUserId.setText("");
                etUserPsw.setText("");
                etUserPswAgain.setText("");
                etName.setText("");
                etPhone.setText("");
                etMail.setText("");
                etAddress.setText("");

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                java.text.DateFormat dfTime = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String formatTime = dfTime.format(new java.util.Date());

                //將年月日的spinner選定的日期個別轉成字串，再合體成正確的日期格式字串
                String year = spYear.getSelectedItem().toString();
                String month = spMonth.getSelectedItem().toString();
                String day = spDay.getSelectedItem().toString();
                String str_Birthday = year+"-"+month+"-"+day;

                String mem_id = etUserId.getText().toString().trim();
                String mem_psw = etUserPsw.getText().toString().trim();
                String mem_pswAgain = etUserPswAgain.getText().toString().trim();
                String mem_name = etName.getText().toString().trim();
                int memSex;

                Date mem_bd = Date.valueOf(str_Birthday);
                String mem_phone = etPhone.getText().toString().trim();
                String mem_mail = etMail.getText().toString().trim();
                String mem_add = etAddress.getText().toString().trim();
                String mem_cityAdd = spCitys.getSelectedItem().toString().trim()+ mem_add;
                Timestamp reg_time = java.sql.Timestamp.valueOf(formatTime);
                Integer mem_rep_no = 0;
                String mem_state = "待審核";

                String err_empty = "不可空白！";



                if(radioGroupSex.getCheckedRadioButtonId() == R.id.rdMale){
                    memSex = 1 ;
                }else if(radioGroupSex.getCheckedRadioButtonId() == R.id.rdFemale){
                    memSex = 2 ;
                }else{
                    memSex = 0 ;
                }

                boolean isInputValid=true;
                if(isMemIdExist(mem_id)){
                    Toast.makeText(RegisterActivity.this,R.string.msg_UserIdExist,Toast.LENGTH_LONG).show();
                    etUserId.setError("此帳號已被建立！");
                    isInputValid = false;
                }else if(mem_id.isEmpty()){
                    etUserId.setError(err_empty);
                    isInputValid = false;
                }else if(!mem_id.matches("[a-zA-Z0-9]{6,14}")){
                    etUserId.setError("請輸入6到14位的英文或數字");
                    isInputValid = false;
                }
                if(mem_psw.isEmpty()){
                    etUserPsw.setError(err_empty);
                    isInputValid = false;
                }else if(!mem_psw.matches("[a-zA-Z0-9]{6,14}")){
                    etUserPsw.setError("請輸入6到14位的英文或數字");
                    isInputValid = false;
                }
                if(mem_pswAgain.isEmpty()){
                    etUserPswAgain.setError(err_empty);
                    isInputValid = false;
                }else if(!mem_pswAgain.matches(mem_psw)){
                    etUserPswAgain.setError("密碼不一致！");
                    isInputValid= false;
                }
                if(mem_name.isEmpty()){
                    etName.setError(err_empty);
                    isInputValid = false;
                }
                if(mem_phone.isEmpty()){
                    etPhone.setError(err_empty);
                    isInputValid = false;
                }else if(!(mem_phone.matches("[0][9][0-9]{6}")||mem_phone.matches("[0][0-9]{9}"))){
                    etPhone.setError("電話號碼格式錯誤");
                    isInputValid =false;
                }
                if(mem_mail.isEmpty()){
                    etMail.setError(err_empty);
                    isInputValid = false;
                } else if(!mem_mail.matches("[a-zA-Z0-9_]+@[a-zA-Z0-9\\._]+")){
                    etMail.setError("電子郵件格式錯誤！");
                    isInputValid = false;
                }
                if(mem_add.isEmpty()){
                    etAddress.setError(err_empty);
                    isInputValid = false;
                }
                if(memSex==0){
                    Toast.makeText(RegisterActivity.this,R.string.msg_noSelectSex,Toast.LENGTH_LONG).show();
                    isInputValid = false;
                }

             AddMem member =
                     new AddMem(mem_id,mem_psw,mem_name, memSex, mem_bd, mem_mail,mem_phone, mem_cityAdd, reg_time, mem_rep_no, mem_state);

                if(isInputValid){
                    if(Util.networkConnected(RegisterActivity.this)){
                        String url = Util.URL+"MemServletApp";
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action","add");
                        jsonObject.addProperty("memVO",new GsonBuilder().setDateFormat("yyyy-MM-dd").create().toJson(member));
                        String jsonOut = jsonObject.toString();
                        memRegisterTask = new CommonTask(url,jsonOut);
                        boolean isSuccess = false;
                        try {
                            String result = memRegisterTask.execute().get();
                            isSuccess = Boolean.valueOf(result);
                        } catch (Exception e){
                            Log.e(TAG, e.toString());
                        }
                        if(isSuccess){
//                        SharedPreferences preferences = getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
//                          preferences.edit().putBoolean("login",true)
//                                    .putString("mem_id",mem_id)
//                                    .putString("mem_psw",mem_psw).apply();
                            Util.showToast(RegisterActivity.this,R.string.msg_SuccessRegisterAccount);
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(RegisterActivity.this,R.string.msg_FailRegister,Toast.LENGTH_LONG).show();
                        }

                    }else {
                        Util.showToast(RegisterActivity.this,R.string.msg_NoNetwork);
                    }
                }


            }
        });




    }



    public void findSpinner(){
        spCitys = findViewById(R.id.spCitys);
        spYear = findViewById(R.id.spYear);
        spMonth = findViewById(R.id.spMonth);
        spDay = findViewById(R.id.spDate);

        //縣市Spinner
        //因為陣列是放在專案資源中，可使用ArrayAdapter的類別方法createFromResource，直接產生一個ArrayAdapter<CharSequence>的物件
        /*第一個參數為Context，使用this關鍵字即可。

          第二個參數是陣列資源的ID值，給予R.array.notify_array

          第三個參數代表未來清單顯示時使用的版面配置，使用的是Android SDK的版面XML「simple_spinner_item」*/
        adapterSpCity =
                ArrayAdapter.createFromResource(this,R.array.citys_array,android.R.layout.simple_spinner_item);

        //設定一個比較美觀的版面
        adapterSpCity.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        //最後再將arrayAdapter物件設定至Spinner元件中
        spCitys.setAdapter(adapterSpCity);


        Calendar cal = Calendar.getInstance();
        for(int i=0;i<70;i++){
            dateYear.add(""+(cal.get(Calendar.YEAR)-69+i));
        }
        adapterSpYear = new ArrayAdapter<String>(this,R.layout.spinner_item,dateYear);
        adapterSpYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYear.setAdapter(adapterSpYear);
        spYear.setSelection(69);

        for(int i=1;i<13;i++){
            dateMonth.add(""+(i<10?"0"+i:i));
        }
        adapterSpMonth = new ArrayAdapter<String>(this,R.layout.spinner_item,dateMonth);
        adapterSpMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMonth.setAdapter(adapterSpMonth);

        adapterSpDay = new ArrayAdapter<String>(this,R.layout.spinner_item,dateDay);
        adapterSpDay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDay.setAdapter(adapterSpDay);

        spMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Calendar cal = Calendar.getInstance();

                dateDay.clear();
                cal.set(Calendar.YEAR,Integer.valueOf(spYear.getSelectedItem().toString()));
                cal.set(Calendar.MONTH,position);

                int dayform = cal.getActualMaximum(Calendar.DAY_OF_MONTH);  //在此行判定月份的天數

                for(int i=1;i<=dayform;i++){
                    dateDay.add(""+(i<10?"0"+i:i));
                }
                adapterSpMonth.notifyDataSetChanged();
                adapterSpDay.notifyDataSetChanged();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private boolean isMemIdExist(final String mem_id){
        boolean isMemIdExist = false;
        if(Util.networkConnected(this)){
            String url = Util.URL+"MemServletApp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","isMemIdExist");
            jsonObject.addProperty("mem_id",mem_id);
            String jsonOut = jsonObject.toString();
            memExistTask = new CommonTask(url,jsonOut);
            try {
                String result = memExistTask.execute().get();
                isMemIdExist = Boolean.valueOf(result);
            } catch (Exception e){
                Log.e(TAG,e.toString());
            }
        }else{
            Util.showToast(this, R.string.msg_NoNetwork);
        }
        return isMemIdExist;
    }


}




//                spCitys.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//
//                    }
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {
//
//                    }
//                });