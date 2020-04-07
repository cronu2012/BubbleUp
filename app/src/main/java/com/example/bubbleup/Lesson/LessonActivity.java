package com.example.bubbleup.Lesson;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bubbleup.Diveshop.Diveshop;
import com.example.bubbleup.Main.MainActivity;
import com.example.bubbleup.Main.Util;
import com.example.bubbleup.Member.Member;
import com.example.bubbleup.R;
import com.example.bubbleup.Task.CommonTask;
import com.example.bubbleup.Task.ImageTask2;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.example.bubbleup.Main.Util.PREF_FILE;
import static java.lang.String.valueOf;

public class LessonActivity extends AppCompatActivity {
    private static final String TAG = "LessonActivity";
    private RecyclerView rvLesson;
    private CommonTask getLessonTask,getMemTask;
    private ImageTask2 lespicImageTask;
    private Diveshop diveshop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        diveshop = (Diveshop)this.getIntent().getSerializableExtra("diveshop");



        rvLesson = findViewById(R.id.rvLesson);
//        rvLesson.setHasFixedSize(true);
        rvLesson.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        PagerSnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(rvLesson);


        if(Util.networkConnected(this)){
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("action","findByShop");
            jsonObject.addProperty("ds_no",diveshop.getDs_no());
            String jsonOut = jsonObject.toString();
            getLessonTask = new CommonTask(Util.URL+"LessonServletApp",jsonOut);
            List<Lesson> lessonList = new ArrayList<>();
            try {
                String jsonIn = getLessonTask.execute().get();
//                Log.e(TAG,jsonIn);
                Type listType = new TypeToken<List<Lesson>>(){}.getType();
                lessonList = new GsonBuilder().setDateFormat("yyyy-MM-dd").create().fromJson(jsonIn, listType);
//                Log.e(TAG,lessonList.toString());
            }catch (Exception e){
                Log.e(TAG, e.toString());
            }
            if (lessonList == null || lessonList.isEmpty()) {
                Util.showToast(this, R.string.msg_ListNotFound);
            } else {
                Iterator<Lesson> it = lessonList.iterator();
                while (it.hasNext()){
                    Lesson lessons = it.next();
                       if( "報名截止".equals(lessons.getLes_state())||"下架".equals(lessons.getLess_state())){
                        it.remove();
                    }
                }
                rvLesson.setAdapter(new LessonlistAdapter(this, lessonList));
            }

        }else{
            Util.showToast(this,R.string.msg_NoNetwork);
        }

    }




    private class LessonlistAdapter extends RecyclerView.Adapter<LessonlistAdapter.ViewHolder>{
        private List<Lesson> lessonList;
        private Context context;
        private LayoutInflater layoutInflater;
        private int imageSize;

        LessonlistAdapter(Context context,List<Lesson> lessonList){
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
            this.lessonList = lessonList;
            imageSize = getResources().getDisplayMetrics().widthPixels/4;
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            private ImageView ivLespic;
            private TextView tvDsName, tvLesName, tvStartdate, tvCost,tvDays,tvSignupEnd;
            public ViewHolder(View view){
                super(view);
                ivLespic = view.findViewById(R.id.ivLespic);
                tvDsName = view.findViewById(R.id.tvDsName);
                tvLesName = view.findViewById(R.id.tvLesName);
                tvStartdate = view.findViewById(R.id.tvStartdate);
                tvCost = view.findViewById(R.id.tvCost);
                tvDays = view.findViewById(R.id.tvDays);
                tvSignupEnd = view.findViewById(R.id.tvSignupEnd);
            }

        }
        @Override
        public int getItemCount(){
            return lessonList.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,int type){
         View view = layoutInflater.inflate(R.layout.cardview_lesson,parent,false);
         return  new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder,int positon){
            final Lesson lesson = lessonList.get(positon);
            String url = Util.URL+"LespicServletApp";
            String les_no = lesson.getLes_no();
            lespicImageTask = new ImageTask2(url,les_no,imageSize,viewHolder.ivLespic);
            lespicImageTask.execute();

            viewHolder.tvDsName.setText(lesson.getDs_name());
            viewHolder.tvLesName.setText(lesson.getLes_name());
            viewHolder.tvDays.setText("天數："+lesson.getDays().toString()+"天");

            String year = String.valueOf(lesson.getSignup_enddate()).substring(0,4);
            String mon = String.valueOf(lesson.getSignup_enddate()).substring(5,7);
            String day = String.valueOf(lesson.getSignup_enddate()).substring(8);
            viewHolder.tvSignupEnd.setText("報名截止日： "+year+"年"+mon+"月"+day+"日");

            year = String.valueOf(lesson.getLes_startdate()).substring(0,4);
            mon = String.valueOf(lesson.getLes_startdate()).substring(5,7);
            day = String.valueOf(lesson.getLes_startdate()).substring(8);

            viewHolder.tvStartdate.setText("開課日期： "+year+"年"+mon+"月"+day+"日");

            viewHolder.tvCost.setText("費用:"+ lesson.getCost().toString()+" 新臺幣整");




            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LessonActivity.this,SignuplessonActivity.class);
                    intent.putExtra("lesson",lesson);
                    startActivity(intent);
                }
            });

        }

    }

    @Override
    public void onStop(){
        super.onStop();
        if(getLessonTask!=null){
            getLessonTask.cancel(true);
        }
        if(lespicImageTask!=null){
            lespicImageTask.cancel(true);
        }
    }





}
