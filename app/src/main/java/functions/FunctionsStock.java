package functions;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus_user.labs.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Properties;

import layout.UserProfileFragment;

public abstract class FunctionsStock {
    private static final String SERIALIZING_DIRECTORY = UserProfileFragment.SERIALIZING_DIRECTORY;
    private static final String USER_SETTINGS_FILE = UserProfileFragment.USER_SETTINGS_FILE;
    private static final String USER_AVATAR_FILE = UserProfileFragment.USER_AVATAR_FILE;

    public static class ImageSerialize extends AsyncTask<Activity, Void, Void> {

        private final WeakReference<ImageView> imageViewReference;


        public ImageSerialize(ImageView imageView){
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Void doInBackground(Activity ... activities){
            ImageView target = imageViewReference.get();
            BitmapDrawable holder = null;
            final Activity senderActivity = activities[0];
            /* this made cuz with big pictures sometimes file is still aquired by I
            suppose the other task that still reads it
             */
            if (target != null)
                holder = ((BitmapDrawable) target.getDrawable());
            else {
                showFailed(senderActivity);
                return null;
            }
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
                showSuccess(senderActivity);
            } catch (IOException e) {
                showWriteFailed(senderActivity);
            }
            return null;
        }
        private void showFailed(final Activity sender){
            sender.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(sender, "if you see this message when just set the avatar" +
                                    "but don't see the avatar" +
                                    " please return, set the photo again and wait until it " +
                                    "appears on the screen",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
        private void showSuccess(final Activity sender){
            sender.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(sender, "avatar saved", Toast.LENGTH_LONG).show();
                }
            });
        }
        private void showWriteFailed(final Activity sender){
            sender.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(sender, "failed to write file with avatar", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public static class ImageDeserialize extends AsyncTask<Void, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewReference;

        public ImageDeserialize(ImageView imageView){
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

    public static void setWorkingDirectory(){
        final File wd = new File(SERIALIZING_DIRECTORY);
        if (!wd.exists()) {
            wd.mkdir();
        }
    }

    public static Properties deserializeUser(Activity activity) {
        Properties props = new Properties();
        InputStream inputFile = null;
        try {
            inputFile = new FileInputStream(SERIALIZING_DIRECTORY + "/" + USER_SETTINGS_FILE);

            props.load(inputFile);

        } catch (IOException e) {
            Toast.makeText(activity, "failed to locate property file",
                    Toast.LENGTH_LONG).show();
        } finally {
            try {
                if(inputFile != null)
                    inputFile.close();
            } catch (IOException e) {
                Toast.makeText(activity, "failed to save property file",
                        Toast.LENGTH_LONG).show();
            }
        }
        return props;
    }
}
