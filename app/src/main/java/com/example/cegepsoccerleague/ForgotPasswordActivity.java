package com.example.cegepsoccerleague;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton forgot_back_btn;
    private TextInputLayout forgot_email;
    private TextInputEditText forgot_email_edit_txt;
    private MaterialButton forgot_send_link_btn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //Referencing class variables to the XML Layout
        forgot_back_btn = findViewById(R.id.forgot_back_btn);
        forgot_email = findViewById(R.id.forgot_email);
        forgot_email_edit_txt = findViewById(R.id.forgot_email_edit_txt);
        forgot_send_link_btn = findViewById(R.id.forgot_send_link_btn);

        //Creating Reference of Set On Click Listener
        forgot_send_link_btn.setOnClickListener(this);
        forgot_back_btn.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();



    }

    @Override
    public void onClick(View view) {
        if(view==forgot_send_link_btn){
            final String email = forgot_email.getEditText().getText().toString().trim();
            if(!isEmailValid(email)){
                forgot_email.setError("Please enter valid email!");
            }
            else {
                forgot_send_link_btn.setEnabled(false);
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotPasswordActivity.this, "Reset Password link is sent on your email!", Toast.LENGTH_LONG).show();
                                    forgot_send_link_btn.setEnabled(true);
                                    finish();
                                } else {
                                    Toast.makeText(ForgotPasswordActivity.this, "Email not found in database!", Toast.LENGTH_LONG).show();
                                    forgot_send_link_btn.setEnabled(true);
                                }
                            }
                        });

            }
        }
        else if(view==forgot_back_btn){
            finish();
        }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
