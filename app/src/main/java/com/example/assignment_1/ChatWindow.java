
package com.example.assignment_1;

import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class ChatWindow extends AppCompatActivity {

    private static final String ACTIVITY_NAME = "ChatWindow";

    private static final int REQUEST_DETAILS = 10;

    private ListView listView;
    private EditText textInput;
    private Button sendButton;

    private final ArrayList<String> messages = new ArrayList<>();

    private ChatAdapter messageAdapter;

    private ChatDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    // Cursor is now a class-level variable
    private Cursor cursor;

    // Tablet detection
    private boolean isTablet;
    private FrameLayout detailsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(ACTIVITY_NAME, "Inside onCreate");

        setContentView(R.layout.activity_chat_window);

        listView = findViewById(R.id.chatListView);
        textInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        // Check whether the tablet FrameLayout exists
        detailsContainer = findViewById(
                R.id.messageDetailsContainer
        );

        isTablet = detailsContainer != null;

        Log.i(ACTIVITY_NAME, "Tablet layout: " + isTablet);

        // Open database
        dbHelper = new ChatDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        Log.i(ACTIVITY_NAME, "Database opened successfully");

        // Load saved messages from database
        refreshChatList();

        // Initialize adapter
        messageAdapter = new ChatAdapter(this);
        listView.setAdapter(messageAdapter);

        // Send button click listener
        sendButton.setOnClickListener(v -> {

            String message = textInput.getText().toString().trim();

            if (!message.isEmpty()) {

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

                    Log.i(
                            ACTIVITY_NAME,
                            "Message saved to database: " + message
                    );

                    // Reload messages and database IDs
                    refreshChatList();

                    messageAdapter.notifyDataSetChanged();

                    // Scroll to latest message
                    listView.setSelection(messages.size() - 1);

                } else {

                    Log.e(
                            ACTIVITY_NAME,
                            "Failed to save message"
                    );
                }

                textInput.setText("");
            }
        });

        // Message selection listener
        listView.setOnItemClickListener(
                (parent, view, position, id) -> {

                    String selectedMessage =
                            messageAdapter.getItem(position);

                    // ID comes from getItemId()
                    long selectedMessageId = id;

                    Log.i(
                            ACTIVITY_NAME,
                            "Selected message ID: " + selectedMessageId
                    );

                    // Create Bundle
                    Bundle bundle = new Bundle();

                    bundle.putString(
                            MessageFragment.KEY_MESSAGE,
                            selectedMessage
                    );

                    bundle.putLong(
                            MessageFragment.KEY_ID,
                            selectedMessageId
                    );

                    if (isTablet) {

                        // TABLET: Display Fragment on the right

                        MessageFragment fragment =
                                new MessageFragment(this);

                        fragment.setArguments(bundle);

                        FragmentTransaction transaction =
                                getSupportFragmentManager()
                                        .beginTransaction();

                        transaction.replace(
                                R.id.messageDetailsContainer,
                                fragment
                        );

                        transaction.commit();

                    } else {

                        // PHONE: Open MessageDetails Activity

                        Intent intent = new Intent(
                                ChatWindow.this,
                                MessageDetails.class
                        );

                        intent.putExtras(bundle);

                        startActivityForResult(
                                intent,
                                REQUEST_DETAILS
                        );
                    }
                }
        );
    }

    // Load or reload messages from SQLite
    private void refreshChatList() {

        messages.clear();

        // Close previous Cursor before querying again
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        // Query database including message ID and message text
        cursor = db.query(
                ChatDatabaseHelper.TABLE_NAME,
                new String[]{
                        ChatDatabaseHelper.KEY_ID,
                        ChatDatabaseHelper.KEY_MESSAGE
                },
                null,
                null,
                null,
                null,
                null
        );

        Log.i(
                ACTIVITY_NAME,
                "Cursor column count = " + cursor.getColumnCount()
        );

        // Print column names
        for (int i = 0; i < cursor.getColumnCount(); i++) {

            Log.i(
                    ACTIVITY_NAME,
                    "Column name: " + cursor.getColumnName(i)
            );
        }

        // Load saved messages
        if (cursor.moveToFirst()) {

            do {

                int messageColumnIndex = cursor.getColumnIndex(
                        ChatDatabaseHelper.KEY_MESSAGE
                );

                String message = cursor.getString(
                        messageColumnIndex
                );

                messages.add(message);

                Log.i(
                        ACTIVITY_NAME,
                        "SQL MESSAGE: " + message
                );

            } while (cursor.moveToNext());
        }

        Log.i(
                ACTIVITY_NAME,
                "Total messages loaded: " + messages.size()
        );
    }

    // Delete a message using its database ID
    public void deleteMessageById(long messageId) {

        Log.i(
                ACTIVITY_NAME,
                "Deleting message ID: " + messageId
        );

        int deletedRows = db.delete(
                ChatDatabaseHelper.TABLE_NAME,
                ChatDatabaseHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(messageId)}
        );

        Log.i(
                ACTIVITY_NAME,
                "Deleted rows: " + deletedRows
        );

        if (deletedRows > 0) {

            // Reload messages from database
            refreshChatList();

            messageAdapter.notifyDataSetChanged();

            // Remove Fragment from tablet
            if (isTablet) {

                Fragment fragment =
                        getSupportFragmentManager()
                                .findFragmentById(
                                        R.id.messageDetailsContainer
                                );

                if (fragment != null) {

                    getSupportFragmentManager()
                            .beginTransaction()
                            .remove(fragment)
                            .commit();
                }
            }
        }
    }

    // Receive deletion result from MessageDetails Activity
    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode == REQUEST_DETAILS &&
                resultCode == MessageDetails.RESULT_DELETE &&
                data != null) {

            long messageId = data.getLongExtra(
                    MessageFragment.KEY_ID,
                    -1
            );

            if (messageId != -1) {

                deleteMessageById(messageId);
            }
        }
    }

    // Chat Adapter
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

        // Return actual database ID for each message
        @Override
        public long getItemId(int position) {

            if (cursor != null &&
                    !cursor.isClosed() &&
                    cursor.moveToPosition(position)) {

                int idColumnIndex = cursor.getColumnIndex(
                        ChatDatabaseHelper.KEY_ID
                );

                return cursor.getLong(idColumnIndex);
            }

            return -1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
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

        // Close Cursor
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        // Close database helper
        if (dbHelper != null) {

            dbHelper.close();

            Log.i(
                    ACTIVITY_NAME,
                    "Database closed"
            );
        }
    }
}
