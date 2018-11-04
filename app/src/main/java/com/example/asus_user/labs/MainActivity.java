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
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.*;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 228;
    private NavController navController;
    //private NavController controller = new NavController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment host = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        navController = host.getNavController();

        initializeNavigation();

        if(!hasPermissions())
            requestPermissions();

        uriNavigate();
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
        if (!allowed.get(Manifest.permission.READ_PHONE_STATE)){
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)){
                Toast.makeText(this, "Phone State permission is needed to show IMEI",
                        Toast.LENGTH_LONG).show();
                requestPermissions();
            }
        }

        if (!allowed.get(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                !allowed.get(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)){
                Toast.makeText(this, "Read and Write permission is needed to save user data",
                        Toast.LENGTH_LONG).show();
                requestPermissions();
            }
        }
    }

    private boolean hasPermissions(){
        int result = 0;
        String[] permissions = new String[] {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };

        for (String permission : permissions){
            result = checkCallingOrSelfPermission(permission);

            if(!(result == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    private void requestPermissions(){
        String[] permissions = new String[] {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        requestPermissions(permissions, PERMISSION_REQUEST_CODE);
    }

    private void initializeNavigation(){
        NavigationView sideNavView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(sideNavView, navController);
    }

    private void uriNavigate() {
        Uri data = getIntent().getData();
        String text = data == null? null : data.getLastPathSegment();

        //Toast.makeText(this, text == null ? "no index" : text, Toast.LENGTH_LONG).show();
        int pageNum;
        try {
            pageNum = Integer.parseInt(text);
        }
        catch (NumberFormatException e) {
            pageNum = 0;
        }
        switch (pageNum) {
            case 1: {
                navController.navigate(R.id.homeFragment);
                break;
            }
            case 2: {
                navController.navigate(R.id.phoneState);
                break;
            }
            case 3: {
                navController.navigate(R.id.userProfile);
                break;
            }
            default:
                break;
        }

    }
}
