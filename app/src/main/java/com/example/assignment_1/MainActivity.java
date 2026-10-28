package com.example.assignment_1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 10;

    private Button myButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "inside onCreate");

        setContentView(R.layout.activity_main);

        Button startChatButton = findViewById(R.id.startChatButton);

        startChatButton.setOnClickListener(v -> {
            Log.i("MainActivity", "User clicked Start Chat");

            Intent intent = new Intent(MainActivity.this, ChatWindow.class);
            startActivity(intent);
        });
        myButton = findViewById(R.id.myButton);

        myButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    ListItemActivity.class
            );

            startActivityForResult(intent, REQUEST_CODE);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "inside onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "MainActivity: inside onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "MainActivity: inside onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "MainActivity: inside onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "MainActivity: inside onDestroy");
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {

            Log.i(TAG, "Returned to MainActivity.onActivityResult");

            if (resultCode == Activity.RESULT_OK && data != null) {

                String messagePassed = data.getStringExtra("Response");

                if (messagePassed != null) {
                    Toast.makeText(
                            this,
                            "ListItemsActivity passed: " + messagePassed,
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        }
    }
}