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
import functions.Utility;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static instances.UserConstants.SERIALIZING_DIRECTORY;
import static instances.UserConstants.USER_RSS_FILE;


public class HomeFragment extends Fragment {

    private View homeFragmentView;
    private RecyclerView home;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeFragmentView = inflater.inflate(R.layout.fragment_home, container, false);

        return homeFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Utility.isNetworkAvailable(getActivity())) {
            new DownloadRSS("http://static.userland.com/gems/backend/rssTwoExample2.xml")
                    .addOnDownloadListener(new DownloadRSS.onDownloadedListener() {
                        @Override
                        public void onPostExecute(Document rss) {
                            home = homeFragmentView.findViewById(R.id.home_recycler_view);
                            home.setLayoutManager(new LinearLayoutManager(getContext()));
                            List<RssNote> rssNotes;
                            rssNotes = ProcessXml(rss);
                            home.setAdapter(new RssRecycleViewAdapter((MainActivity) getActivity(), rssNotes));
                        }
                    }).execute();
        }
        else {
            List<RssNote> rssNotes = ProcessXml(GetData());

            home = homeFragmentView.findViewById(R.id.home_recycler_view);
            home.setLayoutManager(new LinearLayoutManager(getContext()));
            home.setAdapter(new RssRecycleViewAdapter((MainActivity) getActivity(), rssNotes));
        }
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

                DOMSource source = new DOMSource(xmlDoc);
                FileWriter writer = new FileWriter(new File(SERIALIZING_DIRECTORY + "/" + USER_RSS_FILE));
                StreamResult result = new StreamResult(writer);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.transform(source, result);
                return xmlDoc;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public Document GetData(){
        File file = new File(SERIALIZING_DIRECTORY + "/" + USER_RSS_FILE);
        try {
            InputStream inputStream = new FileInputStream(file);
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDoc = builder.parse(inputStream);

            return xmlDoc;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private String[] ProcessHtml(String html){
        org.jsoup.nodes.Document document = Jsoup.parse(html);
        org.jsoup.nodes.Element link = document.select("a").first();
        org.jsoup.nodes.Element image = document.select("img").first();
        String linkImage = null;
        if (image != null)
            linkImage = image.attr("src"); // A(|)blrEHHA
        String linkSource = null;
        if (link != null)
            linkSource = link.attr("href");
        String description = null;
        if(document != null)
            description = document.body().text();
        return new String[] {description, linkImage, linkSource};
    }

    private List<RssNote> ProcessXml(Document data) {
        List<RssNote> feedItems = new ArrayList<>();

        if (data != null) {
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
        }
        return feedItems;
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
