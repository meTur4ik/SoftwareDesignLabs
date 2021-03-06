package layout;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import functions.Utility;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus_user.labs.MainActivity;
import com.example.asus_user.labs.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


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

        if(FirebaseAuth.getInstance().getCurrentUser() !=  null &&
                !FirebaseAuth.getInstance().getCurrentUser().isAnonymous()){
            startApp();
        }

        return loginFragmentView;
    }

    private void setUpSignInButton(){
        Button signIn = loginFragmentView.findViewById(R.id.auth_sign_in_button);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailET = loginFragmentView.findViewById(R.id.auth_sign_in_email);
                EditText passwordET = loginFragmentView.findViewById(R.id.auth_sign_in_password);
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();

                if (!Utility.isNetworkAvailable(getContext())){
                    return;
                }

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startApp();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Auth Failed", e.getMessage());
                        Toast.makeText(getContext(),"incorrect credentials", Toast.LENGTH_LONG).show();
                    }
                });

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

    private void startApp(){
        startActivity(new Intent(getContext(), MainActivity.class));
        getActivity().finish();
    }
}
