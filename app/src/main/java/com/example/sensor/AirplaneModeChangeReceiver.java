package com.example.sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

public class AirplaneModeChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(isAirplaneModeOn(context))
            Toast.makeText(context, "AirPlane mode is on",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "AirPlane mode is off",Toast.LENGTH_LONG).show();
    }
    private static boolean isAirplaneModeOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }
}
