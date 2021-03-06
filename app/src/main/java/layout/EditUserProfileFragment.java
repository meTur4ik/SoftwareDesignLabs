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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import functions.MyNavigationUISetup;
import functions.SerializingFunctions;
import functions.Utility;
import glide.GlideApp;
import instances.AppUser;
import instances.UserConstants;

import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.asus_user.labs.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class EditUserProfileFragment extends Fragment {
    private static final int REQUEST_LOAD_IMAGE = 229;
    private static final int REQUEST_TAKE_PHOTO = 230;

    private static final int PERMISSION_REQUEST_CODE = 228;
    private static final String SERIALIZING_DIRECTORY = UserConstants.SERIALIZING_DIRECTORY;
    private static final String USER_SETTINGS_FILE = UserConstants.USER_SETTINGS_FILE;
    private static final String USER_AVATAR_FILE = UserConstants.USER_AVATAR_FILE;

    //private AppUser user;
    private View editUserProfileView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        editUserProfileView = inflater.inflate(R.layout.fragment_edit_user_profile, container, false);



        setLoadButtonAction();
        setDoneButtonAction();
        setSyncButton();

        if (hasPermissions()) {
            deserializeUser();
            deserializeAvatar();
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

                ImageView avatar = editUserProfileView.findViewById(R.id.avatarEditImageView);
                avatar.setImageURI(imageUri);
                avatar.setTag("changed");
                break;
            }
            case REQUEST_TAKE_PHOTO: {
                if(resultCode != Activity.RESULT_OK)
                    return;
                Bitmap takenPhoto = (Bitmap) data.getExtras().get("data");

                ImageView avatar = editUserProfileView.findViewById(R.id.avatarEditImageView);
                avatar.setImageBitmap(takenPhoto);
                break;
            }
        }

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

    /**
     * gather intents that can choose image and pass it via data section
     * @param context
     * @return gathered intent
     */
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
                if(Utility.isNetworkAvailable(getContext())) {
                    try {
                        serializeUser();
                    } catch (IllegalStateException e) {}

                }
                else {
                    Toast.makeText(getContext(), "network is not available", Toast.LENGTH_LONG).show();
                }
                MyNavigationUISetup.hideKeyboard(getActivity());
            }
        });
    }

    private void setSyncButton(){
        Button sync = editUserProfileView.findViewById(R.id.syncButton);

        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isNetworkAvailable(getContext())){
                    try {
                        syncUser();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * downloads all info about user from Firebase and saves it to file. also outputs it to
     * fragment
     */
    private void syncUser(){
        final ImageView avatarEditView = editUserProfileView.findViewById(R.id.avatarEditImageView);
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("Fetcing account data...");
        pd.setCancelable(false);
        final Fragment that = this;
        if(Utility.isNetworkAvailable(getContext())) {
            pd.show();
            //String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //FirebaseDatabase.getInstance().getReference().
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(getString(R.string.dbnode_users))
                    .orderByKey()
                    .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    AppUser usr = new AppUser();
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        usr = singleSnapshot.getValue(AppUser.class);
                    }

                    final AppUser user = usr;

                    EditText lastNameEditText = editUserProfileView.findViewById(R.id.lastNameEditText);
                    EditText firstNameEditText = editUserProfileView.findViewById(R.id.firstNameEditText);
                    EditText phoneEditText = editUserProfileView.findViewById(R.id.phoneEditText);
                    EditText emailEditText = editUserProfileView.findViewById(R.id.emailEditText);
                    EditText rssAddressEditText = editUserProfileView.findViewById(R.id.rssAddressEditText);

                    lastNameEditText.setText(usr.getLast_name());
                    firstNameEditText.setText(usr.getFirst_name());
                    phoneEditText.setText(usr.getPhone_number());
                    emailEditText.setText(usr.getEmail());
                    rssAddressEditText.setText(usr.getRss_address());


                    //Log.i("FIREBASE_IMAGE", usr.toString());
                    GlideApp.with(getActivity())
                            .load(usr.getProfile_image())
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    pd.dismiss();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    SerializingFunctions.serializeUserFields(user, that);
                                    WeakReference<Bitmap> avatar = new WeakReference<Bitmap> (((BitmapDrawable) resource).getBitmap());
                                    new SerializingFunctions.SaveAvatar(avatar,
                                            SERIALIZING_DIRECTORY + "/" + USER_AVATAR_FILE,
                                            new SerializingFunctions.SaveAvatar.SaveListener() {
                                                @Override
                                                public void onBeforeSave() {}

                                                @Override
                                                public void onAfterSave() {
                                                    pd.dismiss();
                                                    NavController controller = ((NavHostFragment) (getActivity()).getSupportFragmentManager()
                                                            .findFragmentById(R.id.nav_host_fragment))
                                                            .getNavController();

                                                    controller.navigate(R.id.userProfile);
                                                }

                                                @Override
                                                public void onError() {}
                                            }).execute();
                                    return false;
                                }
                            })
                            .into(avatarEditView);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    pd.dismiss();
                }
            });
        }
    }

    /**
     * loads avatar from local storage
     */
    private void deserializeAvatar() {
        final ImageView avatarEditView = editUserProfileView.findViewById(R.id.avatarEditImageView);

        SerializingFunctions.loadAvatar(avatarEditView,
                new File(SERIALIZING_DIRECTORY + "/" + USER_AVATAR_FILE));

    }

    /**
     * loads user from local storage
     */
    private void deserializeUser() {
        Properties props = SerializingFunctions.deserializeUser();
        AppUser user = new AppUser(props);
        Log.i("USER", user.toString());
        EditText lastNameEditText = editUserProfileView.findViewById(R.id.lastNameEditText);
        EditText firstNameEditText = editUserProfileView.findViewById(R.id.firstNameEditText);
        EditText phoneEditText = editUserProfileView.findViewById(R.id.phoneEditText);
        EditText emailEditText = editUserProfileView.findViewById(R.id.emailEditText);
        EditText rssAddressEditText = editUserProfileView.findViewById(R.id.rssAddressEditText);
        lastNameEditText.setText(user.getLast_name());
        firstNameEditText.setText(user.getFirst_name());
        phoneEditText.setText(user.getPhone_number());
        emailEditText.setText(user.getEmail());
        rssAddressEditText.setText(user.getRss_address());
    }

    /**
     * saves user to local storage and Firebase
     */
    private void serializeUser() {

        ImageView avatarImageView = editUserProfileView.findViewById(R.id.avatarEditImageView);

        final EditText lastNameEditText = editUserProfileView.findViewById(R.id.lastNameEditText);
        final EditText firstNameEditText = editUserProfileView.findViewById(R.id.firstNameEditText);
        final EditText phoneEditText = editUserProfileView.findViewById(R.id.phoneEditText);
        final EditText emailEditText = editUserProfileView.findViewById(R.id.emailEditText);
        final EditText rssAddressText = editUserProfileView.findViewById(R.id.rssAddressEditText);
        final String[] imageUri = new String[1];

        WeakReference<Bitmap> bitmapWeakReference = new WeakReference<Bitmap>(
                ((BitmapDrawable)avatarImageView.getDrawable()).getBitmap()
        );

        if(!avatarImageView.getTag().toString().equals("changed")) {
            //Log.i("TAG", avatarImageView.getTag().toString());
            AppUser usr = new AppUser(SerializingFunctions.deserializeUser());
            usr.setLast_name(lastNameEditText.getText().toString());
            usr.setFirst_name(firstNameEditText.getText().toString());
            usr.setPhone_number(phoneEditText.getText().toString());
            usr.setEmail(emailEditText.getText().toString());
            usr.setRss_address(rssAddressText.getText().toString());
            usr.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
            SerializingFunctions.serializeUserFields(usr,this);

        } else {
            final ProgressDialog pd = new ProgressDialog(getActivity());
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setMessage("Saving photo");
            pd.setOwnerActivity(getActivity());
            pd.setIndeterminate(true);
            pd.setCancelable(false);

            final FragmentActivity thatActivity = this.getActivity();
            final Fragment that = this;

            new SerializingFunctions.SaveAvatar(bitmapWeakReference,
                    SERIALIZING_DIRECTORY + "/" + USER_AVATAR_FILE, new SerializingFunctions.SaveAvatar.SaveListener() {
                @Override
                public void onBeforeSave() {
                    pd.show();
                }

                @Override
                public void onAfterSave() {
                }

                @Override
                public void onError() {
                    Toast.makeText(thatActivity, "failed to save image", Toast.LENGTH_LONG).show();
                }
            }).execute();

            Bitmap bitmap = ((BitmapDrawable) avatarImageView.getDrawable()).getBitmap();
            new SerializingFunctions.CompressImageBackground(bitmap, new SerializingFunctions.CompressImageBackground.UploadListener() {
                @Override
                public void onPreExecute() {
                    pd.show();
                }

                @Override
                public void onPostExecute(byte[] bitmap) {
                    StorageReference reference = FirebaseStorage.getInstance().getReference()
                            .child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/avatar/");

                    reference.putBytes(bitmap)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    try {
                                        pd.dismiss();
                                    } catch (IllegalArgumentException e) {
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "failed to upload image", Toast.LENGTH_LONG).show();
                                }
                            });

                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            AppUser usr = new AppUser();
                            usr.setEmail(emailEditText.getText().toString());
                            usr.setFirst_name(firstNameEditText.getText().toString());
                            usr.setLast_name(lastNameEditText.getText().toString());
                            usr.setPhone_number(phoneEditText.getText().toString());
                            usr.setRss_address(rssAddressText.getText().toString());
                            usr.setProfile_image(uri.toString());
                            usr.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            SerializingFunctions.serializeUserFields(usr, that);

                            try {
                                NavController controller = ((NavHostFragment) (thatActivity).getSupportFragmentManager()
                                        .findFragmentById(R.id.nav_host_fragment))
                                        .getNavController();

                                controller.navigate(R.id.userProfile);
                            } catch (NullPointerException e) {
                            }
                        }
                    });
                }
            }).execute();
        }

        NavController controller = ((NavHostFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment))
                .getNavController();

        controller.navigate(R.id.userProfile);
    }

    /**
     * storage permission explanation explanation
     */
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

    /**
     * runs intent of system settings for this application
     */
    private void openApplicationSettings(){
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getActivity().getPackageName()));
        startActivityForResult(appSettingsIntent, PERMISSION_REQUEST_CODE);
    }

    /**
     * checks if there are all needed permissions
     * @return true if all permissions granted false if not.
     */
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
