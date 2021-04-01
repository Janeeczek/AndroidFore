package com.example.androidfore.foregroundServices;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.androidfore.MainActivity;
import com.example.androidfore.R;

public class ForeService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static final String ACTION_STARTED = ForeService.class.getName() + ".STARTED";
    public static final String ACTION_STOP = ForeService.class.getName() + ".STOP";
    public static final String ACTION_IS_STARTED = ForeService.class.getName() + ".ISSTARTED";
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive (Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_IS_STARTED)) {
                LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplicationContext());
                manager.sendBroadcast(new Intent(ACTION_STARTED));
            }
        }
    };

    public ForeService() {
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Usługa utworzona", Toast.LENGTH_SHORT).show();

        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Usługa się uruchamia", Toast.LENGTH_SHORT).show();
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Aktywna usługa Foreground")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        Intent intents = new Intent(getBaseContext(),MainActivity.class);
        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intents);
        Toast.makeText(this, "Usługa uruchomiona sukcesem", Toast.LENGTH_LONG).show();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplicationContext());
        manager.registerReceiver(mReceiver, new IntentFilter(PingableService.ACTION_PONG));
        // the service will respond to this broadcast only if it's running
        manager.sendBroadcast(new Intent(ACTION_PING));
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onDestroy() {
        Toast.makeText(this, "Usługa zakończyła działanie", Toast.LENGTH_SHORT).show();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}