package com.example.androidfore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidfore.foregroundServices.ForeService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final int ONGOING_NOTIFICATION_ID = 1112;
    private TextView baa ;
    private boolean serviceStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        baa =(TextView)findViewById(R.id.serviceBool);
        
        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive (Context context, Intent intent) {
                if (intent.getAction().equals(ForeService.ACTION_STARTED)) {
                    Toast.makeText(MainActivity.this, "MRECEIVER", Toast.LENGTH_SHORT).show();
                    serviceStarted = true;
                }
            }
        };
        BroadcastReceiver mReceiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive (Context context, Intent intent) {
                if (intent.getAction().equals(ForeService.ACTION_STOP)) {
                    Toast.makeText(MainActivity.this, "MRECEIVER1", Toast.LENGTH_SHORT).show();
                    serviceStarted = false;
                }
            }
        };
        registerReceiver(mReceiver, new IntentFilter(ForeService.ACTION_STARTED));
        registerReceiver(mReceiver1, new IntentFilter(ForeService.ACTION_STOP));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW));
        }
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        if(!isMyServiceRunning(ForeService.class) || !serviceStarted){
            baa.setText(R.string.off);
       } else if(isMyServiceRunning(ForeService.class) || serviceStarted) baa.setText(R.string.on);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("ASDSAD", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("ASDSAD", msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                serviceStarted=true;
                return true;
            }
        }
        serviceStarted=false;
        return false;
    }
}