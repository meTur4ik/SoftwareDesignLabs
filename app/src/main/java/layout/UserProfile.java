package layout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus_user.labs.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

public class UserProfile extends Fragment {
    private static final int REQUEST_LOAD_IMAGE = 229;
    private static final String SERIALIZING_DIRECTORY = "/storage/emulated/0/.Labs";
    private static final String USER_SETTINGS_FILE = "user_settings.properties";
    private static final String USER_AVATAR_FILE = "avatar.jpeg";

    private Properties props = new Properties();
    private ImageView imgView;
    private View userProfileView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         userProfileView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        // Inflate the layout for this fragment
        imgView = userProfileView.findViewById(R.id.avatarImageView);
        setButtonAction();
        //String currentDir = System.getProperty("user.dir");
        //Toast.makeText(getActivity(), currentDir, Toast.LENGTH_LONG).show(); output "/"
        return userProfileView;
    }

    @Override
    public void onStart() {
        super.onStart();
        deserializeUser();
        deserializeAvatar();
    }

    @Override
    public void onPause(){
        super.onPause();
        serializeUser();
        serializeAvatar();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_LOAD_IMAGE && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            imgView.setImageURI(imageUri);

        }
    }

    private void setButtonAction(){
        Button load = userProfileView.findViewById(R.id.loadImageButton);
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, REQUEST_LOAD_IMAGE);
            }
        });
    }

    private void deserializeUser() {
        Properties props = new Properties();
        final File wd = new File(SERIALIZING_DIRECTORY);
        InputStream inputFile = null;
        try {
            inputFile = new FileInputStream(SERIALIZING_DIRECTORY + "/" + USER_SETTINGS_FILE);

            props.load(inputFile);

            TextView lastNameTV = userProfileView.findViewById(R.id.lastNameTextView);
            lastNameTV.setText(props.getProperty("last_name"));
            TextView firstNameTV = userProfileView.findViewById(R.id.firstNameTextView);
            firstNameTV.setText(props.getProperty("first_name"));

        } catch (IOException e) {
            Toast.makeText(getActivity(), "failed to locate property file",
                    Toast.LENGTH_LONG).show();
        } finally {
            try {
                if(inputFile != null)
                    inputFile.close();
            } catch (IOException e) {
                Toast.makeText(getActivity(), "failed to save property file",
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    private void serializeUser() {
        final File wd = new File(SERIALIZING_DIRECTORY);
        wd.mkdir(); // pox if created
        try {
            new File(SERIALIZING_DIRECTORY + "/" + USER_SETTINGS_FILE).createNewFile();
        } catch (IOException e) {
            Toast.makeText(getActivity(), "failed to create property file",
                    Toast.LENGTH_LONG).show();
        }

        OutputStream outputFile = null;
        try {
            outputFile = new FileOutputStream(SERIALIZING_DIRECTORY + "/" + USER_SETTINGS_FILE);

            TextView lastNameTV = userProfileView.findViewById(R.id.lastNameTextView);
            TextView firstNameTV = userProfileView.findViewById(R.id.firstNameTextView);
            props.setProperty("last_name", lastNameTV.getText().toString());
            props.setProperty("first_name", firstNameTV.getText().toString());

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

    private void serializeAvatar() {
        ImageView avatar = userProfileView.findViewById(R.id.avatarImageView);
        new ImageSerialyze(avatar).execute();
    }

    private void deserializeAvatar() {
        ImageView avatarView = userProfileView.findViewById(R.id.avatarImageView);
        new ImageDeserialyze(avatarView).execute();
    }

    private static class ImageDeserialyze extends AsyncTask<Void, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewReference;

        public ImageDeserialyze(ImageView imageView){
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {

            File avatarFile = new File(SERIALIZING_DIRECTORY + "/" + USER_AVATAR_FILE);
            Bitmap avatarBitmap = null;
            if (avatarFile.exists()) {
                 avatarBitmap = BitmapFactory.decodeFile(avatarFile.getAbsolutePath());
            }
            return avatarBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap avatarBitmap){
            if (isCancelled()){
                avatarBitmap = null;
            }
            if (imageViewReference != null){
                ImageView targetImageView = imageViewReference.get();
                if (targetImageView != null){
                    targetImageView.setImageBitmap(avatarBitmap);
                }
            }
        }
    }

    private static class ImageSerialyze extends AsyncTask<Void, Void, Void> {

        private final WeakReference<ImageView> imageViewReference;

        public ImageSerialyze(ImageView imageView){
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Void doInBackground(Void ... voids){
            ImageView target = imageViewReference.get();
            BitmapDrawable holder = ((BitmapDrawable) target.getDrawable());
            Bitmap img;
            if (holder != null){
                img = holder.getBitmap();
            } else {
                return null;
            }

            try (FileOutputStream out = new FileOutputStream(SERIALIZING_DIRECTORY + "/" + USER_AVATAR_FILE)) {
                if (img != null) {
                    img.compress(Bitmap.CompressFormat.PNG, 100, out);
                }
            } catch (IOException e) {
                Toast.makeText(imageViewReference.get().getContext(), "failed to save avatar",
                        Toast.LENGTH_LONG).show();
            }
            return null;
        }
    }
}
