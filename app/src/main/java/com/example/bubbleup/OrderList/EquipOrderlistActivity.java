
package com.example.bubbleup.OrderList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bubbleup.Diveshop.Diveshop;
import com.example.bubbleup.Main.Util;
import com.example.bubbleup.R;
import com.example.bubbleup.Task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class EquipOrderlistActivity extends AppCompatActivity {
    private static final String TAG = "EquipOrderlistActivity";
    private RecyclerView  rvEqOrder;
    private CommonTask getEqOrderTask,getMemTask,getDsNameTask;


    public EquipOrderlistActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equip_orderlist);

        rvEqOrder = findViewById(R.id.rvEqOrder);


        rvEqOrder.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        SharedPreferences preferences = getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
        String mem_id = preferences.getString("mem_id", "");

        String mem_no = findPkById(mem_id);
        if(Util.networkConnected(this)){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","getAllRoByAMem");
            jsonObject.addProperty("mem_no",  mem_no  );
            String jsonOut = jsonObject.toString();
            getEqOrderTask = new CommonTask(Util.URL+"ROrderServletApp",jsonOut);
            List<ROrder> rorderList = null;
            try {
                String jsonIn = getEqOrderTask.execute().get();
                Type listType = new TypeToken<List<ROrder>>(){}.getType();
                rorderList = new GsonBuilder().setDateFormat("yyyy-MM-dd").create().fromJson(jsonIn,listType);
            } catch (Exception e){
                Log.e(TAG, e.toString());
            }
            if(rorderList==null||rorderList.isEmpty()){
                Util.showToast(this, R.string.msg_ListNotFound);
            }else {
                rvEqOrder.setAdapter(new EquipOredrlistAdapter(this,rorderList));
            }
        } else{
            Util.showToast(this,R.string.msg_NoNetwork);
        }
    }
    @Override
    public void onStart(){
        super.onStart();

    }
    private class EquipOredrlistAdapter extends RecyclerView.Adapter<EquipOredrlistAdapter.ViewHolder>{
        private List<ROrder> rorderlist;
        private Context context;
        private LayoutInflater layoutInflater;
        private int imageSize;



        public EquipOredrlistAdapter(Context context,List<ROrder> rorderlist){
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
            this.rorderlist = rorderlist;
            imageSize = getResources().getDisplayMetrics().widthPixels/4;
        }

        class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
            private TextView tvRono, tvDsno, tvTotalCost, tvOrderDate;


            ViewHolder(View view){
                super(view);
                tvRono = view.findViewById(R.id.tvRono);
                tvDsno = view.findViewById(R.id.tvLesNo);
                tvTotalCost = view.findViewById(R.id.tvTotalcost);
                tvOrderDate = view.findViewById(R.id.tvOrderDate);
            }

            @Override
            public void onClick(View v) {

            }
        }
        @Override
        public int getItemCount(){
            return  rorderlist.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_rorder,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder,int position){
            final ROrder rorder = rorderlist.get(position);

            viewHolder.tvRono.setText("訂單編號: "+rorder.getRo_no());
            viewHolder.tvDsno.setText("潛店: "+findDsName(rorder.getDs_no()));
            viewHolder.tvTotalCost.setText("總金額:    "+rorder.getTpriz().toString()+"新臺幣整");
            String year = rorder.getRo_no().substring(1,5);
            String mon = rorder.getRo_no().substring(5,7);
            String day = rorder.getRo_no().substring(7,9);
            viewHolder.tvOrderDate.setText("建立訂單日期："+year+"年"+mon+"月"+day+"日");


            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EquipOrderlistActivity.this,EqorderDetailActivity.class);
                    intent.putExtra("rorder",rorder);
                    startActivity(intent);
                }
            });
        }
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

    public String findDsName(String ds_no){
        Diveshop diveshop = new Diveshop();
        if(Util.networkConnected(this)){
            String url = Util.URL+"DiveshopServletApp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","findByPrimaryKey");
            jsonObject.addProperty("ds_no",ds_no);
            String jsonOut = jsonObject.toString();
            getDsNameTask = new CommonTask(url,jsonOut);

            try {
                String jsonIn = getDsNameTask.execute().get();
                Type type = new TypeToken<Diveshop>(){}.getType();
                diveshop = new Gson().fromJson(jsonIn,type);

            }catch(Exception e){
                Log.e(TAG, e.toString());
            }
            if (diveshop == null) {
                Util.showToast(this, R.string.msg_ListNotFound);
            }

        }else {
            Util.showToast(this,R.string.msg_NoNetwork);
        }
        return diveshop.getDs_name();
    }

    public String findPkById(String mem_id) {
        String mem_no = null;
        if (Util.networkConnected(this)) {
            String url = Util.URL + "MemServletApp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findPkById");
            jsonObject.addProperty("mem_id", mem_id);
            String jsonOut = jsonObject.toString();
            getMemTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = getMemTask.execute().get();
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
