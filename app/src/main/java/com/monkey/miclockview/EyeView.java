package com.monkey.miclockview;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by ivan on 2017/8/4.
 */

public class EyeView extends RelativeLayout {
    private View lefteye, righteye;
    private SensorManager sensorManager;
    private Sensor defaultSensor;
    private float normalSpace, x, y;
    public EyeView(Context context) {
        super(context);
    }

    public EyeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.eye_layout, null);
        lefteye = view.findViewById(R.id.lefteye);
        righteye = view.findViewById(R.id.righteye);
        normalSpace =20;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        defaultSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        addView(view);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        sensorManager.registerListener(listerner, defaultSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        sensorManager.unregisterListener(listerner);
    }

    private SensorEventListener listerner = new SensorEventListener(){

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                System.out.println(event.values[0]+"----"+event.values[1]);
                x -= 6.0f * event.values[0];
                y += 6.0f * event.values[1];
                //越界处理
                if (x < -normalSpace)
                {
                    x = -normalSpace;
                }
                if (x > 0)
                {
                    x = 0;
                }
                if (y > 0)
                {
                    y = 0;
                }
                if (y < -normalSpace)
                {
                    y = -normalSpace;
                }
                lefteye.setTranslationY(y);
                lefteye.setTranslationX(x);
                lefteye.setRotation(x);
                righteye.setTranslationX(x);
                righteye.setTranslationY(y);
                righteye.setRotation(x);

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
