package layout;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus_user.labs.BuildConfig;
import com.example.asus_user.labs.R;


public class PhoneState extends Fragment {
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
        if (hasPermissions())
            showPhoneState();
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
}
