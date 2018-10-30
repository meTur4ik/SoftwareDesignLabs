package com.example.asus_user.labs;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.renderscript.ScriptGroup;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.*;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 228;
    private DrawerLayout drawerLayout;
    //private NavController controller = new NavController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*if(!hasPermissions()){
            requestPermissions();
        } else {
            showPhoneState();
        }*/
        //ActivityM = DataBindingUtil.setContentView(this, R.layout.activity_main);
        //drawerLayout = Binding.
        //NavController navController = Navigation.findNavController(this, R.layout.)
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        Map<String, Boolean> allowed = new HashMap<String, Boolean>();
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                for (int i = 0, len = grantResults.length; i < len; ++i) {
                    allowed.put(permissions[i] ,(grantResults[i] == PackageManager.PERMISSION_GRANTED));
                }
                break;
            }
        }
        if (allowed.get(Manifest.permission.READ_PHONE_STATE)){
            showPhoneState();
        }
        else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)){
                requestPermissions();
                Toast.makeText(this, "Phone State permission is needed to show IMEI",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                showNoPhoneStatePermissionSnackbar();
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
        requestPermissions(permissions, PERMISSION_REQUEST_CODE);
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
        String version = BuildConfig.VERSION_NAME;
        upper.setText(version);
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
}
