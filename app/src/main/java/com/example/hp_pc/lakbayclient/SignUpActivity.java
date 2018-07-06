package com.example.hp_pc.lakbayclient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    public EditText cEmail, cPassword, cPassword2;
    public TextView register;

    public FirebaseAuth nAuth;
    public FirebaseAuth.AuthStateListener firebaseAuthListener;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        progressDialog = new ProgressDialog(this);

        nAuth = FirebaseAuth.getInstance();

        cEmail =findViewById(R.id.clientEmail);
        cPassword =findViewById(R.id.clientPass);
        cPassword2 =findViewById(R.id.clientPass2);
        register = findViewById(R.id.btn_register);

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
////                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
////                    startActivity(intent);
////                    finish();
//                    return;
                }
            }
        };

        register.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                final String email = cEmail.getText().toString();
                final String password = cPassword.getText().toString();
                final String password2 = cPassword2.getText().toString();

                if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Enter Email and Password.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter Email Address.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter Password.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(password2)){
                    Toast.makeText(getApplicationContext(), "Password Mismatch.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    nAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this, "Sign Up Error!", Toast.LENGTH_SHORT).show();
                            progressDialog.setMessage("Registration Error!");
                            progressDialog.show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                }
                            }, 2000);

                            }else{
                                //sendverification email
                                sendVerificationEmail();

                                progressDialog.setMessage("Registering Your Account");
                                progressDialog.show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                    }
                                }, 4000);

                                String user_id = nAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("clients").child(user_id);
                                current_user_db.setValue(true);
                                Intent intent = new Intent(SignUpActivity.this, RegistrationActivity.class);
                                startActivity(intent);

                            }
                        }
                    });
                }
            }
        });
    }


    public void sendVerificationEmail(){
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    if (user!=null){
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                        }else {
                            Toast.makeText(SignUpActivity.this, "couldn't send email verification", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }




    @Override
    protected void onStart() {
        super.onStart();
        nAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        nAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
