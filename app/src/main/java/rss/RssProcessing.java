package rss;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
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

public abstract class RssProcessing {
    // limiting strings
    public static String limitString(String str){
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

    public static String limitString(String str, int symbols){
        int count = 0;
        byte dots = 0;
        for (char c : str.toCharArray()){
            count++;
            if (c == '.')
                dots++;
            if (count == symbols)
                return str.substring(0, count) + "...";
        }
        return str;
    }

    public static String limitString(String str, int symbols, byte dots){
        int count = 0;
        byte dotsAmount = 0;
        for (char c : str.toCharArray()){
            count++;
            if (c == '.')
                dots++;
            if (dotsAmount == dots)
                return str.substring(0, count) + "...";
            if (count == symbols)
                return str.substring(0, count) + "...";
        }
        return str;
    }
    //end limiting strings

    public static class DownloadRSS extends AsyncTask<Void, Void, Document>{
        public interface onDownloadedListener {
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

    public static Document GetData(){
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

    //process XML and HTML

    public static String[] ProcessHtml(String html){
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

    /**
     * processes XML to rss notes
     * @param data
     * @return List<RssNote>
     */
    public static List<RssNote> ProcessXml(Document data) {
        List<RssNote> feedItems = new ArrayList<>();

        if (data != null) {
            Element root = data.getDocumentElement();
            Node channel = root.getChildNodes().item(1);
            NodeList items = channel.getChildNodes();
            for (int i = 0; i < items.getLength(); i++) {
                Node currentchild = items.item(i);
                if (currentchild.getNodeName().equalsIgnoreCase("item")) {
                    RssNote item = new RssNote();
                    NodeList itemchilds = currentchild.getChildNodes();
                    for (int j = 0; j < itemchilds.getLength(); j++) {
                        Node current = itemchilds.item(j);
                        if (current.getNodeName().equalsIgnoreCase("title")) {
                            item.setTitle(current.getTextContent());
                        } else if (current.getNodeName().equalsIgnoreCase("description")) {
                            String[] strings = ProcessHtml(current.getTextContent());
                            item.setDescription(limitString(strings[0]));
                            item.setImageUri(strings[1]);
                            item.setLink(strings[2]);
                        } else if (current.getNodeName().equalsIgnoreCase("image")) {
                            item.setImageUri(current.getTextContent());
                        } else if (current.getNodeName().equalsIgnoreCase("link")) {
                            item.setLink(current.getTextContent());
                        } else if (current.getNodeName().equalsIgnoreCase("media:content")
                                || current.getNodeName().equalsIgnoreCase("media:thumbnail")) {
                            item.setImageUri(current.getAttributes().getNamedItem("url").getNodeValue());
                        }
                    }
                    feedItems.add(item);


                }
            }
        }
        return feedItems;
    }

}
