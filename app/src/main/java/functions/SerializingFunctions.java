package functions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.asus_user.labs.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;
import glide.GlideApp;
import layout.UserProfileFragment;

import static instances.UserConstants.SERIALIZING_DIRECTORY;
import static instances.UserConstants.USER_SETTINGS_FILE;

public abstract class SerializingFunctions {
    /*private static final String SERIALIZING_DIRECTORY = UserProfileFragmen.SERIALIZING_DIRECTORY;
    private static final String USER_SETTINGS_FILE = UserProfileFragment.USER_SETTINGS_FILE;
    private static final String USER_AVATAR_FILE = UserProfileFragment.USER_AVATAR_FILE;*/

    public interface SaveListener {
        void onBeforeSave();
        void onAfterSave();
        void onError();
    }

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

    public static class SaveAvatar extends AsyncTask<Void, Void, Boolean>{

        private WeakReference<Bitmap> bitmapWeakReference;
        private final String path;
        private final SaveListener listener;

        public SaveAvatar(WeakReference<Bitmap> bitmapWeakReference, String path, SaveListener listener) {
            this.bitmapWeakReference = bitmapWeakReference;
            this.path = path;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listener.onBeforeSave();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Bitmap imageToSave = bitmapWeakReference.get();
            if(imageToSave == null)
                return false;

            try (FileOutputStream out = new FileOutputStream(path)) {
                imageToSave.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (IOException e) {
                listener.onError();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            listener.onAfterSave();
            super.onPostExecute(aBoolean);
        }
    }

    public static class UploadImageBackground extends AsyncTask<Void, Void, byte[]>{

        public interface UploadListener{
            void onPreExecute();
            void onPostExecute(byte[] bitmap);
        }
        WeakReference<Bitmap> picture;
        UploadListener uploadListener;

        public UploadImageBackground(Bitmap picture, UploadListener uploadListener) {
            this.picture = new WeakReference<Bitmap>(picture);
            this.uploadListener = uploadListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            uploadListener.onPreExecute();
        }

        @Override
        protected byte[] doInBackground(Void... voids) {
            Bitmap compressed = picture.get();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            compressed.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            return byteArray;
        }

        @Override
        protected void onPostExecute(byte[] bitmap) {
            uploadListener.onPostExecute(bitmap);
            super.onPostExecute(bitmap);
        }
    }

    public static boolean loadAvatar(ImageView targetView, File targetFile){
        if(!targetFile.exists()) {
            return false;
        }
        GlideApp.with(targetView.getContext())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .load(targetFile)
                .override(1280, 800)
                .into(targetView);
        return true;
    }
}
