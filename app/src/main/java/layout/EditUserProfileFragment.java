package layout;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import functions.MyNavigationUISetup;
import functions.SerializingFunctions;
import instances.AppUser;

import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class EditUserProfileFragment extends Fragment {
    private static final int REQUEST_LOAD_IMAGE = 229;
    private static final int REQUEST_TAKE_PHOTO = 230;

    private static final int PERMISSION_REQUEST_CODE = 228;
    private static final String SERIALIZING_DIRECTORY = UserProfileFragment.SERIALIZING_DIRECTORY;
    private static final String USER_SETTINGS_FILE = UserProfileFragment.USER_SETTINGS_FILE;
    private static final String USER_AVATAR_FILE = UserProfileFragment.USER_AVATAR_FILE;

    private AppUser user;
    private View editUserProfileView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        editUserProfileView = inflater.inflate(R.layout.fragment_edit_user_profile, container, false);

        deserializeAvatar();

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

        switch (requestCode){
            case REQUEST_LOAD_IMAGE:{
                if(resultCode != Activity.RESULT_OK)
                    return;

                Uri imageUri = data.getData();
                //serialize avatar

                ImageView avatar = editUserProfileView.findViewById(R.id.avatarEditImageView);
                avatar.setImageURI(imageUri);
                //serializeAvatar();
                break;
            }
            case REQUEST_TAKE_PHOTO: {
                if(requestCode != Activity.RESULT_OK)
                    return;
                Bitmap takenPhoto = (Bitmap) data.getExtras().get("data");

                ImageView avatar = editUserProfileView.findViewById(R.id.avatarEditImageView);
                avatar.setImageBitmap(takenPhoto);
                //serializeAvatar();
                //getDialog().dismiss();
                break;
            }
        }

    }

    private void serializeAvatar(){
        ImageView avatarImageView = editUserProfileView.findViewById(R.id.avatarEditImageView);
        WeakReference<Bitmap> bitmapWeakReference = new WeakReference<Bitmap>(
                ((BitmapDrawable)avatarImageView.getDrawable()).getBitmap()
        );

        ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Saving photo");
        pd.setOwnerActivity(getActivity());
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        final FragmentActivity that = this.getActivity();

        new SerializingFunctions.SaveAvatarAndBackToProfile(bitmapWeakReference,
                SERIALIZING_DIRECTORY + "/" + USER_AVATAR_FILE, new SerializingFunctions.SaveListener() {
            @Override
            public void onBeforeSave() {

            }

            @Override
            public void onAfterSave() {
                NavController controller = ((NavHostFragment)(that).getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment))
                        .getNavController();

                if (controller != null)
                    controller.navigate(R.id.userProfile);
            }

            @Override
            public void onError() {

            }
        }).execute();
    }

    private void setLoadButtonAction(){
        Button load = editUserProfileView.findViewById(R.id.loadPhotoButton);
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(getPickImageIntent(getActivity()), REQUEST_LOAD_IMAGE);
            }
        });
    }

    public static Intent getPickImageIntent(Context context) {
        Intent chooserIntent = null;

        List<Intent> intentList = new ArrayList<>();

        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.putExtra("requestCode", REQUEST_LOAD_IMAGE);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);
        takePhotoIntent.putExtra("requestCode", REQUEST_TAKE_PHOTO);
        intentList = addIntentsToList(context, intentList, pickIntent);
        intentList = addIntentsToList(context, intentList, takePhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    context.getString(R.string.pick_image_intent_text));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }

    private void setDoneButtonAction() {
        Button done = editUserProfileView.findViewById(R.id.doneButton);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serializeUser();
                serializeAvatar();
                MyNavigationUISetup.hideKeyboard(getActivity());
                /*((NavHostFragment) getActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment))
                        .getNavController().navigate(R.id.userProfile);*/
            }
        });
    }

    private void deserializeAvatar() {
        ImageView avatarEditView = editUserProfileView.findViewById(R.id.avatarEditImageView);
        SerializingFunctions.loadAvatar(avatarEditView,
                new File(SERIALIZING_DIRECTORY + "/" + USER_AVATAR_FILE));
    }

    private void deserializeUser() {
        Properties props = SerializingFunctions.deserializeUser();
        user = new AppUser(props);
        EditText lastNameEditText = editUserProfileView.findViewById(R.id.lastNameEditText);
        EditText firstNameEditText = editUserProfileView.findViewById(R.id.firstNameEditText);
        EditText phoneEditText = editUserProfileView.findViewById(R.id.phoneEditText);
        EditText emailEditText = editUserProfileView.findViewById(R.id.emailEditText);
        lastNameEditText.setText(user.getLast_name());
        firstNameEditText.setText(user.getFirst_name());
        phoneEditText.setText(user.getPhone_number());
        emailEditText.setText(user.getEmail());
    }

    private void serializeUser() {
        SerializingFunctions.setWorkingDirectory();
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
            user.setFirst_name(firstNameEditText.getText().toString());
            user.setLast_name(lastNameEditText.getText().toString());
            user.setPhone_number(phoneEditText.getText().toString());
            user.setEmail(emailEditText.getText().toString());

            Properties props = user.toProperties();

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
