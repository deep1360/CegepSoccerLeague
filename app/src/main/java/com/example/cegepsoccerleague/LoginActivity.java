package com.example.cegepsoccerleague;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextInputLayout login_email, login_password;
    private MaterialButton login_btn,signup_on_login_btn,guest_on_login_btn;
    private TextView login_forgot_pass_txt;
    private TextInputEditText email_edit_txt, password_edit_txt;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Referencing class variables to the XML Layout
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_btn = findViewById(R.id.login_btn);
        login_forgot_pass_txt = findViewById(R.id.login_forgot_pass_txt);
        email_edit_txt = findViewById(R.id.email_edit_txt);
        password_edit_txt = findViewById(R.id.password_edit_txt);
        signup_on_login_btn = findViewById(R.id.signup_on_login_btn);
        guest_on_login_btn = findViewById(R.id.guest_on_login_btn);

        //Creating Reference of Set On Click Listener and connect to the OnCLick method
        login_btn.setOnClickListener(this);
        signup_on_login_btn.setOnClickListener(this);
        guest_on_login_btn.setOnClickListener(this);
        login_forgot_pass_txt.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    public void onClick(View view) {
        if (view == login_btn) {
            //getting value of email and password layout
            String email = login_email.getEditText().getText().toString().trim();
            String password = login_password.getEditText().getText().toString().trim();
            if(!isEmailValid(email)){
                login_email.setError("Please enter valid email!");
            }
            else if(TextUtils.isEmpty(password)){
                login_password.setError("Please enter valid password!");
            }
            else {
                login_user(email,password);
            }
        }
        else if (view == signup_on_login_btn) {
            startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
        }
        else if (view == guest_on_login_btn) {
            startActivity(new Intent(LoginActivity.this,DashboardActivity.class));
        }
        else if (view == login_forgot_pass_txt) {
            startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
        }
    }

    private void login_user(final String email, final String password) {
        login_btn.setEnabled(false);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();
                            login_btn.setEnabled(true);
                            PreferenceData.setUserpassword(getApplicationContext(),password);
                            PreferenceData.setUseremail(getApplicationContext(),email);
                            Toast.makeText(LoginActivity.this, "Login Successfull!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this,DashboardActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            login_btn.setEnabled(true);
                        }
                    }
                });

    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
