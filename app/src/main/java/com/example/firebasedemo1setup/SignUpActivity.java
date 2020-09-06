package com.example.firebasedemo1setup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.firebasedemo1setup.Data.UserFormat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private String name, email, password, confirmPassword;

    private TextView tvLogin;
    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private Button buttonSignUp;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ids();

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("USERS");
        progressDialog = new ProgressDialog(this);

    }

    private void ids() {
        tvLogin = findViewById(R.id.tvLogin);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword=findViewById(R.id.etConfirmPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etName.getText().toString().trim();
                email = etEmail.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                confirmPassword=etConfirmPassword.getText().toString().trim();
                if(name.equals("") || email.equals("") || password.equals("") || confirmPassword.equals(""))
                {
                    Toast.makeText(SignUpActivity.this, "Please Fill all the Details!!!", Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(confirmPassword))
                {
                    Toast.makeText(SignUpActivity.this, "Passwords does not match!!!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    registerUser();
                }
            }
        });
    }

    private void registerUser()
    {
        progressDialog.setMessage("Please Wait....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
        {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            FirebaseUser currentUser=firebaseAuth.getCurrentUser();
                            String uid= currentUser.getUid();
                            UserFormat user = new UserFormat(name,email,uid);
                            databaseReference.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(SignUpActivity.this, "Successfully Registered.", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(getApplicationContext(),ShowData.class));
                                        finish();
                                    }
                                }
                            });
                        }
                        else
                            Toast.makeText(SignUpActivity.this, "Registration Failed!!!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}