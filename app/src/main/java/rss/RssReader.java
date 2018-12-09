package rss;

import android.os.AsyncTask;

public class RssReader {
    public static class ReadFeed extends AsyncTask<Void, Void, Void>{

        String url;
        public ReadFeed(String url) {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
