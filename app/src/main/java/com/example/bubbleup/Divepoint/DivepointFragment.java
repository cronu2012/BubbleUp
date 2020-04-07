package com.example.bubbleup.Divepoint;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bubbleup.Diveshop.Diveshop;
import com.example.bubbleup.Diveshop.DiveshopdetailActivity;
import com.example.bubbleup.Diveshop.DiveshoplistFragment;
import com.example.bubbleup.Main.Util;
import com.example.bubbleup.R;
import com.example.bubbleup.Task.CommonTask;
import com.example.bubbleup.Task.DivepointPicTask;
import com.example.bubbleup.Task.ImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DivepointFragment extends Fragment {
    private static final String TAG = "DivepointFragment";
    private RecyclerView rvDivepoint;

    private CommonTask getDivepointTask;
    private DivepointPicTask getPicTask;

    private static final int MY_REQUEST_CODE = 1;

    public DivepointFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_divepoint, container, false);

        rvDivepoint = view.findViewById(R.id.rvDivepoint);
        rvDivepoint.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        List<Divepoint> divepointList = new ArrayList<>();

        if(Util.networkConnected(getActivity())){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","getAll");
            String jsonOut = jsonObject.toString();
            getDivepointTask = new CommonTask(Util.URL+"DivepointServletApp",jsonOut);

            try {
                String jsonIn = getDivepointTask.execute().get();
                Type listType = new TypeToken<List<Divepoint>>(){}.getType();
                divepointList = new Gson().fromJson(jsonIn, listType);
            }catch (Exception e){
                Log.e(TAG, e.toString());
            }
            if (divepointList == null || divepointList.isEmpty()) {
                Util.showToast(getActivity(), R.string.msg_ListNotFound);
            } else {
                rvDivepoint.setAdapter(new DivepointAdapter(getActivity(), divepointList));
            }

        }else{
            Util.showToast(getActivity(),R.string.msg_NoNetwork);
        }

        return view;
    }

    private class DivepointAdapter extends RecyclerView.Adapter<DivepointAdapter.ViewHolder>{
        private List<Divepoint> divepointList;
        private Context context;
        private LayoutInflater layoutInflater;
        private int imageSize;

        public DivepointAdapter(Context context ,List<Divepoint> divepointList) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
            this.divepointList = divepointList;
            /* 螢幕寬度除以4當作將圖的尺寸 */
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private ImageView ivDivepointPic;
            private TextView tvDivepointName;

            public ViewHolder( View view) {
                super(view);
                ivDivepointPic = view.findViewById(R.id.ivDivepoint);
                tvDivepointName = view.findViewById(R.id.tvDp1);

            }
            @Override
            public void onClick(View v) {

            }
        }
        @Override
        public int getItemCount(){
            return  divepointList.size();
        }


        @Override
        public DivepointAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_divepoint, parent, false);
            return new DivepointAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DivepointAdapter.ViewHolder holder, int position) {
            final Divepoint divepoint = divepointList.get(position);
            String url = Util.URL+"DivepointServletApp";
            String dp_no = divepoint.getDp_no();
            getPicTask = new DivepointPicTask(url,dp_no,imageSize,holder.ivDivepointPic);
            getPicTask.execute();

            holder.tvDivepointName.setText(divepoint.getDp_name());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   Intent intent = new Intent(getActivity(),DivepointdetailActivity.class);
                   intent.putExtra("divepoint", divepoint);
                   startActivity(intent);
                }
            });
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        askPermissions();
    }

    private void askPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        Set<String> permissionsRequest = new HashSet<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(getActivity(), permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(permission);
            }
        }

        if (!permissionsRequest.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(),
                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
                    MY_REQUEST_CODE);
        }
    }


}