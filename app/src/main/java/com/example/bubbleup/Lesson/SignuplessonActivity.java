package com.example.bubbleup.Lesson;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bubbleup.Diveshop.DiveshopMasterActivity;
import com.example.bubbleup.Main.Util;
import com.example.bubbleup.Member.Member;
import com.example.bubbleup.OrderList.LesOrder;
import com.example.bubbleup.R;
import com.example.bubbleup.Task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SignuplessonActivity extends AppCompatActivity  implements  DialogInterface.OnClickListener{
    private static final String TAG = "SignuplessonActivity";
    private Lesson lesson;
    private String mem_id,mem_psw;
    private Member member;
    private LesOrder lesOrder;
    private Button btnRe,btnConfirm;
    private TextView tvLessonDetail;
    private String signStartYear,signStartMon,signStartDay,signEndYear,signEndMon,signEndDay,
            lesStartYear,lesStartMon,lesStartDay,lesEndYear,lesEndMon,lesEndDay;
    private CommonTask getLesOrderTask,addOneLesorderTask,getMemTask,isMemOrderedTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signuplesson);
        btnRe = findViewById(R.id.btnRe);
        btnConfirm = findViewById(R.id.btnConfirm);
        tvLessonDetail = findViewById(R.id.tvLesDetail);

        lesson = (Lesson)this.getIntent().getSerializableExtra("lesson");

        SharedPreferences preferences = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
        mem_id = preferences.getString("mem_id","");
        mem_psw = preferences.getString("mem_psw","");

        member = findMem(mem_id,mem_psw);

        lesOrder = new LesOrder(member.getMem_no(),lesson.getLes_no(),lesson.getDs_no(),member.getMem_name(),lesson.getLes_name()
                                        ,lesson.getCost(),lesson.getCoach(),"已付款");

         signStartYear = lesson.getSignup_startdate().toString().substring(0,4);
         signStartMon = lesson.getSignup_startdate().toString().substring(5,7);
         signStartDay = lesson.getSignup_startdate().toString().substring(8);

         signEndYear = lesson.getSignup_enddate().toString().substring(0,4);
         signEndMon = lesson.getSignup_enddate().toString().substring(5,7);
         signEndDay = lesson.getSignup_enddate().toString().substring(8);

         lesStartYear = lesson.getLes_startdate().toString().substring(0,4);
         lesStartMon = lesson.getLes_startdate().toString().substring(5,7);
         lesStartDay = lesson.getLes_startdate().toString().substring(8);

         lesEndYear = lesson.getLes_enddate().toString().substring(0,4);
         lesEndMon = lesson.getLes_enddate().toString().substring(5,7);
         lesEndDay = lesson.getLes_enddate().toString().substring(8);

        tvLessonDetail.setText("開課潛店： "+lesson.getDs_name()+"\n"+
                               "課程名稱： "+lesson.getLes_name()+"\n"+
                               "課程介紹： "+lesson.getLes_info()+"\n"+
                               "課程教練： "+lesson.getCoach()+"\n"+
                               "報名開始日期："+signStartYear+"年"+signStartMon+"月"+signStartDay+"日"+"\n"+
                               "報名截止日期："+signEndYear+"年"+signEndMon+"月"+signEndDay+"日"+"\n"+
                               "課程開始日期："+lesStartYear+"年"+lesStartMon+"月"+lesStartDay+"日"+"\n"+
                               "課程結束日期："+lesEndYear+"年"+lesEndMon+"月"+lesEndDay+"日"+"\n"+
                               "課程天數："+lesson.getDays().toString()+"天"+"\n"+
                               "學員人數上限："+lesson.getLes_max().toString()+"人"+"\n"+
                               "課程費用： "+lesson.getCost().toString()+"新臺幣整");


        btnRe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });





    }

    public void onSignupConfirm(View view){

        AlertDialog.Builder  confirm = new AlertDialog.Builder(SignuplessonActivity.this);
        confirm.setTitle("確定要報名？")
                .setMessage("開課潛店： "+lesson.getDs_name()+"\n"+
                            "課程名稱： "+lesson.getLes_name()+"\n"+
                            "課程教練： "+lesson.getCoach()+"\n"+
                        "課程開始日期："+lesStartYear+"年"+lesStartMon+"月"+lesStartDay+"日"+"\n"+
                        "課程結束日期："+lesEndYear+"年"+lesEndMon+"月"+lesEndDay+"日"+"\n"+
                        "課程天數： "+lesson.getDays().toString()+"天"+"\n"+
                        "課程費用： "+lesson.getCost().toString()+"新臺幣整")
                .setIcon(R.drawable.ic_help_outline_black_48dp)
                .setCancelable(true)
                .setPositiveButton(R.string.btn_sure,this)
                .setNegativeButton(R.string.btn_cancel,this)
                .show();
    }


