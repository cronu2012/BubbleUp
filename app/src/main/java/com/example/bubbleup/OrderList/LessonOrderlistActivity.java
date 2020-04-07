package com.example.bubbleup.OrderList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bubbleup.Main.Util;
import com.example.bubbleup.R;
import com.example.bubbleup.Task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class LessonOrderlistActivity extends AppCompatActivity {
    private static final String TAG = "LessonOrderlistActivity";
    private RecyclerView rvLesOrder;
    private CommonTask getLesOrderTask;



    private EquipOrderlistActivity equipOrderlistActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_orderlist);

        rvLesOrder = findViewById(R.id.rvLesOrder);

        rvLesOrder.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        SharedPreferences preferences = getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
        String mem_id = preferences.getString("mem_id","");
        String mem_no =findPkById(mem_id);

        if(Util.networkConnected(this)){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","findByMem_no");
            jsonObject.addProperty("mem_no",mem_no);
            String jsonOut = jsonObject.toString();
            getLesOrderTask = new CommonTask(Util.URL+"LessonorderServletApp",jsonOut);
            List<LesOrder> lesOrderList = null;
            try{
                String jsonIn = getLesOrderTask.execute().get();
                Type type = new TypeToken<List<LesOrder>>(){}.getType();
                lesOrderList = new Gson().fromJson(jsonIn,type);
            }catch (Exception e){
                Log.e(TAG,e.toString());
            }

            if(lesOrderList==null||lesOrderList.isEmpty()){
                Util.showToast(this,R.string.msg_ListNotFound);
            }else{
                rvLesOrder.setAdapter(new LessonOrderlistAdapter(this,lesOrderList));
            }
        }else {
            Util.showToast(this,R.string.msg_NoNetwork);
        }


    }
    @Override
    public void onStart(){
        super.onStart();

    }

    private class LessonOrderlistAdapter extends RecyclerView.Adapter<LessonOrderlistAdapter.ViewHolder>{
        private List<LesOrder> lesOrderList;
        private Context context;
        private LayoutInflater layoutInflater;

        public LessonOrderlistAdapter(Context context,List<LesOrder> lesOrderList){
            this.context = context;
            this.lesOrderList= lesOrderList;
            layoutInflater = LayoutInflater.from(context);
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            private TextView tvLesOrderNo, tvLesName, tvTotalCost, tvOrderDate;

            ViewHolder(View view){
                super(view);
                tvLesOrderNo = view.findViewById(R.id.tvLesOrderNo);
                tvLesName = view.findViewById(R.id.tvLesName);
                tvTotalCost = view.findViewById(R.id.tvTotalCost);
                tvOrderDate = view.findViewById(R.id.tvOrderDate);
            }
            @Override
            public void onClick(View v) {

            }
        }

        @Override
        public int getItemCount(){
            return lesOrderList.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int type){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_lesorder,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder,int potion){
            final LesOrder lesOrder = lesOrderList.get(potion);

            viewHolder.tvLesOrderNo.setText("訂單編號: "+lesOrder.getLes_o_no());
            viewHolder.tvLesName.setText("課程名稱: "+lesOrder.getLes_name());
            viewHolder.tvTotalCost.setText("總金額:    "+lesOrder.getCost().toString()+"新臺幣整");
            String year = lesOrder.getLes_o_no().substring(1,5);
            String mon = lesOrder.getLes_o_no().substring(5,7);
            String day = lesOrder.getLes_o_no().substring(7,9);
            viewHolder.tvOrderDate.setText("建立訂單日期："+year+"年"+mon+"月"+day+"日");

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LessonOrderlistActivity.this,LesorderDetailActivity.class);
                    intent.putExtra("lesOrder",lesOrder);
                    startActivity(intent);
                }
            });
        }
    }

    public String findPkById(String mem_id) {
        String mem_no = null;
        if (Util.networkConnected(this)) {
            String url = Util.URL + "MemServletApp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findPkById");
            jsonObject.addProperty("mem_id", mem_id);
            String jsonOut = jsonObject.toString();
            getLesOrderTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = getLesOrderTask.execute().get();
                Type type = new TypeToken<String>() {
                }.getType();
                mem_no = new Gson().fromJson(jsonIn,type);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (mem_no == null) {
                Util.showToast(this, R.string.msg_ListNotFound);
            }
        } else {
            Util.showToast(this, R.string.msg_NoNetwork);
        }
        return mem_no;
    }

}
