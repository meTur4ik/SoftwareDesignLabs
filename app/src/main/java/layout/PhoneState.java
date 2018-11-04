package layout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus_user.labs.BuildConfig;
import com.example.asus_user.labs.MainActivity;
import com.example.asus_user.labs.R;
import com.google.android.material.snackbar.Snackbar;


public class PhoneState extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 228;
    private View phoneStateView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        phoneStateView = inflater.inflate(R.layout.fragment_phone_state, container, false);
        return phoneStateView;
    }

    @Override
    public void onStart(){
        super.onStart();
        if (hasPermissions()) {
            showPhoneState();
        } else {
            showNoPhoneStatePermissionSnackbar();
        }
    }

    private void showPhoneState(){
        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String imei = "";
        TextView central = phoneStateView.findViewById(R.id.textView);//(TextView)getActivity().findViewById(R.id.textView);
        try {
            imei = tm.getDeviceId();

            central.setText(imei);
        }
        catch (SecurityException e) {
            central.setText(e.getMessage());
        }
        TextView upper = phoneStateView.findViewById(R.id.textView2);//getActivity().findViewById(R.id.textView2);
        String version = BuildConfig.VERSION_NAME;
        upper.setText(version);
    }

    private boolean hasPermissions(){
        int result = 0;
        String[] permissions = new String[] {Manifest.permission.READ_PHONE_STATE};

        for (String permission : permissions){
            result = getActivity().checkCallingOrSelfPermission(permission);

            if(!(result == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    private void openApplicationSettings(){
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getActivity().getPackageName()));
        startActivityForResult(appSettingsIntent, PERMISSION_REQUEST_CODE);
    }

    private void showNoPhoneStatePermissionSnackbar(){
        Snackbar.make(getActivity().findViewById(R.id.drawer_layout),
                "Phone State permission isn't granted", Snackbar.LENGTH_LONG).setAction(
                "SETTINGS", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openApplicationSettings();
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Open permissions and grant the Calls permission",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .show();
    }
}
