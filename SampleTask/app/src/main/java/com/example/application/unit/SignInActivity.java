package com.example.application.unit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    EditText emailEt,passwordEt;
    Button signButton;
    TextView registerTv;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        emailEt = (EditText)findViewById(R.id.signin_email_et);
        passwordEt = (EditText)findViewById(R.id.signin_password_et);
        signButton = (Button) findViewById(R.id.signIn_button);
        registerTv = (TextView) findViewById(R.id.register_tv);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //startActivity(new Intent(this,ProfileActivity.class));
        }

        signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEt.getText().toString();
                String password = passwordEt.getText().toString();
                if(TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(),"Enter email",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password) && !TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"Enter password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password) && TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"Enter email and password",Toast.LENGTH_SHORT).show();
                    return;
                }
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                firebaseAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    //startActivity(new Intent(SignInActivity.this,ProfileActivity.class));
                                    emailEt.setText("");
                                    passwordEt.setText("");
                                    finish();
                                }else{
                                    Toast.makeText(getApplicationContext(),"Login Unsuccessful",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

        registerTv.setPaintFlags(registerTv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        registerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this,RegisterActivity.class));

            }
        });

    }
}
