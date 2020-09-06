package com.example.firebasedemo1setup;

import android.app.ProgressDialog;
import android.content.Context;
import  android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextView tvSignUp, tvForgotPassword;
    private EditText etEmail, etPassword;
    private Button buttonLogin;

    private SharedPreferences.Editor editor;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginscreen);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);

        editor = getSharedPreferences("FireNotesData", Context.MODE_PRIVATE).edit();

        firebaseAuth = FirebaseAuth.getInstance();

        ids();
    }

    private void ids() {

        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp = findViewById(R.id.tvNew);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonLogin.startAnimation(animation);
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if(email.equals("") || password.equals(""))
                    Toast.makeText(LoginActivity.this, "Please Fill all the Details", Toast.LENGTH_SHORT).show();
                else
                    loginUser(email,password);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvForgotPassword.startAnimation(animation);
                String email = etEmail.getText().toString().trim();
                if(email.equals(""))
                {
                    Toast.makeText(LoginActivity.this, "Enter Email", Toast.LENGTH_LONG).show();
                    etEmail.setError("Required");
                }
                else
                    firebaseAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                        Toast.makeText(LoginActivity.this, "Check Email for RESET link.", Toast.LENGTH_LONG).show();
                                    else
                                        Toast.makeText(LoginActivity.this, "Error Resetting password", Toast.LENGTH_LONG).show();
                                }
                            });
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvSignUp.startAnimation(animation);
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
            }
        });
    }
    private void loginUser(String email, String password)
    {
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            editor.putBoolean("LoginStatus",true);
                            editor.putString("UID", Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
                            editor.commit();
                            progressDialog.dismiss();
                            startActivity(new Intent(LoginActivity.this,ShowData.class));
                            finish();
                        }
                        else
                        {
                            editor.putBoolean("LoginStatus",false);
                            editor.commit();
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Logging Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}