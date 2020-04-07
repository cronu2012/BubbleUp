package com.example.bubbleup.OrderList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bubbleup.R;

public class OrderListFragment extends Fragment {
    private  static  final String TAG = "OrderListFragment";
    private Button btnRorder,btnLesorder;


    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_orderlist, container, false);

        btnRorder = view.findViewById(R.id.btnRorder);
        btnLesorder = view.findViewById(R.id.btnLesorder);

        btnRorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),EquipOrderlistActivity.class);
                startActivity(intent);
            }
        });

        btnLesorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),LessonOrderlistActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
