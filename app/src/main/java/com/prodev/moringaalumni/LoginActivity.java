package com.prodev.moringaalumni;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    //views


    EditText mEmailEt, mPasswordEt;
    TextView notHaveAccntTv;
    Button mLoginBtn;


//Declare an instance of FirebaseAuth
private FirebaseAuth mAuth;

        ProgressDialog pd;



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Actionbar and its title
        ActionBar actionBar= getSupportActionBar();
        actionBar.setTitle("Login");
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //In the onCreate() method, initialize the FirebaseAuth instance.
        mAuth = FirebaseAuth.getInstance();


        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        notHaveAccntTv = findViewById(R.id.nothave_accountTv);
        mLoginBtn = findViewById(R.id.loginBtn);

        //login Button click
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //input data
                        String email = mEmailEt.getText().toString();
                        String passw = mPasswordEt.getText().toString().trim();
                        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

                            //invalid email_pattern set error;
                            mEmailEt.setError("Invalid Email");
                            mEmailEt.setFocusable(true);

                        } else {
                            //valid email
                            loginUser(email, passw);
                        }
                    }
                });
        //not have account textview click
            notHaveAccntTv.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    startActivity (new Intent(LoginActivity.this, RegisterActivity.class));
                }
            });
            //init progress dialog

            pd = new ProgressDialog(this);
            pd.setMessage("Logging In...");



    }

    private void loginUser(String email, String passw) {
        pd.show();
        mAuth.signInWithEmailAndPassword(email, passw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            pd.dismiss();
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                }).addOnFailureListener(new OnFailureListener(){


            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                //error, get and show error message
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed(); //go to previous activity
        return super.onSupportNavigateUp();
    }
}