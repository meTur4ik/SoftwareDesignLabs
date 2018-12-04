package layout;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.asus_user.labs.MainActivity;
import com.example.asus_user.labs.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {


    private View loginFragmentView;
    private NavController navController;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        loginFragmentView = inflater.inflate(R.layout.fragment_login, container, false);

        NavHostFragment host = (NavHostFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.auth_nav_host_fragment);

        navController = host.getNavController();

        setUpSignInButton();
        setUpRegisterButton();

        return loginFragmentView;
    }

    private void setUpSignInButton(){
        Button signIn = loginFragmentView.findViewById(R.id.auth_sign_in_button);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().finish();
            }
        });
    }

    private void setUpRegisterButton(){
        Button register = loginFragmentView.findViewById(R.id.auth_register_button_home);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.registerFragment);
            }
        });
    }
}
