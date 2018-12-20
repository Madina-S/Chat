package corn.chat.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import corn.chat.R;
import corn.chat.model.Message;
import corn.chat.utilities.ChatRecycViewAdapter;
import corn.chat.utilities.FirebaseMessageEventListener;
import corn.chat.utilities.FirebaseMessageManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

// TODO: 12/12/18 change the last message in the chat list
// TODO: 13/12/18 change the view of the received message if the last message is from the receiver
public class ChatActivity extends AppCompatActivity implements View.OnClickListener, FirebaseMessageEventListener {

    private FirebaseMessageManager firebaseMessageManager;

    private RecyclerView recyclerView;
    private ChatRecycViewAdapter adapter;

    private EditText messageEditText;

    private String TAG = ChatActivity.class.getName();

    private String userLogin;
    private String chatMemberLogin;
    private ArrayList<Message> chat;

    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();
    }

    private void init(){
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        String chatAsString = bundle.getString("chat");
        userLogin = bundle.getString("user_login");
        chatMemberLogin = bundle.getString("chat_member_login");

        initUI();

        try {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Message>>(){}.getType();
            chat = gson.fromJson(chatAsString, type);
            for (Message message : chat){
                Log.i(TAG, message.toString());
            }
        }catch (NullPointerException e){
            chat = new ArrayList<>();
        }
        adapter = new ChatRecycViewAdapter(this, chat);
        recyclerView.setAdapter(adapter);

        firebaseMessageManager = new FirebaseMessageManager(this, this);
        firebaseMessageManager.registerReceiveListener(chatMemberLogin, userLogin);
    }

    private void initUI(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.messages_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        messageEditText = findViewById(R.id.message_edit_text);

        ImageButton btn = findViewById(R.id.send_btn);
        btn.setOnClickListener(this);
        btn = findViewById(R.id.attach_btn);
        btn.setOnClickListener(this);
    }

    // TODO: 17/12/18 set the message ready to be sent and when it is really sent notify the user
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send_btn){
            String messageText = messageEditText.getText().toString().trim();
            if(!messageText.isEmpty()){
                int sequence = 1;
                if(chat.size() > 0)
                    sequence = chat.get(chat.size() - 1).getSequence() + 1;
                Message messageSending = new Message(messageText, Message.TEXT_TYPE, userLogin, sequence, true);
                messageEditText.getText().clear();
                firebaseMessageManager.sendMessage(chatMemberLogin, messageSending);
            }
        }else {
            //open the gallery for choosing the image being sent
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null ) {
            Uri imageUri = data.getData();
            Log.d(TAG, "request code: " + requestCode);
            try {
                int sequence = 1;
                if(chat.size() > 0)
                    sequence = chat.get(chat.size() - 1).getSequence() + 1;
                firebaseMessageManager.sendImage(userLogin, chatMemberLogin, sequence, imageUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sentMessage(Message message) {
        Log.d(TAG, "Sent message\n" + message.getMessage());
        adapter.addMessage(message);
    }

    private boolean isExistsSuchMessage(Message message){
        for (Message m : chat){
            if(m.getSequence() == message.getSequence())
                return true;
        }

        return false;
    }

    // TODO: 17/12/18 notify the user about receiving the message
    @Override
    public void receivedMessage(final Message message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!isExistsSuchMessage(message))
                    adapter.addMessage(message);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
