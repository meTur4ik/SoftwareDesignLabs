package layout;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import functions.FunctionsStock;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.asus_user.labs.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import static android.app.Activity.RESULT_OK;


public class EditUserProfileFragment extends Fragment {
    private static final int REQUEST_LOAD_IMAGE = 229;
    private static final int PERMISSION_REQUEST_CODE = 228;
    private static final String SERIALIZING_DIRECTORY = UserProfileFragment.SERIALIZING_DIRECTORY;
    private static final String USER_SETTINGS_FILE = UserProfileFragment.USER_SETTINGS_FILE;
    private static final String USER_AVATAR_FILE = UserProfileFragment.USER_AVATAR_FILE;

    private View editUserProfileView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        editUserProfileView = inflater.inflate(R.layout.fragment_edit_user_profile, container, false);
        new FunctionsStock.ImageDeserialize((ImageView) editUserProfileView.findViewById(R.id.avatarEditImageView)).execute();
        setLoadButtonAction();
        setDoneButtonAction();
        if (hasPermissions()) {
            deserializeUser();
        } else {
            showNoReadExternalStoragePermissionSnackbar();
        }

        return editUserProfileView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOAD_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            ImageView imgView = new ImageView(getActivity());
            imgView.setImageURI(imageUri);
            new FunctionsStock.ImageSerialize(imgView).execute(getActivity());
            ImageView avatar = editUserProfileView.findViewById(R.id.avatarEditImageView);
            avatar.setImageURI(imageUri);
        }
    }

    private void setLoadButtonAction(){
        Button load = editUserProfileView.findViewById(R.id.loadPhotoButton);
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, REQUEST_LOAD_IMAGE);
            }
        });
    }

    private void setDoneButtonAction() {
        Button done = editUserProfileView.findViewById(R.id.doneButton);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serializeUser();
                ((NavHostFragment) getActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment))
                        .getNavController().navigate(R.id.userProfile);
            }
        });
    }

    private void deserializeUser() {
        Properties props = FunctionsStock.deserializeUser(getActivity());
        EditText lastNameEditText = editUserProfileView.findViewById(R.id.lastNameEditText);
        EditText firstNameEditText = editUserProfileView.findViewById(R.id.firstNameEditText);
        EditText phoneEditText = editUserProfileView.findViewById(R.id.phoneEditText);
        EditText emailEditText = editUserProfileView.findViewById(R.id.emailEditText);
        lastNameEditText.setText(props.getProperty("last_name"));
        firstNameEditText.setText(props.getProperty("first_name"));
        phoneEditText.setText(props.getProperty("phone"));
        emailEditText.setText(props.getProperty("email"));
    }

    private void serializeUser() {
        Properties props = new Properties();
        FunctionsStock.setWorkingDirectory();
        try {
            new File(SERIALIZING_DIRECTORY + "/" + USER_SETTINGS_FILE).createNewFile();
        } catch (IOException e) {
            Toast.makeText(getActivity(), "failed to create property file",
                    Toast.LENGTH_LONG).show();
        }

        OutputStream outputFile = null;
        try {
            outputFile = new FileOutputStream(SERIALIZING_DIRECTORY + "/" + USER_SETTINGS_FILE);

            EditText lastNameEditText = editUserProfileView.findViewById(R.id.lastNameEditText);
            EditText firstNameEditText = editUserProfileView.findViewById(R.id.firstNameEditText);
            EditText phoneEditText = editUserProfileView.findViewById(R.id.phoneEditText);
            EditText emailEditText = editUserProfileView.findViewById(R.id.emailEditText);
            props.setProperty("last_name", lastNameEditText.getText().toString());
            props.setProperty("first_name", firstNameEditText.getText().toString());
            props.setProperty("phone", phoneEditText.getText().toString());
            props.setProperty("email", emailEditText.getText().toString());

            props.store(outputFile, null);
        } catch (IOException e) {
            Toast.makeText(getActivity(), "no property file found", Toast.LENGTH_LONG).show();
        } finally {
            try {
                if(outputFile != null)
                    outputFile.close();
            } catch (IOException e) {
                Toast.makeText(getActivity(), "failed to save property file",
                        Toast.LENGTH_LONG).show();
            }
        }
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
}