//&& !(isMemOrdered(lesson.getLes_no(),member.getMem_no()))

    @Override
    public  void onClick(DialogInterface dialog, int which){
        if(which==DialogInterface.BUTTON_POSITIVE){
            if(findLesorderByLesno(lesson.getLes_no())< 8){
                addOnelesorder(lesOrder,member.getMem_name(),lesson.getLes_no());
            }else {
                if(findLesorderByLesno(lesson.getLes_no())>7) {
                    new AlertDialog.Builder(this)
                            .setTitle("無法報名")
                            .setMessage("人數超過上限～")
                            .setIcon(R.drawable.ic_highlight_off_black_24dp)
                            .show();
                }else if(isMemOrdered(lesson.getLes_no(),member.getMem_no())){
                    new AlertDialog.Builder(this)
                            .setTitle("無法報名")
                            .setMessage(member.getMem_name()+"，你已經報名過囉～")
                            .setIcon(R.drawable.ic_highlight_off_black_24dp)
                            .show();
                }
            }

        }else if(which==DialogInterface.BUTTON_NEGATIVE){
            dialog.cancel();
        }
    }

    private void addOnelesorder(LesOrder lesOrder,String mem_name,String les_no){
        if(Util.networkConnected(this)) {
            String url = Util.URL + "LessonorderServletApp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "insert");
            jsonObject.addProperty("lessonorderVO", new Gson().toJson(lesOrder));
            String jsonOut = jsonObject.toString();
            addOneLesorderTask = new CommonTask(url, jsonOut);
            boolean isSuccess = false;
            try {
                String result = addOneLesorderTask.execute().get();
                isSuccess = Boolean.valueOf(result);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

            if(isSuccess){
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_signupOK)
                        .setMessage(mem_name+"!恭喜你成功報名-課程編號："+les_no)
                        .setIcon(R.drawable.ic_done_black_24dp)
                        .show();

                final Intent intent = new Intent(SignuplessonActivity.this,CreditcardActivity.class);
                intent.putExtra("lesson",lesson);
                Timer timer = new Timer();
                TimerTask timerTask= new TimerTask(){
                    @Override
                    public void run(){
                        finish();
                        startActivity(intent);
                    }
                };
                timer.schedule(timerTask,1500);

            }else{
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_signupFail)
                        .setMessage(R.string.msg_signupFail)
                        .setIcon(R.drawable.ic_highlight_off_black_24dp)
                        .show();
            }
        }else{
            Util.showToast(this,R.string.msg_NoNetwork);
        }



    }


    private boolean isMemOrdered(String les_no,String mem_no){
        boolean isMemOrder = false;
        if(Util.networkConnected(this)) {
            String url = Util.URL + "LessonorderServletApp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "isMemOrdered");
            jsonObject.addProperty("les_no", les_no);
            jsonObject.addProperty("mem_no", mem_no);
            String jsonOut = jsonObject.toString();
            isMemOrderedTask = new CommonTask(url, jsonOut);
            try {
                String result = isMemOrderedTask.execute().get();
                isMemOrder = Boolean.valueOf(result);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }else{
            Util.showToast(this,R.string.msg_NoNetwork);
        }
        return  isMemOrder;
    }


    private int findLesorderByLesno(String les_no){
        List<LesOrder> list = new ArrayList<>();
        if(Util.networkConnected(this)){
            String url = Util.URL+"LessonorderServletApp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","findByLes_no");
            jsonObject.addProperty("les_no",les_no);
            String jsonOut = jsonObject.toString();
            getLesOrderTask = new CommonTask(url,jsonOut);
            try {
                String jsonIn = getLesOrderTask.execute().get();
                Type type = new TypeToken<List<LesOrder>>(){}.getType();
                list = new Gson().fromJson(jsonIn,type);
            } catch(Exception e){
                Log.e(TAG,e.toString());
            }
        }else{
            Util.showToast(this,R.string.msg_NoNetwork);
        }
        return list.size();
    }

    private Member findMem(String mem_id, String mem_psw){
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
