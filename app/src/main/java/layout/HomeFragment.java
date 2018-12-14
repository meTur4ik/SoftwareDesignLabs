package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import rss.RssRecycleViewAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.asus_user.labs.R;


public class HomeFragment extends Fragment {

    private View homeFragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeFragmentView = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView home = homeFragmentView.findViewById(R.id.home_recycler_view);
        home.setLayoutManager(new LinearLayoutManager(getContext()));

        //home.setAdapter(new RssRecycleViewAdapter(getContext(), Datamana));

        return homeFragmentView;
    }
}
