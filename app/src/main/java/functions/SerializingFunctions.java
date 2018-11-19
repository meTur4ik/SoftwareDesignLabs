package functions;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus_user.labs.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import layout.UserProfileFragment;

public abstract class SerializingFunctions {
    private static final String SERIALIZING_DIRECTORY = UserProfileFragment.SERIALIZING_DIRECTORY;
    private static final String USER_SETTINGS_FILE = UserProfileFragment.USER_SETTINGS_FILE;
    private static final String USER_AVATAR_FILE = UserProfileFragment.USER_AVATAR_FILE;

    public static void setWorkingDirectory(){
        final File wd = new File(SERIALIZING_DIRECTORY);
        if (!wd.exists()) {
            wd.mkdir();
        }
    }

    public static Properties deserializeUser() {
        Properties props = new Properties();
        InputStream inputFile = null;
        try {
            inputFile = new FileInputStream(SERIALIZING_DIRECTORY + "/" + USER_SETTINGS_FILE);

            props.load(inputFile);

        } catch (IOException e) {
            Log.v("alarm", "failed to locate property file");
        } finally {
            try {
                if(inputFile != null)
                    inputFile.close();
            } catch (IOException e) {
                Log.v("alarm","failed to save property file");
            }
        }
        return props;
    }

    public static class SaveImage implements Callable<Boolean> {

        private Bitmap imageToSave;
        private final String path;

        public SaveImage(ImageView imageView, String path) {
            imageToSave = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            this.path = path;
        }

        public SaveImage(Bitmap image, String path) {
            imageToSave = image;
            this.path = path;
        }

        @Override
        public Boolean call() throws NullPointerException {
            if (imageToSave == null)
                throw new NullPointerException("no image");

            try (FileOutputStream out = new FileOutputStream(path)) {
                imageToSave.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (IOException e) {
                return false;
            }
            return true;
        }
    }

    public static class LoadImage implements Callable<Bitmap> {

        private final String path;
        public LoadImage(String path) {
            this.path = path;
        }

        @Override
        public Bitmap call() throws FileNotFoundException {
            File avatarFile = new File(path);
            Bitmap avatarBitmap = null;
            if (avatarFile.exists()) {
                avatarBitmap = BitmapFactory.decodeFile(avatarFile.getAbsolutePath());
            } else {
              throw new FileNotFoundException ("file not found");
            }
            return avatarBitmap;
        }
    }
}
