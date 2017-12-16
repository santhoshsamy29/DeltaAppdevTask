package com.example.application.unit;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    EditText registerEmailEt,registerPasswordEt,registerNumberEt,registerNameEt;
    Button signUpButton;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        registerEmailEt = findViewById(R.id.email_register);
        registerNameEt = findViewById(R.id.name_register);
        registerNumberEt = findViewById(R.id.number_register);
        registerPasswordEt = findViewById(R.id.password_register);
        signUpButton = findViewById(R.id.register_button);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser(){

        String email = registerEmailEt.getText().toString().trim();
        String password = registerPasswordEt.getText().toString().trim();

        if(email.isEmpty() && !password.isEmpty()){
            registerEmailEt.setError("Email is required");
            registerEmailEt.requestFocus();
            return;
        }

        if(password.isEmpty() && !email.isEmpty()){
            registerPasswordEt.setError("Password is required");
            registerPasswordEt.requestFocus();
            return;
        }

        if(email.isEmpty() && password.isEmpty()){
            registerEmailEt.setError("Email is required");
            registerPasswordEt.setError("Password is required");
            registerEmailEt.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            registerEmailEt.setError("Enter a valid email id");
            registerEmailEt.requestFocus();
            return;
        }

        if(password.length() < 6){
            registerPasswordEt.setError("Minimum six characters required");
            registerPasswordEt.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(RegisterActivity.this, "Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(RegisterActivity.this, "Email alread registered",
                                        Toast.LENGTH_SHORT).show();
                            } else{
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }

                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }
}
