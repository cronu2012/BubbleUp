package com.example.bubbleup.OrderList;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bubbleup.Diveshop.Diveshop;
import com.example.bubbleup.Lesson.Lesson;
import com.example.bubbleup.Main.Util;
import com.example.bubbleup.QRcodePage.Contents;
import com.example.bubbleup.QRcodePage.QRCodeEncoder;
import com.example.bubbleup.R;
import com.example.bubbleup.Task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.lang.reflect.Type;

public class LesorderDetailActivity extends AppCompatActivity {
    private static final String TAG ="LesorderDetailActivity";
    private TextView tvLesOrno,tvLesNo,tvLesName,tvDsName ,tvMemName, tvCoach, tvCost, tvOstate, tvStLesDate;
    private ImageView ivCodeL;
    private CommonTask getDsTask, getLesTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesorder_detail);
        final LesOrder lesOrder = (LesOrder)this.getIntent().getSerializableExtra("lesOrder");

        tvLesOrno = findViewById(R.id.tvLesOrno_data);
        tvLesNo = findViewById(R.id.tvLesNo_data);
        tvLesName = findViewById(R.id.tvLesName_data);
        tvDsName = findViewById(R.id.tvDsNameLes_data);
        tvMemName = findViewById(R.id.tvMemName_data);
        tvCoach = findViewById(R.id.tvCoach_data);
        tvCost = findViewById(R.id.tvCost_data);
        tvOstate = findViewById(R.id.tvOState_data);
        tvStLesDate = findViewById(R.id.tvStLesDate_data);
        ivCodeL = findViewById(R.id.ivCodeL);

        Diveshop diveshop = findOneDiveshop(lesOrder.getDs_no());
        Lesson lesson = findOneLesson(lesOrder.getLes_no());

        tvLesOrno.setText(lesOrder.getLes_o_no());
        tvLesNo.setText(lesOrder.getLes_no());
        tvLesName.setText(lesOrder.getLes_name());
        tvDsName.setText(diveshop.getDs_name());
        tvMemName.setText(lesOrder.getMem_name());
        tvCoach.setText(lesOrder.getCoach());
        tvCost.setText(lesOrder.getCost().toString());
        tvOstate.setText(lesOrder.getLo_state());
        tvStLesDate.setText(String.valueOf(lesson.getLes_startdate()));


        int smallerDimension = getDimension();
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(lesOrder.getLes_o_no(), null,
                Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(),
                smallerDimension);
        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            ivCodeL.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }



    }

    private int getDimension() {
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        // 取得螢幕尺寸
        Display display = manager.getDefaultDisplay();
        // API 13列為deprecated，但為了支援舊版手機仍採用
        int width = display.getWidth();
        int height = display.getHeight();

        // 產生的QR code圖形尺寸(正方形)為螢幕較短一邊的1/2長度
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension / 2;


        return smallerDimension;
    }

    private Diveshop findOneDiveshop(String ds_no){
        Diveshop diveshop = new Diveshop();
        if (Util.networkConnected(this)) {
            String url = Util.URL + "DiveshopServletApp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findByPrimaryKey");
            jsonObject.addProperty("ds_no", ds_no);
            String jsonOut = jsonObject.toString();
            getDsTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = getDsTask.execute().get();
                Type type = new TypeToken<Diveshop>() {
                }.getType();
                diveshop = new Gson().fromJson(jsonIn,type);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (diveshop == null) {
                Util.showToast(this, R.string.msg_ListNotFound);
            }
        } else {
            Util.showToast(this, R.string.msg_NoNetwork);
        }
        return diveshop;

    }

    private Lesson findOneLesson(String les_no){
        Lesson lesson = new Lesson();
        if (Util.networkConnected(this)) {
            String url = Util.URL + "LessonServletApp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findByPrimaryKey");
            jsonObject.addProperty("les_no", les_no);
            String jsonOut = jsonObject.toString();
            getLesTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = getLesTask.execute().get();
                Type type = new TypeToken<Lesson>() {
                }.getType();
                lesson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create().fromJson(jsonIn,type);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (lesson == null) {
                Util.showToast(this, R.string.msg_ListNotFound);
            }
        } else {
            Util.showToast(this, R.string.msg_NoNetwork);
        }
        return lesson;
    }
}
