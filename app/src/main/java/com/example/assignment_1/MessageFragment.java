
package com.example.assignment_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MessageFragment extends Fragment {

    public static final String KEY_MESSAGE = "message";
    public static final String KEY_ID = "messageId";

    private ChatWindow chatWindow;

    public MessageFragment() {
        // Required empty constructor
    }

    public MessageFragment(ChatWindow chatWindow) {
        this.chatWindow = chatWindow;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_message,
                container,
                false
        );

        TextView messageText =
                view.findViewById(R.id.messageDetailText);

        TextView messageIdText =
                view.findViewById(R.id.messageDetailId);

        Button deleteButton =
                view.findViewById(R.id.deleteMessageButton);

        Bundle bundle = getArguments();

        if (bundle != null) {

            String message = bundle.getString(KEY_MESSAGE);
            long messageId = bundle.getLong(KEY_ID);

            messageText.setText(message);
            messageIdText.setText("Message ID: " + messageId);

            deleteButton.setOnClickListener(v -> {

                if (chatWindow != null) {

                    // Tablet: delete directly through ChatWindow
                    chatWindow.deleteMessageById(messageId);

                } else {

                    // Phone: return the selected ID to MessageDetails
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(KEY_ID, messageId);

                    requireActivity().setResult(
                            MessageDetails.RESULT_DELETE,
                            resultIntent
                    );

                    requireActivity().finish();
                }
            });
        }

        return view;
    }
}
