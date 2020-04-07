package com.example.bubbleup.Diveshop;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bubbleup.Lesson.LessonActivity;
import com.example.bubbleup.Main.Util;
import com.example.bubbleup.R;
import com.example.bubbleup.Task.CommonTask;
import com.example.bubbleup.Task.ImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class DiveshoplistFragment extends Fragment {
    private static final String TAG = "DiveshoplistFragment";
    private RecyclerView rvDiveshop;
    private CommonTask getDiveshopTask;
    private ImageTask dspicImageTask;
    private Button btnLessonList, btnEquipList;



    public DiveshoplistFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_diveshoplist, container, false);

        rvDiveshop = view.findViewById(R.id.rvDiveshop);
        rvDiveshop.setHasFixedSize(true);

//      RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvDiveshop.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        if(Util.networkConnected(getActivity())){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","getAll");
            String jsonOut = jsonObject.toString();
            getDiveshopTask = new CommonTask(Util.URL+"DiveshopServletApp",jsonOut);
            List<Diveshop> diveshopList = null;
            try {
                String jsonIn = getDiveshopTask.execute().get();
                Type listType = new TypeToken<List<Diveshop>>(){}.getType();
                diveshopList = new Gson().fromJson(jsonIn, listType);
            }catch (Exception e){
                Log.e(TAG, e.toString());
            }
            if (diveshopList == null || diveshopList.isEmpty()) {
                Util.showToast(getActivity(), R.string.msg_ListNotFound);
            } else {
                rvDiveshop.setAdapter(new DiveshoplistAdapter(getActivity(), diveshopList));
            }

        }else{
            Util.showToast(getActivity(),R.string.msg_NoNetwork);
        }





        return view;
    }

    @Override
    public void onStart(){
        super.onStart();

    }




    private class DiveshoplistAdapter extends RecyclerView.Adapter<DiveshoplistAdapter.ViewHolder>{
        private List<Diveshop> diveshopList;
        private Context context;
        private LayoutInflater layoutInflater;
        private int imageSize;

        public DiveshoplistAdapter(Context context ,List<Diveshop> diveshopList) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
            this.diveshopList = diveshopList;
            /* 螢幕寬度除以4當作將圖的尺寸 */
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private ImageView ivDiveshopPic;
            private TextView tvDiveshopName,tvDiveshopAddress;

            public ViewHolder( View view) {
                super(view);
                ivDiveshopPic = view.findViewById(R.id.ivDiveshopPic);
                tvDiveshopName = view.findViewById(R.id.tvDiveshopName);
                tvDiveshopAddress = view.findViewById(R.id.tvDiveshopAddress);
            }
            @Override
            public void onClick(View v) {

            }
        }



        @Override
        public int getItemCount(){
            return  diveshopList.size();
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_diveshop, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder( ViewHolder holder, int position) {
            final Diveshop diveshop = diveshopList.get(position);
            String url = Util.URL+"DspicServletApp";
            String ds_no = diveshop.getDs_no();
            dspicImageTask = new ImageTask(url,ds_no,imageSize,holder.ivDiveshopPic);
            dspicImageTask.execute();

            holder.tvDiveshopName.setText(diveshop.getDs_name());
            holder.tvDiveshopAddress.setText(diveshop.getAddress());


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(),DiveshopdetailActivity.class);
                     intent.putExtra("diveshop",diveshop);
                     startActivity(intent);
                }
            });
        }


    }






    @Override
    public void onStop() {
        super.onStop();
        if (getDiveshopTask != null) {
            getDiveshopTask.cancel(true);
        }

        if (dspicImageTask != null) {
            dspicImageTask.cancel(true);
        }

//        if (categoryTask != null) {
//            categoryTask.cancel(true);
//        }
    }

}
