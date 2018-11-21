package layout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import functions.SerializingFunctions;
import instances.AppUser;

import static functions.SerializingFunctions.setWorkingDirectory;

public class UserProfileFragment extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 228;
    public static final String SERIALIZING_DIRECTORY = "/storage/emulated/0/.Labs";
    public static final String USER_SETTINGS_FILE = "user_settings.properties";
    public static final String USER_AVATAR_FILE = "avatar.jpeg";

    private AppUser user;
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
        Properties props = SerializingFunctions.deserializeUser();

        TextView lastNameTV = userProfileView.findViewById(R.id.lastNameTextView);
        TextView firstNameTV = userProfileView.findViewById(R.id.firstNameTextView);
        TextView phoneTV = userProfileView.findViewById(R.id.phoneTextView);
        TextView emailTV = userProfileView.findViewById(R.id.emailTextView);
        user = new AppUser(props);
        lastNameTV.setText(user.getLast_name());
        firstNameTV.setText(user.getFirst_name());
        phoneTV.setText(user.getPhone_number());
        emailTV.setText(user.getEmail());
    }

    private void deserializeAvatar() {
        ImageView avatarEditView = userProfileView.findViewById(R.id.avatarImageView);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Bitmap> loader = executor.submit(
                new SerializingFunctions.LoadImage(SERIALIZING_DIRECTORY + "/" + USER_AVATAR_FILE));
        try {
            avatarEditView.setImageBitmap(loader.get());
        } catch (ExecutionException |InterruptedException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
