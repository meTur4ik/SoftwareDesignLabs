package layout;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import instances.UserConstants;
import rss.RssNote;
import rss.RssRecycleViewAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.jsoup.Jsoup;
import org.w3c.dom.*;
import org.xml.sax.Parser;

import com.example.asus_user.labs.MainActivity;
import com.example.asus_user.labs.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class HomeFragment extends Fragment {

    private View homeFragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeFragmentView = inflater.inflate(R.layout.fragment_home, container, false);

        new DownloadRSS("https://www.onliner.by/feed")
                .addOnDownloadListener(new DownloadRSS.onDownloadedListener() {
                    @Override
                    public void onPostExecute(Document rss) {
                        RecyclerView home = homeFragmentView.findViewById(R.id.home_recycler_view);
                        home.setLayoutManager(new LinearLayoutManager(getContext()));
                        List<RssNote> rssNotes;
                        rssNotes = ProcessXml(rss);
                        home.setAdapter(new RssRecycleViewAdapter((MainActivity) getActivity(), rssNotes));
                    }
                }).execute();

        return homeFragmentView;
    }

    public static class DownloadRSS extends AsyncTask<Void, Void, Document>{
        interface onDownloadedListener {
            void onPostExecute(Document rss);
        }

        onDownloadedListener listener;
        String address;
        public DownloadRSS(String address) {
            this.address = address;
        }

        @Override
        protected Document doInBackground(Void... voids) {
            return Getdata(address);
        }

        @Override
        protected void onPostExecute(Document document) {
            listener.onPostExecute(document);
            super.onPostExecute(document);
        }

        public DownloadRSS addOnDownloadListener(onDownloadedListener listener) {
            this.listener = listener;
            return this;
        }

        public Document Getdata(String address) {
            try {
                URL url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                Document xmlDoc = builder.parse(inputStream);
                return xmlDoc;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private String[] ProcessHtml(String html){
        org.jsoup.nodes.Document document = Jsoup.parse(html);
        org.jsoup.nodes.Element link = document.select("a").first();
        org.jsoup.nodes.Element image = document.select("img").first();
        String linkImage = image.attr("src"); // A(|)blrEHHA
        String linkSource = link.attr("href");
        String description = document.body().text();
        return new String[] {description, linkImage, linkSource};
    }

    private List<RssNote> ProcessXml(Document data) {
        if (data != null) {
            List<RssNote> feedItems = new ArrayList<>();
            Element root = data.getDocumentElement();
            Node channel = root.getChildNodes().item(1);
            NodeList items = channel.getChildNodes();
            for (int i = 0; i < items.getLength(); i++) {
                Node cureentchild = items.item(i);
                if (cureentchild.getNodeName().equalsIgnoreCase("item")) {
                    RssNote item = new RssNote();
                    NodeList itemchilds = cureentchild.getChildNodes();
                    for (int j = 0; j < itemchilds.getLength(); j++) {
                        Node cureent = itemchilds.item(j);
                        if (cureent.getNodeName().equalsIgnoreCase("title")) {
                            item.setTitle(cureent.getTextContent());
                        } else if (cureent.getNodeName().equalsIgnoreCase("description")) {
                            String[] strings = ProcessHtml(cureent.getTextContent());
                            //item.setDescription(cureent.getTextContent());
                            item.setDescription(limitString(strings[0]));
                            item.setImageUri(strings[1]);
                            item.setLink(strings[2]);
                        } else if (cureent.getNodeName().equalsIgnoreCase("image")) {
                            item.setImageUri(cureent.getTextContent());
                        } else if (cureent.getNodeName().equalsIgnoreCase("link")) {
                            item.setLink(cureent.getTextContent());
                        }
                    }
                    feedItems.add(item);


                }
            }
            return feedItems;
        }
        return null;
    }

    public String limitString(String str){
        int count = 0;
        byte dots = 0;
        for (char c : str.toCharArray()){
            count++;
            if (c == '.')
                dots++;
            if (dots == 2)
                return str.substring(0, count) + "...";
            if (count == 200)
                return str.substring(0, count) + "...";
        }
        return str;
    }
}
