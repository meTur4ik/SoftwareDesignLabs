package layout;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import instances.AppUser;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus_user.labs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {


    private View registerView;
    private NavController navController;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        registerView = inflater.inflate(R.layout.fragment_register, container, false);
        NavHostFragment host = (NavHostFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.auth_nav_host_fragment);
        navController = host.getNavController();

        setRegisterClick();
        return registerView;
    }

    private void registerNewUser() {
        EditText emailET = registerView.findViewById(R.id.auth_register_email_editText);
        EditText passwordET = registerView.findViewById(R.id.auth_register_password_editText);
        EditText passwordRepeatET = registerView.findViewById(R.id.auth_repeat_password_editText);
        final String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordRepeat = passwordRepeatET.getText().toString();
        if (password.equals(passwordRepeat)){
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                AppUser user = new AppUser();
                                user.setLast_name(email.substring(0, email.indexOf('@')));
                                user.setFirst_name("");
                                user.setEmail(email);
                                user.setPhone_number("");
                                user.setProfile_image("");
                                user.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                FirebaseDatabase.getInstance().getReference()
                                        .child(getString(R.string.dbnode_users))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //FirebaseAuth.getInstance().getCurrentUser().ge
                                        FirebaseAuth.getInstance().signOut();
                                        goToLogin();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        e.printStackTrace();
                                        Log.e("CYKA", e.getMessage());
                                        Toast.makeText(getContext(),"something went wrong", Toast.LENGTH_LONG).show();
                                    }//
                                });
                            }
                        }
                    });
        }
    }

    private void setRegisterClick(){
        Button registerBtn = registerView.findViewById(R.id.auth_register_register_button);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });
    }

    private void goToLogin(){
        navController.navigate(R.id.loginFragment);
    }
}
