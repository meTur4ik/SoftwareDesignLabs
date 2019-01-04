package layout;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import functions.SerializingFunctions;
import functions.Utility;
import instances.AppUser;
import rss.RssNote;
import rss.RssProcessing;
import rss.RssRecycleViewAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.w3c.dom.*;

import com.example.asus_user.labs.R;

import java.util.List;

import static rss.RssProcessing.ProcessXml;
import static rss.RssProcessing.limitString;


public class HomeFragment extends Fragment {

    private View homeFragmentView;
    private RecyclerView home;
    private AppUser user;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeFragmentView = inflater.inflate(R.layout.fragment_home, container, false);
        user = new AppUser(SerializingFunctions.deserializeUser());

        int deviceOrientation = getResources().getConfiguration().orientation;
        if (deviceOrientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new LinearLayoutManager(getContext());
        }
        else {
            layoutManager = new GridLayoutManager(getContext(), 2);
        }


        setSwipeRefreshAction();
        return homeFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRSS();
    }

    /**
     * loads and parses rss to cards
     */
    private void loadRSS() {
        home = homeFragmentView.findViewById(R.id.home_recycler_view);
        //home.setLayoutManager(new LinearLayoutManager(getContext()));
        home.setLayoutManager(layoutManager);
        final SwipeRefreshLayout swipeRefreshLayout = homeFragmentView.findViewById(R.id.home_swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(true);
        final Context ctx = getContext();
        //if (ctx == null) { return; }
        if(Utility.isNetworkAvailable(getActivity())) {
            new RssProcessing.DownloadRSS(user.getRss_address())
                    .addOnDownloadListener(new RssProcessing.DownloadRSS.OnDownloadedListener() {
                        @Override
                        public void onPostExecute(Document rss) {
                            List<RssNote> rssNotes;
                            rssNotes = ProcessXml(rss);
                            home.setAdapter(new RssRecycleViewAdapter(ctx, rssNotes));
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    })
                    .addOnFailureListener(new RssProcessing.DownloadRSS.OnFailureListener() {
                        @Override
                        public void onFailure() {
                            Toast.makeText(ctx, "please check RSS link or Sync the account", Toast.LENGTH_LONG).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }).execute();
        }
        else {
            List<RssNote> rssNotes = ProcessXml(RssProcessing.GetData());

            home = homeFragmentView.findViewById(R.id.home_recycler_view);
            home.setLayoutManager(layoutManager);
            //home.setLayoutManager(new LinearLayoutManager(getContext()));
            home.setAdapter(new RssRecycleViewAdapter(getActivity(), rssNotes));
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setSwipeRefreshAction(){
        final SwipeRefreshLayout swipeRefreshLayout = homeFragmentView.findViewById(R.id.home_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRSS();
            }
        });
    }
}
