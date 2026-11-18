
package com.example.assignment_1;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ChatWindow extends AppCompatActivity {

    private static final String ACTIVITY_NAME = "ChatWindow";

    private ListView listView;
    private EditText textInput;
    private Button sendButton;

    private final ArrayList<String> messages = new ArrayList<>();

    private ChatAdapter messageAdapter;

    private ChatDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(ACTIVITY_NAME, "Inside onCreate");

        setContentView(R.layout.activity_chat_window);

        listView = findViewById(R.id.chatListView);
        textInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        dbHelper = new ChatDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        Log.i(ACTIVITY_NAME, "Database opened successfully");

        Cursor cursor = db.query(
                ChatDatabaseHelper.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Display Cursor information
        Log.i(ACTIVITY_NAME,
                "Cursor's column count = " + cursor.getColumnCount());

        // Print each column name
        for (int i = 0; i < cursor.getColumnCount(); i++) {

            Log.i(ACTIVITY_NAME,
                    "Column name: " + cursor.getColumnName(i));
        }

        if (cursor.moveToFirst()) {

            do {

                int messageColumnIndex =
                        cursor.getColumnIndex(
                                ChatDatabaseHelper.KEY_MESSAGE
                        );

                String message =
                        cursor.getString(messageColumnIndex);

                Log.i(ACTIVITY_NAME, "SQL MESSAGE: " + message);

                messages.add(message);

            } while (cursor.moveToNext());
        }

        // Close Cursor
        cursor.close();

        // Initialize adapter after loading saved messages
        messageAdapter = new ChatAdapter(this);
        listView.setAdapter(messageAdapter);

        // Send button click listener
        sendButton.setOnClickListener(v -> {

            String message = textInput.getText().toString().trim();

            if (!message.isEmpty()) {

                messages.add(message);

                ContentValues values = new ContentValues();

                values.put(
                        ChatDatabaseHelper.KEY_MESSAGE,
                        message
                );

                long result = db.insert(
                        ChatDatabaseHelper.TABLE_NAME,
                        null,
                        values
                );

                if (result != -1) {

                    Log.i(ACTIVITY_NAME,
                            "Message saved to database: " + message);

                } else {

                    Log.e(ACTIVITY_NAME,
                            "Failed to save message");
                }

                // Refresh ListView
                messageAdapter.notifyDataSetChanged();

                // Clear input field
                textInput.setText("");

                // Scroll to latest message
                listView.setSelection(messages.size() - 1);
            }
        });
    }


    private class ChatAdapter extends ArrayAdapter<String> {

        public ChatAdapter(android.content.Context ctx) {
            super(ctx, 0);
        }

        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public String getItem(int position) {
            return messages.get(position);
        }

        @Override
        public View getView(
                int position,
                View convertView,
                ViewGroup parent) {

            LayoutInflater inflater =
                    ChatWindow.this.getLayoutInflater();

            int layoutId;

            if (position % 2 == 0) {
                layoutId = R.layout.chat_row_incoming;
            } else {
                layoutId = R.layout.chat_row_outgoing;
            }

            View result = inflater.inflate(
                    layoutId,
                    parent,
                    false
            );

            TextView message =
                    result.findViewById(R.id.message_text);

            message.setText(getItem(position));

            return result;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (dbHelper != null) {

            dbHelper.close();

            Log.i(ACTIVITY_NAME, "Database closed");
        }
    }
}
