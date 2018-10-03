package com.example.rohit.asuchatbot.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rohit.asuchatbot.R;
import com.example.rohit.asuchatbot.adapters.MessageListAdapter;
import com.example.rohit.asuchatbot.helpers.DialogFlowRequestTask;
import com.example.rohit.asuchatbot.interfaces.ResponseInterface;
import com.example.rohit.asuchatbot.model.ChatMessage;
import com.mapzen.speakerbox.Speakerbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.rohit.asuchatbot.constants.StringConstants.NO_INTERNET;
import static com.example.rohit.asuchatbot.constants.StringConstants.NO_QUESTION;

public class MainActivity extends AppCompatActivity implements ResponseInterface {

    @BindView(R.id.button_chatbox_send)
    Button sendBtn;

    @BindView(R.id.edittext_chatbox)
    TextView message;

    ImageButton voiceBtn;
    Speakerbox speakerbox;
    final List<ChatMessage> messageList = new ArrayList<>();
    String uniqueID = UUID.randomUUID().toString();
    private MessageListAdapter mMessageAdapter;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        speakerbox = new Speakerbox(getApplication());
        speakerbox.enableVolumeControl(this);
        initUI();
        setListeners();
    }


    private void initUI() {
        RecyclerView mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        messageList.add(new ChatMessage("Hi, how may I help you?",2));
        mMessageAdapter = new MessageListAdapter(this, messageList);
        mMessageRecycler.setAdapter(mMessageAdapter);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
    }


    private void setListeners() {
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!message.getText().toString().isEmpty()) {
                    if (isOnline()) {
                        new DialogFlowRequestTask(MainActivity.this).execute(message.getText().toString(), uniqueID);
                        messageList.add(new ChatMessage(message.getText().toString(), 1));
                        mMessageAdapter.notifyDataSetChanged();
                        message.setText("");
                    } else {
                        Snackbar snackbar = Snackbar.make(v, NO_INTERNET, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                } else {
                    Snackbar snackbar = Snackbar.make(v, NO_QUESTION, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
        voiceBtn = (ImageButton) findViewById(R.id.button_voice_input);
        voiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }

    @Override
    public void updateResponse(String asyncResult) {
        messageList.add(new ChatMessage(asyncResult, 2));
        mMessageAdapter.notifyDataSetChanged();
        speakerbox.play(asyncResult);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (isOnline()) {
                        new DialogFlowRequestTask(MainActivity.this).execute(result.get(0), uniqueID);
                        messageList.add(new ChatMessage(result.get(0), 1));
                        mMessageAdapter.notifyDataSetChanged();
                        message.setText("");
                    }
                }
                break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMessageAdapter.notifyDataSetChanged();
    }

}
