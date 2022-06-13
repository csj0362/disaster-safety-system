package com.example.embeded;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickImage(View v) {

        switch (v.getId()) {
            case R.id.iv_temperature:
                Intent it_temp = new Intent(this, TempActivity.class);
                startActivity(it_temp);
                break;

            case R.id.iv_humidity:
                Intent it_hum = new Intent(this, HumActivity.class);
                startActivity(it_hum);
                break;

            case R.id.iv_vibration:
                Intent it_vib = new Intent(this, VibActivity.class);
                startActivity(it_vib);
                break;

            case R.id.iv_fire:
                Intent it_fire = new Intent(this, FireActivity.class);
                startActivity(it_fire);
                break;
        }
    }

//    public void onClickTemp(View v) {
//        switch(v.getId()) {
//            case R.id.iv_temperature:
//                Intent it_temp = new Intent(Intent.ACTION_VIEW, Uri.parse("https://thingspeak.com/channels/1765206/charts/1?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&title=%EC%98%A8%EB%8F%84%ED%98%84%ED%99%A9&type=line"));
//                startActivity(it_temp);
//                //v.getSettings().setUseWideViewPort(true);
//                //v.getSettings().setLoadWithOverviewMode(true);
//                break;
//
//            case R.id.iv_humidity:
//                Intent it_hum = new Intent(Intent.ACTION_VIEW, Uri.parse(""));
//                startActivity(it_hum);
//
//            case R.id.iv_vibration:
//                Intent it_vib = new Intent(Intent.ACTION_VIEW, Uri.parse(""));
//                startActivity(it_vib);
//
//            case R.id.iv_fire:
//                Intent it_fire = new Intent(Intent.ACTION_VIEW, Uri.parse(""));
//                startActivity(it_fire);
//
//        }
//
//    }
}