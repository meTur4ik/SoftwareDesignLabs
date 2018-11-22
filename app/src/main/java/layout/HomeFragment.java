package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

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

        setRegisterButtonClick();
        setSignUpButtonClick();

        return homeFragmentView;
    }

    private void setRegisterButtonClick(){
        Button registerButton = homeFragmentView.findViewById(R.id.register_button_home);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavHostFragment) getActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment))
                        .getNavController()
                        .navigate(R.id.registerUserFragment);
            }
        });
    }
    private void setSignUpButtonClick(){
        Button signUpButton = homeFragmentView.findViewById(R.id.sign_in_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavHostFragment) getActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment))
                        .getNavController()
                        .navigate(R.id.userProfile);
            }
        });
    }
}
