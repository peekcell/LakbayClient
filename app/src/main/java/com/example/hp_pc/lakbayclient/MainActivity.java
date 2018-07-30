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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onPause() {
        super.onPause();

        progressDialog.dismiss();
    }

    public Button signin;
    public EditText cEmail, cPassword;
    public TextView reg;

    public FirebaseAuth nAuth;
    public DatabaseReference isAuth;
    public FirebaseAuth.AuthStateListener firebaseAuthListener;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        progressDialog = new ProgressDialog(this);

        nAuth = FirebaseAuth.getInstance();
        isAuth = FirebaseDatabase.getInstance().getReference().child("clients");




        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                final Handler handler = new Handler();
//                progressDialog.show();
//                handler.postDelayed(new Runnable() {
//                    public void run() {
//                        progressDialog.dismiss();
//                    }
//                }, 2000);
                if(user!=null){
                    try{
                        if(user.isEmailVerified()){
                            progressDialog.setMessage("Signing In Your Account");
                            progressDialog.show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 3000);

                        }
                    }catch (NullPointerException e){
                        Toast.makeText(MainActivity.this, "NullPointerException: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
//                    try{
//                        if(user.isEmailVerified()){
//                            progressDialog.setMessage("Signing In Your Account");

//                            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
//                            startActivity(intent);
//                            finish();
//                            return;
//                        }else{
////                            progressDialog.setMessage("Please Verify Your Email");
//
//                            nAuth.signOut();
//                        }
//                    }catch (NullPointerException e){
//                        Toast.makeText(MainActivity.this, "NullPointerException: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
//                    startActivity(intent);

                }
            }
        };

        cEmail =findViewById(R.id.clientEmail);
        cPassword =findViewById(R.id.clientPass);
        signin = findViewById(R.id.btn_signin);
//        register = findViewById(R.id.reg);





        signin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {


                final String email = cEmail.getText().toString();
                final String password = cPassword.getText().toString();
//                final Handler handler = new Handler();
//                progressDialog.show();
//                handler.postDelayed(new Runnable() {
//                    public void run() {
//                        progressDialog.dismiss();
//                    }
//                }, 2000);

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
                }else {
                    nAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            final FirebaseUser user = nAuth.getCurrentUser();

                            final boolean[] isUserTrue = new boolean[1];
                            final boolean b1 = false;

//                            DatabaseReference isValidUser = FirebaseDatabase.getInstance().getReference().child("clients");

//                            isValidUser.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
////                                    isUserTrue[0] = (boolean) dataSnapshot.child("is_user").getValue();
////                                    Toast.makeText(MainActivity.this, "" + isUserTrue[0], Toast.LENGTH_SHORT).show();
//                                    isUserTrue[0] = dataSnapshot.child(user.getUid()).exists();
//                                    Toast.makeText(MainActivity.this, "" + isUserTrue[0], Toast.LENGTH_SHORT).show();
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });

                            ValueEventListener isValidUser = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    isUserTrue[0] = dataSnapshot.child(user.getUid()).exists();

                                    Toast.makeText(MainActivity.this, "" + isUserTrue[0], Toast.LENGTH_SHORT).show();

                                    if(isUserTrue[0]) {
                                        try{
                                            if(user.isEmailVerified()){
                                                progressDialog.setMessage("Signing In Your Account");
                                                progressDialog.show();
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressDialog.dismiss();
                                                        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }, 3000);

                                            }else{
//                                        Toast.makeText(MainActivity.this, "Email is not verified \n check your email inbox.", Toast.LENGTH_SHORT).show();
//                                        progressDialog.setMessage("Please Verify Your Email");
                                                progressDialog.setMessage("Please Verify Your Email");
                                                progressDialog.show();
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressDialog.dismiss();
                                                        nAuth.signOut();
                                                    }
                                                }, 3000);

                                            }
                                        }catch (NullPointerException e){
                                            Toast.makeText(MainActivity.this, "NullPointerException: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        progressDialog.setMessage("Sign In Error!");
                                        progressDialog.show();
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                            }
                                        }, 3000);

