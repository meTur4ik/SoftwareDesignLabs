package com.example.asus_user.labs;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
            TextView central = (TextView)findViewById(R.id.textView);
            central.setText(imei);
        }
        catch (SecurityException e){
            throw e;
        }
//        TextView central = (TextView)findViewById(R.id.textView);
//        central.setText(imei);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;

            if(version.contains("-dev")) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            TextView upper = findViewById(R.id.textView2);
            upper.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            TextView upper = findViewById(R.id.textView2);
            upper.setText(e.getMessage());
        }
    }
}
