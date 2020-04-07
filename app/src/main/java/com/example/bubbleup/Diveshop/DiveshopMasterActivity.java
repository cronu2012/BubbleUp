package com.example.bubbleup.Diveshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bubbleup.Lesson.Lesson;
import com.example.bubbleup.Main.MainActivity;
import com.example.bubbleup.Main.Util;
import com.example.bubbleup.Member.LoginActivity;
import com.example.bubbleup.OrderList.LesOrder;
import com.example.bubbleup.OrderList.ROrder;
import com.example.bubbleup.R;
import com.example.bubbleup.Task.CommonTask;
import com.example.bubbleup.Task.ImageTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Date;

import static com.example.bubbleup.Main.Util.PREF_FILE;

public class DiveshopMasterActivity extends AppCompatActivity {

    private static final String TAG = "DiveshopMasterActivity";
    private AlertDialog builder;
    private TextView tvDsMasterName,tvResult;
    private ImageView ivDiveshop;
    private Button btnScan,btnConfirm,btnClear,btnLogout;
    private CommonTask getDsTask,getROrderTask,getDiveshopTask,getLesOrderTask,getMemTask,getLesTask,getUpdateRorderTask;
    private ImageTask getImageTask;
    private String ds_no;
    private ROrder rOrder;
    private LesOrder lesOrder;
    private String message="??";

    private static final String PACKAGE = "com.google.zxing.client.android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diveshop_master);





        tvDsMasterName = findViewById(R.id.tvDsMasterName);
        ivDiveshop = findViewById(R.id.ivDiveshop);
        btnScan = findViewById(R.id.btnScan);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnClear = findViewById(R.id.btnClear);
        btnLogout = findViewById(R.id.btnLogout);
        tvResult = findViewById(R.id.tvResult);






        SharedPreferences preferences = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
        String dsaccount = preferences.getString("dsaccount", "");
        Diveshop diveshop = findById(dsaccount);
        ds_no = diveshop.getDs_no();
        tvDsMasterName.setText(diveshop.getDs_name());