//                                Toast.makeText(MainActivity.this, "signInFailed: " + isUserTrue[0],
//                                        Toast.LENGTH_SHORT).show();

                                        nAuth.signOut();
                                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };

                            isAuth.addListenerForSingleValueEvent(isValidUser);

                            if (!task.isSuccessful()) {
                                progressDialog.setMessage("Sign In Error!");
                                progressDialog.show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                    }
                                }, 3000);

                                Toast.makeText(MainActivity.this, "task not successful",
                                        Toast.LENGTH_SHORT).show();

                            }
//                            else{
//
//                            }
//                            if(!task.isSuccessful()){
////                            Toast.makeText(MainActivity.this, "Sign In Error!", Toast.LENGTH_SHORT).show();
//                                progressDialog.setMessage("Sign In Error!");
//                                progressDialog.show();
//                                Handler handler = new Handler();
//                                handler.postDelayed(new Runnable() {
//                                    public void run() {
//                                        progressDialog.dismiss();
//                                    }
//                                }, 2000);
//                            }else {
//
//                                try{
//
//                                    if (user.isEmailVerified()){
//                                        progressDialog.setMessage("Signing In Your Account");
//                                        progressDialog.show();
//                                        Handler handler = new Handler();
//                                        handler.postDelayed(new Runnable() {
//                                            public void run() {
//                                                progressDialog.dismiss();
//                                            }
//                                        }, 2000);
//                                        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
//                                        startActivity(intent);
//                                    }else {
//                                        nAuth.signOut();
//                                        progressDialog.setMessage("Please Verify Your Email");
//                                        progressDialog.show();
//                                        Handler handler = new Handler();
//                                        handler.postDelayed(new Runnable() {
//                                            public void run() {
//                                                progressDialog.dismiss();
//                                            }
//                                        }, 2000);
//
//                                    }
//                                }catch (NullPointerException e){
//                                    Toast.makeText(MainActivity.this, "NullPointerException: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//
//
//                                }
//                            }
                        }
                    });
                }









            }
        });




//        register.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//
//                final String email = cEmail.getText().toString();
//                final String password = cPassword.getText().toString();
//
//                if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
//                    Toast.makeText(getApplicationContext(), "Enter Email and Password.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (TextUtils.isEmpty(email)) {
//                    Toast.makeText(getApplicationContext(), "Enter Email Address.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if (TextUtils.isEmpty(password)) {
//                    Toast.makeText(getApplicationContext(), "Enter Password.", Toast.LENGTH_SHORT).show();
//                    return;
//                } else {
//                    nAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if(!task.isSuccessful()){
//                                Toast.makeText(MainActivity.this, "Sign Up Error!", Toast.LENGTH_SHORT).show();
//                            progressDialog.setMessage("Registration Error!");
//                            progressDialog.show();
//                            Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                public void run() {
//                                    progressDialog.dismiss();
//                                }
//                            }, 2000);
//
//                            }else{
//                                //sendverification email
//                                sendVerificationEmail();
//
//                                progressDialog.setMessage("Registering Your Account");
//                                progressDialog.show();
//                                Handler handler = new Handler();
//                                handler.postDelayed(new Runnable() {
//                                    public void run() {
//                                        progressDialog.dismiss();
//                                    }
//                                }, 4000);
//
//                                String user_id = nAuth.getCurrentUser().getUid();
//                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("clients").child(user_id);
//                                current_user_db.setValue(true);
//                                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
//                                startActivity(intent);
//
//                            }
//                        }
//                    });
//
//
//                }
//
//            }
//
//
//        });



    }

    public void reg(View view){
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
    }
//    public void checkUser() {
//        FirebaseUser userIn = FirebaseAuth.getInstance().getCurrentUser();
//        if(userIn != null ) {
//            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
//            startActivity(intent);
//        }
//    }



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


