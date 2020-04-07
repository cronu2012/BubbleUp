package com.example.bubbleup.Member;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.bubbleup.Main.Util;
import com.example.bubbleup.R;
import com.example.bubbleup.Task.CommonTask;
import com.example.bubbleup.Task.MempicTask;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;



import java.lang.reflect.Type;


import static android.content.Context.MODE_PRIVATE;
import static com.example.bubbleup.Main.Util.PREF_FILE;

public class MemberdetailFragment extends Fragment {
    private static final String TAG = "MemberdetailFragment";
    private TextView memberInfo;
    private ImageView ivMemPic;
    private CommonTask getMemberInfoTask;
    private MempicTask getImageTask;



//    private  Button btnUpdate;
//    private static final int REQ_PICK_IMAGE = 1;
//    private Uri contentUri, croppedImageUri;
//    private static final int REQ_CROP_PICTURE = 2;

    public MemberdetailFragment(){
        super();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_memberdetail, container, false);

        ivMemPic =  view.findViewById(R.id.ivMemPic);
        memberInfo = view.findViewById(R.id.textView4);
//        btnUpdate = view.findViewById(R.id.btnUpdatepic);

        SharedPreferences preferences = getActivity().getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        String mem_id = preferences.getString("mem_id","");
        String mem_psw = preferences.getString("mem_psw","");


        Member member = null;
        if (Util.networkConnected(getActivity())){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","findOneByIdPsw");
            jsonObject.addProperty("mem_id",mem_id);
            jsonObject.addProperty("mem_psw",mem_psw);
            String jsonOut = jsonObject.toString();
            getMemberInfoTask = new CommonTask(Util.URL+"MemServletApp",jsonOut);
            member = new Member();
            try {
                String jsonIn = getMemberInfoTask.execute().get();
                Type type = new TypeToken<Member>(){}.getType();
                member = new GsonBuilder().setDateFormat("yyyy-MM-dd").create().fromJson(jsonIn,type);
            } catch(Exception e){
                Log.e(TAG,e.toString());
            }
        }else{
            Util.showToast(getActivity(),R.string.msg_NoNetwork);
        }
        String sex = "";
        if(member.getMem_sex().toString().equals("1")){
            sex="男";
        }else if(member.getMem_sex().toString().equals("2")){
            sex="女";
        }
        String year_bd = member.getMem_bd().toString().substring(0,4);
        String mon_bd = member.getMem_bd().toString().substring(5,7);
        String day_bd = member.getMem_bd().toString().substring(8);
        String regYear = member.getReg_time().toString().substring(0,4);
        String regMon = member.getReg_time().toString().substring(5,7);
        String regDay = member.getReg_time().toString().substring(8,10);
        String phone1 = member.getMem_phone().substring(0,4);
        String phone2 = member.getMem_phone().substring(4,7);
        String phone3 = member.getMem_phone().substring(7);


        memberInfo.setText("帳號：        "+member.getMem_id()+"\n"+
                           "電子信箱："+member.getMem_mail()+"\n"+
                           "會員姓名："+member.getMem_name()+"\n"+
                           "性別：        "+sex+"\n"+
                           "出生日期："+year_bd+"年"+mon_bd+"月"+day_bd+"日"+"\n"+
                           "電話：         "+phone1+"-"+phone2+"-"+phone3+"\n"+
                           "地址           "+member.getMem_add()+"\n"+
                           "註冊日期："+regYear+"年"+regMon+"月"+regDay+"日");

        String url = Util.URL + "MemServletApp";
        String mem_no = member.getMem_no();
        int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        Bitmap bitmap = null;
        try {
            getImageTask = new MempicTask(url, mem_no, imageSize,ivMemPic);
            bitmap = getImageTask.execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            ivMemPic.setImageBitmap(bitmap);
        } else {
            ivMemPic.setImageResource(R.drawable.mempic_default);
        }
        return view;
    }

}
