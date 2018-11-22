package layout;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import instances.AppUser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus_user.labs.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFragment extends Fragment {


    public RegistrationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        AppUser user = new AppUser();
        /*FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbnode_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user);*/
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

}
