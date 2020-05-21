package com.example.cegepsoccerleague;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton signup_back_btn;
    private TextInputLayout signup_firstname,signup_lastname,signup_email, signup_password,signup_confirm_password;
    private MaterialButton signup_btn;
    private TextInputEditText firstname_edit_txt, lastname_edit_txt, signup_email_edit_txt, signup_password_edit_txt,signup_confirm_password_edit_txt;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Referencing class variables to the XML Layout
        signup_firstname = findViewById(R.id.signup_firstname);
        signup_lastname = findViewById(R.id.signup_lastname);
        signup_email = findViewById(R.id.signup_email);
        signup_password = findViewById(R.id.signup_password);
        signup_confirm_password = findViewById(R.id.signup_confirm_password);
        firstname_edit_txt = findViewById(R.id.firstname_edit_txt);
        lastname_edit_txt = findViewById(R.id.lastname_edit_txt);
        signup_email_edit_txt = findViewById(R.id.signup_email_edit_txt);
        signup_password_edit_txt = findViewById(R.id.signup_password_edit_txt);
        signup_confirm_password_edit_txt = findViewById(R.id.signup_confirm_password_edit_txt);
        signup_back_btn = findViewById(R.id.signup_back_btn);
        signup_btn = findViewById(R.id.signup_btn);

        //Creating Reference of Set On Click Listener
        signup_btn.setOnClickListener(this);
        signup_back_btn.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onClick(View view) {
        if(view==signup_btn){
            String first_name = signup_firstname.getEditText().getText().toString().trim();
            String last_name = signup_lastname.getEditText().getText().toString().trim();
            String email = signup_email.getEditText().getText().toString().trim();
            String password = signup_password.getEditText().getText().toString().trim();
            String confirm_password = signup_confirm_password.getEditText().getText().toString().trim();

            if(TextUtils.isEmpty(first_name)){
                signup_firstname.setError("Required Fields are missing!");
            }
            else if(TextUtils.isEmpty(last_name)){
                signup_lastname.setError("Required Fields are missing!");
            }
            else if(!isEmailValid(email)){
                signup_email.setError("Please enter valid email!");
            }
            else if(TextUtils.isEmpty(password)){
                signup_password.setError("Required Fields are missing!");
            }
            else if(password.length()<6){
                signup_password.setError("Password must be 6 characters or long!");
            }
            else if(!password.equals(confirm_password)){
                signup_confirm_password.setError("Confirm password doesn't match with password!");
            }
            else {
                sign_up_User(first_name,last_name,email,password);
            }

        }
        else if(view==signup_back_btn){
            finish();
        }

    }

    private void sign_up_User(final String first_name, final String last_name, final String email, String password) {
        signup_btn.setEnabled(false);

        //Creating User in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign Up success, Create User in Cloud Firestore
                            user = mAuth.getCurrentUser();
                            // Create a new user with a first and last name and user_type
                            final Map<String, Object> usermap = new HashMap<>();
                            usermap.put("first_name", first_name);
                            usermap.put("last_name", last_name);
                            usermap.put("email",email);
                            usermap.put("user_type", "LM");

                            // Add a new document with user's generated ID in authentication
                            db.collection("users")
                                    .document(user.getUid())
                                    .set(usermap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(SignUpActivity.this,"Signed Up successfully!",Toast.LENGTH_LONG).show();
                                    signup_btn.setEnabled(true);
                                    mAuth.signOut();
                                    finish();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    user.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                    }
                                                }
                                            });
                                    Toast.makeText(SignUpActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                    signup_btn.setEnabled(true);
                                }
                            });




                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            signup_btn.setEnabled(true);
                        }
                    }
                });
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
