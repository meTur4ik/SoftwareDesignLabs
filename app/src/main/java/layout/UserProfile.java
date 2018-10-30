package layout;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asus_user.labs.BuildConfig;
import com.example.asus_user.labs.MainActivity;
import com.example.asus_user.labs.R;

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.core.content.ContextCompat.getSystemService;

public class UserProfile extends Fragment {
    private View userProfileView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         userProfileView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        // Inflate the layout for this fragment
        return userProfileView;
    }
}
