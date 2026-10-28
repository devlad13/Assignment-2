
package com.example.assignment_1;

import android.os.Bundle;
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

    private ListView listView;
    private EditText textInput;
    private Button sendButton;

    private final ArrayList<String> messages = new ArrayList<>();
    private ChatAdapter messageAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        listView = findViewById(R.id.chatListView);
        textInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        messageAdapter = new ChatAdapter(this);
        listView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(v -> {
            String message = textInput.getText().toString().trim();

            if (!message.isEmpty()) {
                messages.add(message);

                messageAdapter.notifyDataSetChanged();

                textInput.setText("");

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

            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();

            int layoutId;

            if (position % 2 == 0) {
                layoutId = R.layout.chat_row_incoming;
            } else {
                layoutId = R.layout.chat_row_outgoing;
            }

            View result = inflater.inflate(layoutId, parent, false);

            TextView message =
                    result.findViewById(R.id.message_text);

            message.setText(getItem(position));

            return result;
        }

    }
}
