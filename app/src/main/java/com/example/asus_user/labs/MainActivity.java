package com.example.asus_user.labs;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imei;
        try {
            imei = tm.getDeviceId();
        }
        catch (SecurityException e){
            throw e;
        }
        TextView central = (TextView)findViewById(R.id.textView);
        central.setText(imei);
    }
}
