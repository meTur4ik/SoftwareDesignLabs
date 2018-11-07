package layout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus_user.labs.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Properties;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import functions.FunctionsStock;

import static functions.FunctionsStock.setWorkingDirectory;

public class UserProfileFragment extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 228;
    public static final String SERIALIZING_DIRECTORY = "/storage/emulated/0/.Labs";
    public static final String USER_SETTINGS_FILE = "user_settings.properties";
    public static final String USER_AVATAR_FILE = "avatar.jpeg";

    private View userProfileView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         userProfileView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        // Inflate the layout for this fragment
        setButtonAction();
        setWorkingDirectory();
        //String currentDir = System.getProperty("user.dir");
        //Toast.makeText(getActivity(), currentDir, Toast.LENGTH_LONG).show(); output "/"
        return userProfileView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (hasPermissions()) {
            deserializeUser();
            deserializeAvatar();
        } else {
            showNoReadExternalStoragePermissionSnackbar();
        }

    }

    private void setButtonAction(){
        Button load = userProfileView.findViewById(R.id.editUserProfileButton);
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavHostFragment) getActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment))
                        .getNavController().navigate(R.id.editUserProfile);
            }
        });
    }

    private void deserializeUser() {
        Properties props = FunctionsStock.deserializeUser(getActivity());

        TextView lastNameTV = userProfileView.findViewById(R.id.lastNameTextView);
        TextView firstNameTV = userProfileView.findViewById(R.id.firstNameTextView);
        TextView phoneTV = userProfileView.findViewById(R.id.phoneTextView);
        TextView emailTV = userProfileView.findViewById(R.id.emailTextView);
        lastNameTV.setText(props.getProperty("last_name"));
        firstNameTV.setText(props.getProperty("first_name"));
        phoneTV.setText(props.getProperty("phone"));
        emailTV.setText(props.getProperty("email"));
    }

    private void deserializeAvatar() {
        ImageView avatarView = userProfileView.findViewById(R.id.avatarImageView);
        new FunctionsStock.ImageDeserialize(avatarView).execute();
    }

    private boolean hasPermissions(){
        int result = 0;
        String[] permissions = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE};

        for (String permission : permissions){
            result = getActivity().checkCallingOrSelfPermission(permission);

            if(!(result == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    private void showNoReadExternalStoragePermissionSnackbar(){
        Snackbar.make(getActivity().findViewById(R.id.drawer_layout),
                "read and write external storage permission isn't granted", Snackbar.LENGTH_LONG).setAction(
                "SETTINGS", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openApplicationSettings();
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Open permissions and grant the storage permission",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .show();
    }

    private void openApplicationSettings(){
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getActivity().getPackageName()));
        startActivityForResult(appSettingsIntent, PERMISSION_REQUEST_CODE);
    }
}
