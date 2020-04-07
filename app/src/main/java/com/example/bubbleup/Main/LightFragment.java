package com.example.bubbleup.Main;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

import com.example.bubbleup.R;

import static android.content.Context.SENSOR_SERVICE;

public class LightFragment extends Fragment {
    private  static final String TAG = "LightFragment";
    private SensorManager sm;
    private TextView tvMessage;
    private ImageView ivLight;

    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_light, container, false);

        sm = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        tvMessage = view.findViewById(R.id.tvInfo);
        ivLight = view.findViewById(R.id.ivLight);

        return view;
    }
    SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Sensor sensor = event.sensor;
            float[] values = event.values;
//            StringBuilder info = new StringBuilder();
//            info.append("sensor name: " + sensor.getName() + "\n");
//            info.append("sensor type: " + sensor.getType() + "\n");
//            info.append("used power: " + sensor.getPower() + " mA\n");
//            info.append(getString(R.string.maxRange) +
//                    sensor.getMaximumRange() + "\n");
//            info.append("values[0] = " + values[0] + "\n");


            if (values[0] >= 400) {
                tvMessage.setText("適合潛水的大晴天！");
                ivLight.setImageResource(R.drawable.sunny);

            }
            else if (values[0] >= 100) {
                tvMessage.setText("太陽公公不見了....");
                ivLight.setImageResource(R.drawable.cloudy);
            } else {
                tvMessage.setText("天這麼黑，適合在家寫JAVA");
                ivLight.setImageResource(R.drawable.night);
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        boolean enable = sm.registerListener(listener,
                sm.getDefaultSensor(Sensor.TYPE_LIGHT),
                SensorManager.SENSOR_DELAY_UI);
        if (!enable) {
            sm.unregisterListener(listener);
            Log.e(TAG, getString(R.string.msg_notSupport));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sm.unregisterListener(listener);
    }
}