        btnScan.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                try {
                    startActivityForResult(intent, 1);
                }
                // 如果沒有安裝Barcode Scanner，就跳出對話視窗請user安裝
                catch (ActivityNotFoundException ex) {
                    showDownloadDialog();
                }
            }
        });

        String url = Util.URL + "DspicServletApp";
        String ds_no = diveshop.getDs_no();
        int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        Bitmap bitmap = null;
        try {
            getImageTask = new ImageTask(url, ds_no, imageSize,ivDiveshop);
            bitmap = getImageTask.execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            ivDiveshop.setImageBitmap(bitmap);
        } else {
            ivDiveshop.setImageResource(R.drawable.sample1);
        }

        if(tvResult==null||tvResult.toString().equals("")) {
            btnConfirm.setVisibility(View.INVISIBLE);
        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(message.equals("??")){
                    AlertDialog.Builder  alert = new AlertDialog.Builder(DiveshopMasterActivity.this);
                    alert.setTitle("咦！？")
                            .setMessage("還沒有任何訂單資訊")
                            .setIcon(R.drawable.ic_help_outline_black_48dp)
                            .setCancelable(true)
                            .show();
                }

                if (message.substring(0,1).equals("L")){
                    AlertDialog.Builder  alert3 = new AlertDialog.Builder(DiveshopMasterActivity.this);
                    alert3.setTitle("成功報到！")
                            .setMessage("此課程已經完成報到囉～")
                            .setIcon(R.drawable.ic_done_black_24dp)
                            .setCancelable(true)
                            .show();
                    tvResult.setText("");
                    message = "??";
                    lesOrder = null;
                }else if((message.substring(0,1)).equals("O")&&rOrder.getO_state().equals("待取")){
                    String ro_no = rOrder.getRo_no();
                    String ds_no = rOrder.getDs_no();
                    String mem_no = rOrder.getMem_no();
                    Integer tepc = rOrder.getTepc();
                    Integer tpriz = rOrder.getTpriz();
                    String op_state = rOrder.getOp_state();
                    Date rs_state = rOrder.getRs_date();
                    Date rd_state = rOrder.getRd_date();
                    Date rr_state = rOrder.getRr_date();
                    Integer ffine = rOrder.getFfine();
                    String o_note = rOrder.getO_note();
                    String o_state ="未歸還";

                    ROrder rOrder2 = new ROrder(ro_no,ds_no,mem_no,tepc,tpriz,op_state,rs_state,rd_state,rr_state,ffine,o_state,o_note);
                    if(Util.networkConnected(DiveshopMasterActivity.this)){
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action","update");
                        jsonObject.addProperty("ROrderVO",new Gson().toJson(rOrder2));
                        String jsonOut = jsonObject.toString();
                        String url = Util.URL + "ROrderServletApp";
                        getUpdateRorderTask = new CommonTask(url, jsonOut);
                        try {
                            getUpdateRorderTask.execute().get();

                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                    AlertDialog.Builder  alert1 = new AlertDialog.Builder(DiveshopMasterActivity.this);
                    alert1.setTitle("成功領取！")
                            .setMessage("此裝備租賃訂單已經完成領取囉～")
                            .setIcon(R.drawable.ic_done_black_24dp)
                            .setCancelable(true)
                            .show();

                    tvResult.setText("");
                    message = "??";
                    rOrder = null;
                }


                if(message.substring(0,1).equals("O")&&!(rOrder.getO_state().equals("待取"))){
                    AlertDialog.Builder  alert2 = new AlertDialog.Builder(DiveshopMasterActivity.this);
                    alert2.setTitle("糟糕！")
                            .setMessage("此裝備租賃訂單已經領取過囉～")
                            .setIcon(R.drawable.ic_highlight_off_black_24dp)
                            .setCancelable(true)
                            .show();
                    tvResult.setText("");
                    message = "??";
                    rOrder = null;
                }



            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(message.equals("??")){
                    AlertDialog.Builder  alert2 = new AlertDialog.Builder(DiveshopMasterActivity.this);
                    alert2.setTitle("咦！？")
                            .setMessage("沒有東西要清除喔～")
                            .setIcon(R.drawable.ic_help_outline_black_48dp)
                            .setCancelable(true)
                            .show();
                }

                message = "??";
                tvResult.setText("");
                rOrder = null;
                lesOrder = null;
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertFragment alertFragment = new AlertFragment();
                FragmentManager fm = getSupportFragmentManager();
                alertFragment.show(fm, "alert");
            }
        });
    }
    public static class AlertFragment extends DialogFragment implements DialogInterface.OnClickListener{
        @Override
        public Dialog onCreateDialog(Bundle getInstanceState){
            androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity())
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
                    preferences.edit().putBoolean("Dslogin",false).putString("dsaccpunt","").putString("dspaw","").apply();

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





    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode,resultCode,intent);
        //判斷請求代碼是否相同，確認來源是否正確
        if (requestCode == 1) {
             message = "";
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                message =  contents ;
                if((message.substring(0,1)).equals("O")){
                    tvResult.setText("");
                    rOrder = findOneRorder(message);

                    if(!(rOrder.getDs_no().equals(ds_no))){
                        tvResult.setText("非本店租賃訂單，滾！");
                    }else {

                        String rr_date;
                        if (rOrder.getRr_date() == null) {
                            rr_date = "";
                        } else {
                            rr_date = rOrder.getRr_date().toString();
                        }
                        String ffine;
                        if (rOrder.getFfine() == null) {
                            ffine = "";
                        } else {
                            ffine = rOrder.getFfine().toString();
                        }
                        String onote;
                        if (rOrder.getO_note() == null) {
                            onote = "";
                        } else {
                            onote = rOrder.getO_note();
                        }

                        tvResult.setText("裝備租賃訂單" + "\n" +
                                "訂單編號：  " + rOrder.getRo_no() + "\n" +
                                "潛店編號：  " + rOrder.getDs_no() + "\n" +
                                "潛店名稱：  " + findOneDiveshop(rOrder.getDs_no()).getDs_name() + "\n" +
                                "會員編號：  " + rOrder.getMem_no() + "\n" +
                                "會員姓名：  " + findNameByPk(rOrder.getMem_no()) + "\n" +
                                "裝備數量：  " + rOrder.getTepc().toString() + "\n" +
                                "  總金額：  " + rOrder.getTpriz().toString() + "\n" +
                                "付款狀態：  " + rOrder.getOp_state() + "\n" +
                                "租賃開始日期：" + rOrder.getRs_date().toString() + "\n" +
                                "應歸還日期： " + rOrder.getRd_date().toString() + "\n" +
                                "實際歸還日期：" + rr_date + "\n" +
                                "滯納金    ：" + ffine + "\n" +
                                "訂單狀態：  " + rOrder.getO_state() + "\n" +
                                "訂單備註：  " + onote);



                    }
                }else if(message.substring(0,1).equals("L")){
                    tvResult.setText("");
                     lesOrder = findOneLesorder(message);
                    if(!(lesOrder.getDs_no().equals(ds_no))){
                        tvResult.setText("非本店課程訂單，滾！");
                    }else {
                        String year = lesOrder.getLes_o_no().substring(1,5);
                        String mon = lesOrder.getLes_o_no().substring(5,7);
                        String day = lesOrder.getLes_o_no().substring(7,9);

                        tvResult.setText("課程訂單" + "\n" +
                                "訂單編號：  " + lesOrder.getLes_o_no() + "\n" +
                                "潛店編號：  " + lesOrder.getDs_no() + "\n" +
                                "潛店名稱：  " + findOneDiveshop(lesOrder.getDs_no()).getDs_name() + "\n" +
                                "課程編號：  " + lesOrder.getLes_no()+"\n"+
                                "課程名稱：  " + lesOrder.getLes_name()+"\n"+
                                "會員編號：  " + lesOrder.getMem_no() + "\n" +
                                "會員姓名：  " + findNameByPk(lesOrder.getMem_no()) + "\n" +
                                "教練姓名：  " + lesOrder.getCoach()+"\n"+
                                "  總金額：  " + lesOrder.getCost().toString() + "\n" +
                                "付款狀態：  " + lesOrder.getLo_state() + "\n" +
                                "報名日期：  " + year+"年"+mon+"月"+day+"日"+"\n"+
                                "開課日期：  " + findOneLesson(lesOrder.getLes_no()).getLes_startdate().toString()+"\n"+
                                "課程結束日期："+findOneLesson(lesOrder.getLes_no()).getLes_enddate().toString());
                    }




                }

            } else if (resultCode == RESULT_CANCELED) {
                message = "Scan was Cancelled!";
            }




        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        // 從偏好設定檔中取得登入狀態來決定是否顯示「登出」
        SharedPreferences pref = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        boolean login = pref.getBoolean("Dslogin", false);
        if (!login) {
            Intent intent = new Intent(DiveshopMasterActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void showDownloadDialog() {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(this);
        downloadDialog.setTitle("No Barcode Scanner Found");
        downloadDialog.setMessage("Please download and install Barcode Scanner!");
        downloadDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Uri uri = Uri.parse("market://search?q=pname:" + PACKAGE);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            Log.e(ex.toString(),
                                    "Play Store is not installed; cannot install Barcode Scanner");
                        }
                    }
                });
        downloadDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        downloadDialog.show();
    }





    private Lesson findOneLesson(String les_no){
        Lesson lesson = new Lesson();
        if(Util.networkConnected(this)){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","findByPrimaryKey");
            jsonObject.addProperty("les_no",les_no);
            String jsonOut = jsonObject.toString();
            getLesTask = new CommonTask(Util.URL+"LessonServletApp",jsonOut);
            try {
                String jsonIn = getLesTask.execute().get();
                Type type = new TypeToken<Lesson>(){}.getType();
                lesson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create().fromJson(jsonIn,type);
            }catch(Exception e){
                 Log.e(TAG,e.toString());
            }

        }else {
            Util.showToast(this,R.string.msg_NoNetwork);
        }
        return lesson;
    }


    public Diveshop findById(String dsaccount){
        Diveshop diveshop = new Diveshop();
        if(Util.networkConnected(this)){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","findById");
            jsonObject.addProperty("dsaccount",dsaccount);
            String jsonOut = jsonObject.toString();
            getDsTask = new CommonTask(Util.URL+"DiveshopServletApp",jsonOut);
            try{
                String jsonIn = getDsTask.execute().get();
                Type type = new TypeToken<Diveshop>(){}.getType();
                diveshop = new Gson().fromJson(jsonIn,type);
            } catch(Exception e){
                Log.e(TAG, e.toString());
            }
            if(diveshop==null){
                Util.showToast(this, R.string.msg_ListNotFound);
            }
        }else {
            Util.showToast(this,R.string.msg_NoNetwork);
        }
        return  diveshop;
    }

    private Diveshop findOneDiveshop(String ds_no){
        Diveshop diveshop = new Diveshop();
        if(Util.networkConnected(this)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findByPrimaryKey");
            jsonObject.addProperty("ds_no", ds_no);
            String jsonOut = jsonObject.toString();
            getDiveshopTask = new CommonTask(Util.URL + "DiveshopServletApp", jsonOut);
            try{
                String jsonIn = getDiveshopTask.execute().get();
                Type type = new TypeToken<Diveshop>(){}.getType();
                diveshop =  new GsonBuilder().setDateFormat("yyyy-MM-dd").create().fromJson(jsonIn,type);
            } catch (Exception e){
                Log.e(TAG, e.toString());
            }

        }else{
            Util.showToast(this,R.string.msg_NoNetwork);
        }
        return diveshop;
    }


    private LesOrder findOneLesorder(String les_o_no){
        LesOrder lesOrder = new LesOrder();
        if(Util.networkConnected(this)){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","findByPrimaryKey");
            jsonObject.addProperty("les_o_no",les_o_no);
            String jsonOut = jsonObject.toString();
            getLesOrderTask = new CommonTask(Util.URL+"LessonorderServletApp",jsonOut);
            try {
                String jsonIn = getLesOrderTask.execute().get();
                Type type = new TypeToken<LesOrder>(){}.getType();
                lesOrder = new  Gson().fromJson(jsonIn,type);
            }catch(Exception e){
                Log.e(TAG, e.toString());
            }
        }else {
            Util.showToast(this,R.string.msg_NoNetwork);
        }
        return lesOrder;
    }


    private ROrder findOneRorder(String ro_no){
        ROrder order = new ROrder() ;
        if(Util.networkConnected(this)){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","findOneRo");
            jsonObject.addProperty("ro_no",ro_no);
            String jsonOut = jsonObject.toString();
            getROrderTask = new CommonTask(Util.URL+"ROrderServletApp",jsonOut);
            try{
                String jsonIn = getROrderTask.execute().get();
                Type type = new TypeToken<ROrder>(){}.getType();
                order =  new GsonBuilder().setDateFormat("yyyy-MM-dd").create().fromJson(jsonIn,type);
            } catch (Exception e){
                Log.e(TAG, e.toString());
            }
        }else{
            Util.showToast(this,R.string.msg_NoNetwork);
        }
        return  order;
    }

    private String findNameByPk(final String mem_no){
        String mem_name = null;
        if(Util.networkConnected(this)){
            String url = Util.URL+"MemServletApp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","findNameByPk");
            jsonObject.addProperty("mem_no",mem_no);
            String jsonOut = jsonObject.toString();
            getMemTask = new CommonTask(url,jsonOut);
            try {
                String jsonIn = getMemTask.execute().get();
                Type type = new TypeToken<String>(){}.getType();
                mem_name = new Gson().fromJson(jsonIn,type);
            }catch(Exception e){
                Log.e(TAG,e.toString());
            }
        }else{
            Util.showToast(this, R.string.msg_NoNetwork);
        }
        return mem_name;
    }



}
