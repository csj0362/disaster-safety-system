package com.example.socket;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkActivity extends AppCompatActivity {
    Button send_button;
    EditText send_editText;
    //    TextView send_textView;
//    TextView read_textView;
    private Socket client;
    private DataOutputStream dataOutput;
    private DataInputStream dataInput;
    private static String SERVER_IP = "192.168.64.75";
    private static String CONNECT_MSG = "connect";
    private static String STOP_MSG = "stop";

    public static int vi_line = 0, wa_line = 0, fl_line = 0, hu_line = 0, te_line = 0;

    private static int BUF_SIZE = 100;

    // Channel에 대한 id 생성
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    // Channel을 생성 및 전달해 줄 수 있는 Manager 생성
    private NotificationManager mNotificationManager;

    // Notification에 대한 ID 생성
    private static final int TE_NOTIFICATION_ID = 0;
    private static final int HU_NOTIFICATION_ID = 1;
    private static final int FL_NOTIFICATION_ID = 2;
    private static final int WA_NOTIFICATION_ID = 3;
    private static final int VI_NOTIFICATION_ID = 4;

    public static int getTe_line() {
        return te_line;
    }

    public static int getHu_line() {
        return hu_line;
    }

    public static int getVi_line() {
        return vi_line;
    }
    public static int getFl_line() {
        return fl_line;
    }
    public static int getWa_line() {
        return wa_line;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_server);
        send_button = findViewById(R.id.connect_btn);
        send_editText = findViewById(R.id.ip_edit);
        createNotificationChannel();

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Connect connect = new Connect();
                connect.execute(CONNECT_MSG);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private class Connect extends AsyncTask<String, String, Void> {
        private String output_message;
        private String input_message;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                client = new Socket(SERVER_IP, 9998);
                dataOutput = new DataOutputStream(client.getOutputStream());
                dataInput = new DataInputStream(client.getInputStream());
                output_message = strings[0];
                dataOutput.writeUTF(output_message);

            } catch (UnknownHostException e) {
                String str = e.getMessage().toString();
                Log.w("discnt", str + " 1");
            } catch (IOException e) {
                String str = e.getMessage().toString();
                Log.w("discnt", str + " 2");
            }

            while (true) {
                try {
                    //데이터 받은 데이터 저장하기 위한 byte 배열 생성
                    byte[] buf = new byte[BUF_SIZE];
                    //입력 스트림에 포함된 바이트수를 읽고 버퍼에 할당
                    int read_Byte = dataInput.read(buf);
                    //배열의 offset 인덱스 위치부터 length만큼 String 객체로 생성
                    input_message = new String(buf, 0, read_Byte);
                    Log.d("msg", input_message);

                    //input_message를 각각의 센서이름과 센서 데이터로 분할
                    String te_id = input_message.substring(0, 2);
                    String te_data = input_message.substring(2, 4);
                    String hu_id = input_message.substring(4, 6);
                    String hu_data = input_message.substring(6, 8);
                    String fl_id = input_message.substring(8, 10);
                    String fl_data = input_message.substring(10, 11);
                    String wa_id = input_message.substring(11, 13);
                    String wa_data = input_message.substring(13, 14);
                    String vi_id = input_message.substring(14, 16);
                    String vi_data = input_message.substring(16);

                    Log.d("te_id, te_data", te_id + " " + te_data);
                    Log.d("hu_id, hu_data", hu_id + " " + hu_data);
                    Log.d("fl_id, fl_data", fl_id + " " + fl_data);
                    Log.d("wa_id, wa_data", wa_id + " " + wa_data);
                    Log.d("vi_id, vi_data", vi_id + " " + vi_data);

                    hu_line = Integer.parseInt(hu_data);
                    te_line = Integer.parseInt(te_data);
                    vi_line = Integer.parseInt(vi_data);
                    fl_line = Integer.parseInt(fl_data);
                    wa_line = Integer.parseInt(wa_data);

                    // 해당 센서가 일상 수치 이상일 경우 알림 보냄
                    if (Integer.parseInt(te_data) >= 27) {
                        sendNotification_te();
                    }
                    if (Integer.parseInt(hu_data) >= 80) {
                        sendNotification_hu();
                    }
                    if (Integer.parseInt(fl_data) >= 0) {
                        sendNotification_fl();
                    }
                    if (Integer.parseInt(wa_data) >= 1) {
                        sendNotification_wa();
                    }
                    if (Integer.parseInt(vi_data) >= 30) {
                        sendNotification_vi();
                    }
                    System.out.println("-------------");

                    if (!input_message.equals(STOP_MSG)) {
                        publishProgress(input_message);
                    } else {
                        break;
                    }
                    Thread.sleep(2);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    //채널을 만드는 메소드
    public void createNotificationChannel() {
        //notification manager 생성
        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // 기기(device)의 SDK 버전 확인 ( SDK 26 버전 이상인지 - VERSION_CODES.O = 26)
        if (android.os.Build.VERSION.SDK_INT
                >= android.os.Build.VERSION_CODES.O) {
            //Channel 정의 생성자( construct 이용 )
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID
                    , "Test Notification", mNotificationManager.IMPORTANCE_HIGH);
            //Channel에 대한 기본 설정
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            // Manager을 이용하여 Channel 생성
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder_te() {
        Intent notificationIntent = new Intent(this, LineChartActivity_te.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_temperature)
                .setContentTitle("알림")
                .setContentText("고온주의")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);
        return notifyBuilder;
    }

    private NotificationCompat.Builder getNotificationBuilder_hu() {
        Intent notificationIntent = new Intent(this, LineChartActivity_te.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_humidity)
                .setContentTitle("알림")
                .setContentText("다습주의")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);
        return notifyBuilder;
    }

    private NotificationCompat.Builder getNotificationBuilder_fl() {
        Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(("tel:119")));
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                TE_NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_fire)
                .setContentTitle("알림")
                .setContentText("화염감지")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true);
        return notifyBuilder;
    }

    private NotificationCompat.Builder getNotificationBuilder_wa() {
        Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(("tel:119")));
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                TE_NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_flood)
                .setContentTitle("알림")
                .setContentText("침수발생")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true);
        return notifyBuilder;
    }

    private NotificationCompat.Builder getNotificationBuilder_vi() {
        Intent notificationIntent = new Intent(this, LineChartActivity_vi.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_vibration)
                .setContentTitle("알림")
                .setContentText("지진발생")
                .setSmallIcon(R.drawable.ic_flood)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);
        return notifyBuilder;
    }

    public void sendNotification_te() {
        // Builder 생성
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder_te();
        // Manager를 통해 notification 디바이스로 전달
        mNotificationManager.notify(TE_NOTIFICATION_ID, notifyBuilder.build());
    }

    public void sendNotification_hu() {
        // Builder 생성
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder_hu();
        // Manager를 통해 notification 디바이스로 전달
        mNotificationManager.notify(HU_NOTIFICATION_ID, notifyBuilder.build());
    }

    public void sendNotification_fl() {
        // Builder 생성
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder_fl();
        // Manager를 통해 notification 디바이스로 전달
        mNotificationManager.notify(FL_NOTIFICATION_ID, notifyBuilder.build());
    }

    public void sendNotification_wa() {
        // Builder 생성
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder_wa();
        // Manager를 통해 notification 디바이스로 전달
        mNotificationManager.notify(WA_NOTIFICATION_ID, notifyBuilder.build());
    }

    // Notification을 보내는 메소드
    public void sendNotification_vi() {
        // Builder 생성
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder_vi();
        // Manager를 통해 notification 디바이스로 전달
        mNotificationManager.notify(VI_NOTIFICATION_ID, notifyBuilder.build());
    }
}