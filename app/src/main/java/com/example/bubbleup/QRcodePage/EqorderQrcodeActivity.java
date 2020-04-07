package com.example.bubbleup.QRcodePage;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.bubbleup.OrderList.ROrder;
import com.example.bubbleup.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

public class EqorderQrcodeActivity extends AppCompatActivity {
    private static final String LOG_TAG = "EqorderQrcodeActivity";

    private ImageView ivCodeE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eqorder_qrcode);
        final ROrder rorder = (ROrder) this.getIntent().getSerializableExtra("rorder");
        ivCodeE = findViewById(R.id.ivCodeE);

        int smallerDimension = getDimension();
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(rorder.getRo_no(), null,
                Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(),
                smallerDimension);
            try {
                Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                ivCodeE.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

            


    }

    private int getDimension() {
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        // 取得螢幕尺寸
        Display display = manager.getDefaultDisplay();
        // API 13列為deprecated，但為了支援舊版手機仍採用
        int width = display.getWidth();
        int height = display.getHeight();

        // 產生的QR code圖形尺寸(正方形)為螢幕較短一邊的1/2長度
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension / 2;

        // API 13開始支援
//        Display display = manager.getDefaultDisplay();
//        Point point = new Point();
//        display.getSize(point);
//        int width = point.x;
//        int height = point.y;
//        int smallerDimension = width < height ? width : height;
//        smallerDimension = smallerDimension / 2;
        return smallerDimension;
    }

}
