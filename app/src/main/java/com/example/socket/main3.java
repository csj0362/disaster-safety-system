package com.example.socket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class main3 extends AppCompatActivity {
    int sended_num = 0;
    boolean selected = false;
    Button connect_btn;
    Button plus_btn, minus_btn;
    EditText ip_edit;
    TextView show_text;

    // Channel에 대한 id 생성
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    // Channel을 생성 및 전달해 줄 수 있는 Manager 생성
    private NotificationManager mNotificationManager;

    // Notification에 대한 ID 생성
    private static final int NOTIFICATION_ID = 0;

    // about socket
    private Handler mHandler;

    private Socket socket;

    private DataOutputStream outstream;
    private DataInputStream instream;

    private int port = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main3);

        ip_edit = (EditText) findViewById(R.id.editText);
        show_text = (TextView) findViewById(R.id.textView);
        connect_btn = (Button) findViewById(R.id.button);
        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });

        createNotificationChannel();

    }

    void connect() {
        mHandler = new Handler(Looper.getMainLooper());
        Log.w("connect", "연결 하는중");
        selected = true;
        Thread checkUpdate = new Thread() {
            public void run() {
                // Get ip
                String newip = String.valueOf(ip_edit.getText());

                Log.d("서버 접속 현황", newip + " " + port);

                // Access server
                try {
                    socket = new Socket(newip, port);
                    Log.w("서버 접속됨", "서버 접속됨");
                } catch (IOException e1) {
                    Log.w("서버 접속 못함", "서버 접속 못함");
                    e1.printStackTrace();
                }

                Log.w("edit 넘어가야 할 값 : ", "안드로이드에서 서버로 연결 요청");

                try {
                    outstream = new DataOutputStream(socket.getOutputStream());
                    instream = new DataInputStream(socket.getInputStream());
                    outstream.writeUTF("안드로이드에서 서버로 연결 요청");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.w("버퍼", "버퍼 생성 잘못 됨");
                }
                Log.w("버퍼", "버퍼 생성 잘 됨");

                try {
                    while(true){
                        byte[] buf = new byte[100];
                        int readB = instream.read(buf);
                        String msg = new String(buf, 0, readB);

                        if(Integer.parseInt(msg) < 50){
                            sendNotification();
                        }
                        show_text.setText(msg);
//                        BufferedReader bufReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                        String message = bufReader.readLine();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        checkUpdate.start();
    }

    //채널을 만드는 메소드
    public void createNotificationChannel()
    {
        //notification manager 생성
        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // 기기(device)의 SDK 버전 확인 ( SDK 26 버전 이상인지 - VERSION_CODES.O = 26)
        if(android.os.Build.VERSION.SDK_INT
                >= android.os.Build.VERSION_CODES.O){
            //Channel 정의 생성자( construct 이용 )
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID
                    ,"Test Notification",mNotificationManager.IMPORTANCE_HIGH);
            //Channel에 대한 기본 설정
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            // Manager을 이용하여 Channel 생성
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

    }

    // Notification Builder를 만드는 메소드
    private NotificationCompat.Builder getNotificationBuilder() {

//        Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(("tel:010-1000-1000")));
//        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
//                NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("알림")
                .setContentText("화재가 발생하였습니다!!")
                .setSmallIcon(R.drawable.ic_launcher_foreground);
                //.setContentIntent(notificationPendingIntent)
                //.setAutoCancel(true);
        Log.d("aa","aa");
        return notifyBuilder;
    }

    // Notification을 보내는 메소드
    public void sendNotification(){
        // Builder 생성
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        // Manager를 통해 notification 디바이스로 전달
        mNotificationManager.notify(NOTIFICATION_ID,notifyBuilder.build());
    }
}