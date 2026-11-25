
package com.example.assignment_1;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class MessageDetails extends AppCompatActivity {

    public static final int RESULT_DELETE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message_details);

        // Get selected message information
        String message = getIntent().getStringExtra(
                MessageFragment.KEY_MESSAGE
        );

        long messageId = getIntent().getLongExtra(
                MessageFragment.KEY_ID,
                -1
        );

        // Create Bundle for Fragment
        Bundle bundle = new Bundle();
        bundle.putString(MessageFragment.KEY_MESSAGE, message);
        bundle.putLong(MessageFragment.KEY_ID, messageId);

        // Create Fragment
        MessageFragment fragment = new MessageFragment(null);
        fragment.setArguments(bundle);

        // Add Fragment to Activity
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();

        transaction.replace(
                R.id.messageDetailsFrame,
                fragment
        );

        transaction.commit();
    }
}
