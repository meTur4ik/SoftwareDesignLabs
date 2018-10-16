package com.example.asus_user.labs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 228;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!hasPermissions()){
            requestPermissionWithRationale();
        } else {
            showPhoneState();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        boolean allowed = true;
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                for (int res : grantResults) {
                    allowed = (res == PackageManager.PERMISSION_GRANTED);
                }
                break;
            }
            default: {
                allowed = false;
                break;
            }
        }
        if (allowed){
            showPhoneState();
        }
        else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)){
                    Toast.makeText(this, "Phone State permission deined",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    showNoPhoneStatePermissionSnackbar();
                }
            }
        }
    }

    private boolean hasPermissions(){
        int result = 0;
        String[] permissions = new String[] {Manifest.permission.READ_PHONE_STATE};

        for (String permission : permissions){
            result = checkCallingOrSelfPermission(permission);

            if(!(result == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    private void requestPermissions(){
        String[] permissions = new String[] {Manifest.permission.READ_PHONE_STATE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    private void showPhoneState(){
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imei = "";
        TextView central = (TextView)findViewById(R.id.textView);
        try {
            imei = tm.getDeviceId();

            central.setText(imei);
        }
        catch (SecurityException e) {
            central.setText(e.getMessage());
        }

        TextView upper = findViewById(R.id.textView2);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            upper.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            upper.setText(e.getMessage());
        }
    }

    private void showNoPhoneStatePermissionSnackbar(){
        Snackbar.make(MainActivity.this.findViewById(R.id.activity_view),
                "Phone State permission isn't granted", Snackbar.LENGTH_LONG).setAction(
                "SETTINGS", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openApplicationSettings();
                        Toast.makeText(getApplicationContext(),
                                "Open permissions and grant the Calls permission",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .show();
    }

    private void openApplicationSettings(){
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, PERMISSION_REQUEST_CODE);
    }
    public  void requestPermissionWithRationale(){
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)){
            final String message = "Calls permission is needed to show IMEI";
            Snackbar.make(MainActivity.this.findViewById(R.id.activity_view), message,
                    Snackbar.LENGTH_LONG)
                    .setAction("GRANT", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestPermissions();
                        }
                    })
                    .show();
        }
        else {
            requestPermissions();
        }
    }
}
