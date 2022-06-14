package com.example.socket;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvTemperature, tvHumidity;
    private Thread thread;
    NetworkActivity na;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        na = new NetworkActivity();

        tvTemperature = findViewById(R.id.tv_temperature);
        tvHumidity = findViewById(R.id.tv_humidity);
        show();

    }

    public void onClickImage(View v) {

        switch (v.getId()) {
            case R.id.iv_temperature:
                Intent it_temp = new Intent(this, LineChartActivity_te.class);
                startActivity(it_temp);
                break;

            case R.id.iv_humidity:
                Intent it_hum = new Intent(this,LineChartActivity_hu.class);
                startActivity(it_hum);
                break;

            case R.id.iv_vibration:
                Intent it_vib = new Intent(this, LineChartActivity_vi.class);
                startActivity(it_vib);
                break;

            case R.id.iv_fire:
//                Intent it_fire = new Intent(this, FireActivity.class);
//                startActivity(it_fire);
                break;
        }
    }

    public void show(){
        if (thread != null)
            thread.interrupt();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                tvTemperature.setText(String.valueOf(na.getTe_line()));
                tvHumidity.setText(String.valueOf(na.getHu_line()));
            }
        };

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    runOnUiThread(runnable);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

}