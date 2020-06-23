package com.example.cegepsoccerleague;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateProfileFragment extends Fragment implements View.OnClickListener{

    private TextInputLayout update_firstname, update_lastname, update_password, update_confirm_password;
    private TextInputEditText update_firstname_edit_txt, update_lastname_edit_txt, update_password_edit_txt, update_confirm_password_edit_txt;
    private MaterialButton update_profile_btn;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Context context;
    private String user_type,user_email;
    public NavController HomeNavController;
    private static ProgressDialog progressDialog;

    public UpdateProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        HomeNavController = Navigation.findNavController(getActivity(), R.id.home_host_fragment);

        update_firstname = view.findViewById(R.id.update_firstname);
        update_lastname = view.findViewById(R.id.update_lastname);
        update_password = view.findViewById(R.id.update_password);
        update_confirm_password = view.findViewById(R.id.update_confirm_password);
        update_firstname_edit_txt = view.findViewById(R.id.update_firstname_edit_txt);
        update_lastname_edit_txt = view.findViewById(R.id.update_lastname_edit_txt);
        update_password_edit_txt = view.findViewById(R.id.update_password_edit_txt);
        update_confirm_password_edit_txt = view.findViewById(R.id.update_confirm_password_edit_txt);
        update_profile_btn = view.findViewById(R.id.update_profile_btn);

        update_profile_btn.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        //Get Current User refernece
        user = mAuth.getCurrentUser();

        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        update_firstname.getEditText().setText(document.getData().get("first_name").toString());
                        update_lastname.getEditText().setText(document.getData().get("last_name").toString());
                        user_type = document.getData().get("user_type").toString();
                        user_email = document.getData().get("email").toString();
                    }
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        if(view == update_profile_btn){
            update_confirm_password.setErrorEnabled(false);
            update_password.setErrorEnabled(false);
            update_firstname.setErrorEnabled(false);
            update_lastname.setErrorEnabled(false);
            String first_name = update_firstname.getEditText().getText().toString().trim();
            String last_name = update_lastname.getEditText().getText().toString().trim();
            String password = update_password.getEditText().getText().toString().trim();
            String confirm_password = update_confirm_password.getEditText().getText().toString().trim();

            if(TextUtils.isEmpty(first_name)){
                update_firstname.setError("Required Fields are missing!");
            }
            else if(TextUtils.isEmpty(last_name)){
                update_lastname.setError("Required Fields are missing!");
            }
            else if(TextUtils.isEmpty(password)){
                update_password.setError("Required Fields are missing!");
            }
            else if(password.length()<6){
                update_password.setError("Password must be 6 characters or long!");
            }
            else if(!password.equals(confirm_password)){
                update_confirm_password.setError("Confirm password doesn't match with password!");
            }
            else {

                update_User(first_name,last_name,password);
            }
        }
    }

    private void update_User(final String first_name, final String last_name, final String password) {
        update_profile_btn.setEnabled(false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        progressDialog.setMessage("Updating Profile");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
        progressDialog.show();
        user.updatePassword(password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            PreferenceData.setUserpassword(context,password);
                            final Map<String, Object> usermap = new HashMap<>();
                            usermap.put("first_name", first_name);
                            usermap.put("last_name", last_name);
                            usermap.put("user_type",user_type);
                            usermap.put("email",user_email);
                            usermap.put("password",password);
                            // Add a new document with user's generated ID in authentication
                            db.collection("users")
                                    .document(user.getUid())
                                    .set(usermap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            Toast.makeText(context,"Profile Updated successfully!",Toast.LENGTH_LONG).show();
                                            update_profile_btn.setEnabled(true);
                                            ((DashboardActivity)getActivity()).setupNavigation();
                                            HomeNavController.navigate(R.id.homeFragment);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    user.updatePassword(PreferenceData.getUserpassword(context))
                                            .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                    }
                                                }
                                            });
                                    Toast.makeText(context,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                    update_profile_btn.setEnabled(true);
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(context, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            update_profile_btn.setEnabled(true);
                        }
                    }
                });
    }
}
