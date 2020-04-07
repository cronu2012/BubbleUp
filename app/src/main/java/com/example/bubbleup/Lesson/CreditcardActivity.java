package com.example.bubbleup.Lesson;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.craftman.cardform.Card;
import com.craftman.cardform.CardForm;
import com.craftman.cardform.OnPayBtnClickListner;
import com.example.bubbleup.Main.Util;
import com.example.bubbleup.Member.Member;
import com.example.bubbleup.OrderList.LessonOrderlistActivity;
import com.example.bubbleup.R;
import com.example.bubbleup.Task.CommonTask;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CreditcardActivity extends AppCompatActivity {
    private static final String TAG = "CreditcardActivity";

    static Lesson lesson;
    static Member member;
    private CommonTask getMemTask;
    private CardForm cardForm;
    private TextView amount ,cardName,cardAmount;
    private Button pay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditcard);

        lesson = (Lesson)this.getIntent().getSerializableExtra("lesson");

        SharedPreferences preferences = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
        String mem_id = preferences.getString("mem_id","");
        String mem_psw = preferences.getString("mem_psw","");
        member= findMem(mem_id,mem_psw);

        cardForm = findViewById(R.id.card_form);
        amount =  (cardForm.getRootView().findViewById(R.id.payment_amount));
        cardName = (cardForm.getRootView().findViewById(R.id.card_name));
        pay = cardForm.getRootView().findViewById(R.id.btn_pay);
        cardAmount = cardForm.getRootView().findViewById(R.id.payment_amount_holder);

        amount.setText("NTD "+lesson.getCost()+".00");
        cardName.setText(member.getMem_name());
        pay.setText("PAYER NTD"+lesson.getCost()+".00");
        cardAmount.setText("");
        cardForm.setPayBtnClickListner(new OnPayBtnClickListner() {
            @Override
            public void onClick(Card card) {
                //Your code here!! use card.getXXX() for get any card property
                //for instance card.getName();

                SweetAlertDialog pDialog = new SweetAlertDialog(CreditcardActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("付款成功");
                pDialog.setCancelable(false);
                pDialog.show();
                final Intent intent = new Intent(CreditcardActivity.this, LessonOrderlistActivity.class);
                Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        finish();
                        startActivity(intent);
                    }
                };
                timer.schedule(timerTask,3500);
            }
        });
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
