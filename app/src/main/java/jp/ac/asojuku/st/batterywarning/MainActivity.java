package jp.ac.asojuku.st.batterywarning;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private MyBroadcastReceiver mReceiver;
    private int notificationId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume(){
        super.onResume();
        mReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mReceiver,filter);
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(mReceiver);
    }


    public class MyBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent){

            //複数のインテントを受信する場合はif文を使う
            if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)){
                int scale = intent.getIntExtra("scale",0);
                int level = intent.getIntExtra("level",0);
                int status = intent.getIntExtra("status",0);
                String statusString = "";
                if(level == 15){
                    statusString = "バッテリー残量が15％になりました";
                    notificationId++;
                }
                final Calendar calendar = Calendar.getInstance();
                final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                final int minute = calendar.get(Calendar.MINUTE);
                final int second = calendar.get(Calendar.SECOND);

                String title = "BatteryWarning";
                String msg = " " + hour + ":" + minute + ":" + second + " " + level + "/" + scale;

                Log.v(title,msg);

                Activity mainActivity = (Activity)context;
                TextView tvTitle = (TextView) mainActivity.findViewById(R.id.title);

                //タイトルの文章を設定
                tvTitle.setText(title);

                TextView tvMsg = (TextView) mainActivity.findViewById(R.id.message);
                //メッセージを設定
                tvMsg.setText(msg);

                NotificationManager myNotification = (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE);
                Intent bootIntent = new Intent(context,MainActivity.class);
                PendingIntent contentIntent = PendingIntent.getActivity(context,0,bootIntent,0);

                Notification.Builder builder = new Notification.Builder(context);
                builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle(title)
                        .setContentText(statusString)
                        .setWhen(System.currentTimeMillis())
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setContentIntent(contentIntent);

                myNotification.notify(notificationId,builder.build());

            }
        }
    }
}
