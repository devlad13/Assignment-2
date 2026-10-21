package com.example.assignment_1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ListItemActivity extends AppCompatActivity {

    private static final String TAG = "ListItemsActivity";
    private static final int CAMERA_REQUEST = 20;

    private ImageButton cameraButton;
    private Switch mySwitch;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "inside onCreate");

        setContentView(R.layout.activity_list_item);

        cameraButton = findViewById(R.id.cameraButton);
        mySwitch = findViewById(R.id.mySwitch);
        checkBox = findViewById(R.id.checkBox);

        cameraButton.setOnClickListener(v -> {

            Intent cameraIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE
            );

            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                print(getString(R.string.camera_unavailable));
            }
        });

        mySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                Toast.makeText(
                        ListItemActivity.this,
                        getString(R.string.switch_on),
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                Toast.makeText(
                        ListItemActivity.this,
                        getString(R.string.switch_off),
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                showExitDialog();
            }
        });
    }

    private void showExitDialog() {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(ListItemActivity.this);

        builder.setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_message)

                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int id
                            ) {

                                Intent resultIntent = new Intent();

                                resultIntent.putExtra(
                                        "Response",
                                        getString(R.string.response_message)
                                );

                                setResult(
                                        Activity.RESULT_OK,
                                        resultIntent
                                );

                                finish();
                            }
                        })

                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int id
                            ) {
                                checkBox.setChecked(false);
                            }
                        })

                .show();
    }

    public void print(String message) {

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST) {

            if (resultCode == Activity.RESULT_OK && data != null) {

                Bundle extras = data.getExtras();

                if (extras != null) {

                    Object imageObject = extras.get("data");

                    if (imageObject instanceof Bitmap) {

                        Bitmap imageBitmap = (Bitmap) imageObject;

                        cameraButton.setImageBitmap(imageBitmap);

                        print(getString(R.string.photo_captured));
                    }
                }
            } else {
                Log.i(TAG, "Camera operation cancelled");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "ListItemsActivity: inside onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "ListItemsActivity: inside onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "ListItemsActivity: inside onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "ListItemsActivity: inside onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ListItemsActivity: inside onDestroy");
    }
}