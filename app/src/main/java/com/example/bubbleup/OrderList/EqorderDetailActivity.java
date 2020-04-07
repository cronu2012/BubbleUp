package com.example.bubbleup.OrderList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bubbleup.Main.Util;
import com.example.bubbleup.QRcodePage.EqorderQrcodeActivity;
import com.example.bubbleup.R;
import com.example.bubbleup.Task.CommonTask;
import com.example.bubbleup.Task.EquipPicTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EqorderDetailActivity extends AppCompatActivity {
    private  static final  String TAG = "EqorderDetailActivity" ;
    private  TextView tvRono,tvDsno,tvMemName,tvTepc,tvTpriz,tvOpstate,tvRsdate,tvRddate,tvOstate,tvOnote;
    private Button btnToQrcode;
    private RecyclerView rvOneEquipList;
    private CommonTask getMemTask, getRoDetailTask,getEquipTask;
    private EquipPicTask getEqpicTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eqorder_detail);

        final ROrder rorder = (ROrder)this.getIntent().getSerializableExtra("rorder");
        tvRono = findViewById(R.id.tvRono_data);
        tvDsno = findViewById(R.id.tvDsno_data);
        tvMemName = findViewById(R.id.tvMemname_data);
        tvTepc = findViewById(R.id.tvTepc_data);
        tvTpriz = findViewById(R.id.tvTpriz_data);
        tvOpstate = findViewById(R.id.tvOpstate_data);
        tvRsdate = findViewById(R.id.tvRsdate_data);
        tvRddate = findViewById(R.id.tvRddate_data);
        tvOstate = findViewById(R.id.tvOstate_data);
        tvOnote = findViewById(R.id.tvOnote_data);
        btnToQrcode = findViewById(R.id.btnToQrcode);
        rvOneEquipList = findViewById(R.id.rvEquipList);
        tvRono.setText(rorder.getRo_no());
        tvDsno.setText(rorder.getDs_no());

        findNameByPk(rorder.getMem_no());
        tvMemName.setText(findNameByPk(rorder.getMem_no()));

        tvTepc.setText(rorder.getTepc().toString());
        tvTpriz.setText(rorder.getTpriz().toString());
        tvOpstate.setText(rorder.getOp_state());
        tvRsdate.setText(String.valueOf(rorder.getRs_date()));
        tvRddate.setText(String.valueOf(rorder.getRd_date()));
        tvOstate.setText(rorder.getO_state());
        tvOnote.setText(rorder.getO_note());


        rvOneEquipList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        List<RoDetail> roDetailList = null;
        if(Util.networkConnected(this)){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","getSameRoRdAll");
            jsonObject.addProperty("ro_no",  rorder.getRo_no());
            String jsonOut = jsonObject.toString();
            getRoDetailTask = new CommonTask(Util.URL+"RoDetailServletApp",jsonOut);
            try {
                String jsonIn = getRoDetailTask.execute().get();
                Type listType = new TypeToken<List<RoDetail>>(){}.getType();
                roDetailList = new Gson().fromJson(jsonIn,listType);
            } catch (Exception e){
                Log.e(TAG, e.toString());
            }
        } else{
            Util.showToast(this,R.string.msg_NoNetwork);
        }

        List<String> epSeqList = new ArrayList<>();
        for(RoDetail roDetail:roDetailList){
            String epSeq = roDetail.getEp_seq();
            epSeqList.add(epSeq);
        }



        if(Util.networkConnected(this)){
            Equip equip = null;
            List<Equip> equipList= new ArrayList<>();

            for(String eqseq: epSeqList){
              JsonObject jsonObject = new JsonObject();
              jsonObject.addProperty("action","findByPrimaryKey");
              jsonObject.addProperty("ep_seq",eqseq);
              String jsonOut = jsonObject.toString();
              getEquipTask =new CommonTask(Util.URL+"EquipServletApp",jsonOut);
              try {
                  String jsonIn1 = getEquipTask.execute().get();
                  Type listType1 = new TypeToken<Equip>() {
                  }.getType();
                  equip = new Gson().fromJson(jsonIn1, listType1);

                  equipList.add(equip);
              }  catch (Exception e){
                Log.e(TAG, e.toString());
              }
            }
            if(equipList==null||equipList.isEmpty()){
                Util.showToast(this, R.string.msg_ListNotFound);
            }else {
                rvOneEquipList.setAdapter(new EquipListAdapter(this,equipList));
            }
        } else{
            Util.showToast(this,R.string.msg_NoNetwork);
        }



        btnToQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EqorderDetailActivity.this, EqorderQrcodeActivity.class);
                intent.putExtra("rorder",rorder);
                startActivity(intent);
            }
        });


    }



    private class EquipListAdapter extends RecyclerView.Adapter<EquipListAdapter.ViewHolder>{
        private List<Equip> equipList;
        private Context context;
        private LayoutInflater layoutInflater;
        private int imageSize;

        public EquipListAdapter(Context context, List<Equip> equipList){
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
            this.equipList = equipList;
            imageSize = getResources().getDisplayMetrics().widthPixels/4;
        }

        class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
            private TextView tvDsName, tvEqName, tvSize, tvEprp;
            private ImageView ivEqpic;
            ViewHolder(View view){
                super(view);
                tvDsName = view.findViewById(R.id.tvDsName);
                tvEqName = view.findViewById(R.id.tvEqName);
                tvSize   = view.findViewById(R.id.tvSize);
                tvEprp   = view.findViewById(R.id.tvEprp);
                ivEqpic = view.findViewById(R.id.ivEqpic);
            }

            @Override
            public void onClick(View v) {

            }
        }

        @Override
        public int getItemCount(){
            return  equipList.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_one_equip,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position){
            final Equip equip = equipList.get(position);

            String url = Util.URL+"EqpicServletApp";
            String ds_no = equip.getDs_no();
            String ep_seq = equip.getEp_seq();
            getEqpicTask = new EquipPicTask(url,ds_no,ep_seq,imageSize,viewHolder.ivEqpic);
            getEqpicTask.execute();

            viewHolder.tvDsName.setText("潛店:  "+equip.getDs_name());
            viewHolder.tvEqName.setText("裝備:  "+equip.getEp_name());
            viewHolder.tvSize.setText("尺寸:  "+equip.getEp_size());
            viewHolder.tvEprp.setText("租金:  "+String.valueOf(equip.getEp_rp())+" 新臺幣整");


            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
}
