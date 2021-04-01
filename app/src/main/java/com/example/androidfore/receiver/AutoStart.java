package com.example.androidfore.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.example.androidfore.foregroundServices.ForeService;

public class AutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent arg1) {
        Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, ForeService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
}