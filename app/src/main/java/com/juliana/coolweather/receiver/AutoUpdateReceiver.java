package com.juliana.coolweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.juliana.coolweather.service.AutoUpdateService;

/**
 * Created by JYUAN7 on 1/11/2019.
 */

public class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}
