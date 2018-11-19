package com.example.asus_user.labs;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

// user, async tasks -> futures, кнопка на тулбаре, label'ы
//glide images
//отписать про onnavigation

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {
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

        navController.addOnNavigatedListener(new NavController.OnNavigatedListener() {
            @Override
            public void onNavigated(@NonNull NavController controller, @NonNull NavDestination destination) {
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
                Fragment current = navHostFragment.getChildFragmentManager().getFragments().get(1);
                Log.i("changePage", current.getId() + " " + R.id.editUserProfile_Fragment);
                if (current.getId() == R.id.editUserProfile_Fragment)
                    Log.i("ololo", Integer.toString(current.getId()));
            }
        });

        Toolbar toolbar = findViewById(R.id.main_tool_bar);
        setSupportActionBar(toolbar);

        initializeNavigation();

        if(!hasPermissions())
            requestPermissions();

        uriNavigate();
    }

    @Override
    public void onResume(){
        super.onResume();
        System.gc();
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
                requestPermissions(new String[] {Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
            }
        }
        if(!allowed.get(Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Read and Write permission is needed to save user data",
                        Toast.LENGTH_LONG).show();
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
        if(!allowed.get(Manifest.permission.CAMERA)){
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                Toast.makeText(this, "Camera is needed for avatar photos",
                        Toast.LENGTH_LONG).show();
                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id) {
            case R.id.about_toolbar_button:
                navController.navigate(R.id.phoneState);
                return true;
            case R.id.menu_toolbar_button:
                DrawerLayout slider = findViewById(R.id.drawer_layout);
                slider.openDrawer(Gravity.RIGHT);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        Fragment current = navHostFragment.getChildFragmentManager().getFragments().get(0);
        Toast.makeText(this, current.getId() + " " + R.id.editUserProfile, Toast.LENGTH_LONG).show();
        if (current.getId() == R.id.editUserProfile)
            Toast.makeText(this, "ololo", Toast.LENGTH_LONG).show();
        return true; //выделять или нет
    }

    private boolean hasPermissions(){
        int result = 0;
        String[] permissions = new String[] {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
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
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
        };
        requestPermissions(permissions, PERMISSION_REQUEST_CODE);
    }

    private void initializeNavigation(){
        NavigationView sideNavView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(sideNavView, navController);
    }

    private void uriNavigate() {
        //adb shell am start -a android.intent.action.VIEW -d "http://www.labs.ru/page/2"
        Uri data = getIntent().getData();
        String text = data == null? null : data.getLastPathSegment();
        Pattern p = Pattern.compile("^/page/\\d*$");


        // Toast.makeText(this, text == null ? "no index" : data.getPath(), Toast.LENGTH_LONG).show();
        int pageNum = 0;
        try {
            Matcher m = p.matcher(data.getPath());
            if(m.matches())
                pageNum = Integer.parseInt(text);
        }
        catch (NumberFormatException|NullPointerException e) {
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
            case 4: {
                navController.navigate(R.id.ololoFragment);
                break;
            }
            default:
                break;
        }

    }
}
