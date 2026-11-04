
package com.example.assignment_1;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class TestToolbar extends AppCompatActivity {

    private String snackbarMessage = "You selected item 1";

    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_toolbar);

        rootView = findViewById(R.id.toolbarRoot);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(v ->
                Snackbar.make(
                        rootView,
                        "Welcome to my Toolbar assignment!",
                        Snackbar.LENGTH_LONG
                ).show()
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_one) {

            Snackbar.make(
                    rootView,
                    snackbarMessage,
                    Snackbar.LENGTH_LONG
            ).show();

            return true;

        } else if (id == R.id.action_two) {

            showExitDialog();
            return true;

        } else if (id == R.id.action_three) {

            showCustomDialog();
            return true;

        } else if (id == R.id.action_about) {

            Toast.makeText(
                    this,
                    "Version 1.0, by Your Name",
                    Toast.LENGTH_LONG
            ).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showExitDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Do you want to go back?")
                .setMessage("Are you sure you want to leave this activity?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", (dialog, which) ->
                        dialog.dismiss())
                .show();
    }

    private void showCustomDialog() {

        View dialogView = getLayoutInflater().inflate(
                R.layout.custom_message_dialog,
                null
        );

        EditText messageInput =
                dialogView.findViewById(R.id.newMessageInput);

        new AlertDialog.Builder(this)
                .setTitle("Enter New Message")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> {

                    String newMessage =
                            messageInput.getText().toString().trim();

                    if (!newMessage.isEmpty()) {

                        snackbarMessage = newMessage;

                        Toast.makeText(
                                this,
                                "New message saved",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        Toast.makeText(
                                this,
                                "Please enter a message",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) ->
                        dialog.dismiss())
                .show();
    }
}
