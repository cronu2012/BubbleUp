package com.example.bubbleup.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bubbleup.Main.Util;
import com.example.bubbleup.Member.Member;
import com.example.bubbleup.R;
import com.example.bubbleup.Task.CommonTask;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import static android.content.Context.MODE_PRIVATE;

public class GolocationFragment extends Fragment {
    private static final String TAG = "GolocationFragment";
    private Button btn1;
    private CommonTask getMemTask;

    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_golocation, container, false);


        btn1 = view.findViewById(R.id.btn1);




        SharedPreferences preferences = getActivity().getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
        String mem_id = preferences.getString("mem_id","");
        String mem_psw = preferences.getString("mem_psw","");
        final Member member= findMem(mem_id,mem_psw);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),BasicmapActivity.class);
                intent.putExtra("member",member);
                startActivity(intent);
            }
        });


        return view;
    }

    private Member findMem(String mem_id,String mem_psw){
        Member member = null;
        if (Util.networkConnected(getActivity())) {
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
                Util.showToast(getActivity(), R.string.msg_ListNotFound);
            }
        } else {
            Util.showToast(getActivity(), R.string.msg_NoNetwork);
        }
        return member;
    }
}
