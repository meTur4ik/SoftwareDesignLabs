package layout;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import functions.Utility;
import instances.UserConstants;
import rss.RssNote;
import rss.RssProcessing;
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
import static rss.RssProcessing.ProcessXml;
import static rss.RssProcessing.limitString;


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
            new RssProcessing.DownloadRSS("https://news.tut.by/rss/index.rss")
                    .addOnDownloadListener(new RssProcessing.DownloadRSS.onDownloadedListener() {
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
            List<RssNote> rssNotes = ProcessXml(RssProcessing.GetData());

            home = homeFragmentView.findViewById(R.id.home_recycler_view);
            home.setLayoutManager(new LinearLayoutManager(getContext()));
            home.setAdapter(new RssRecycleViewAdapter((MainActivity) getActivity(), rssNotes));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putSerializable(home);
    }







}
