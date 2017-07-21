package com.example.application.deltafinaltask;

import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    GameView gameView;
    LinearLayout LL;
    TextView play;

    Sensor accelerometer;
    SensorManager sense;
    SensorEventListener sensorEventListener;
    float yAccel;
    int iAccel;


    @Override
    protected void onStart() {

        sense.registerListener(sensorEventListener,accelerometer,SensorManager.SENSOR_DELAY_GAME);

        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Animation animation;
        animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.blink);

        overridePendingTransition(0,0);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);




        LL = (LinearLayout)findViewById(R.id.LL);
        gameView = new GameView(this,size.x,size.y);
        gameView.setClickable(true);
        LL.addView(gameView);

        play = (TextView)findViewById(R.id.tv);
        play.startAnimation(animation);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.setP();
                play.clearAnimation();
                play.setVisibility(View.INVISIBLE);
            }
        });



        sense = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sense.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                    yAccel = event.values[1];
                    //Log.e("SAN","y value : " + yAccel);


                    if(yAccel > 1){
                        iAccel = 2;
                    }else if(yAccel < -1){
                        iAccel = 1;
                    }else{
                        iAccel = 0;
                    }
                    gameView.ySpeed(iAccel);
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };



    }



    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }


}
