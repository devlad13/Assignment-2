package com.example.assignment_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText loginName;
    private EditText passwordInput;
    private Button loginButton;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "inside onCreate");

        setContentView(R.layout.activity_login);

        loginName = findViewById(R.id.loginName);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

        sharedPreferences = getSharedPreferences(
                "LoginPreferences",
                MODE_PRIVATE
        );

        String savedEmail = sharedPreferences.getString(
                "DefaultEmail",
                "email@domain.com"
        );

        loginName.setText(savedEmail);

        loginButton.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {

        String email = loginName.getText().toString().trim();
        String password = passwordInput.getText().toString();

        if (email.isEmpty()) {
            loginName.setError(getString(R.string.email_required));
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginName.setError(getString(R.string.invalid_email));
            return;
        }

        if (password.trim().isEmpty()) {
            passwordInput.setError(getString(R.string.password_required));
            return;
        }

        sharedPreferences.edit()
                .putString("DefaultEmail", email)
                .apply();

        Intent intent = new Intent(
                LoginActivity.this,
                MainActivity.class
        );

        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "loginActivity: inside onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "loginActivity: inside onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "loginActivity: inside onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "loginActivity: inside onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "loginActivity: inside onDestroy");
    }
}