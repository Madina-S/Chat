package corn.chat.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import corn.chat.model.ChatItem;
import corn.chat.model.Message;
import corn.chat.utilities.*;
import corn.chat.R;
import corn.chat.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

// TODO: 10/12/18 no return to LoginActivity
public class ChatListActivity extends AppCompatActivity implements FirebaseChatEventListener, OnItemClickListener {

    private RecyclerView recyclerView;
    private ChatListRecycViewAdapter adapter;

    private PullRefreshLayout refreshLayout;

    private FirebaseChatManager firebaseDatabaseManager;

    private String TAG = ChatListActivity.class.getName();
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        initUI();

        Intent intent = getIntent();
        String userLogin = intent.getStringExtra(PreferenceManager.USER_LOGIN);
        user = new User(userLogin);
        Log.d(TAG, "user's login " + userLogin);

        firebaseDatabaseManager = new FirebaseChatManager(this);
        firebaseDatabaseManager.getAllUsers(userLogin);
    }

    private void initUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.chats_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ChatListRecycViewAdapter(this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        refreshLayout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "refreshing the refreshLayout");
                firebaseDatabaseManager.getAllUsers(user.getLogin());
            }
        });

        refreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_chat_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //clear the user information
        PreferenceManager.with(this).clear();

        //open LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();

        return true;
    }

    @Override
    public void gotAllUser(ArrayList<ChatItem> chats) {
        refreshLayout.setRefreshing(false);
        adapter.update(chats);
        firebaseDatabaseManager.getAllMessagesOfUser(user.getLogin());
    }

    @Override
    public void updateChatEntries(HashMap<String, ArrayList<Message>> conversations) {
        for(String key : conversations.keySet()){
            Collections.sort(conversations.get(key));
            Log.d(TAG, key + " " + conversations.get(key).size());
        }
        user.setChats(conversations);
        adapter.update(user);
    }

    @Override
    public void onClick(ChatItem chat) {
        Log.d(TAG, chat.getChatMember());

        Gson gson = new Gson();
        String chatAsString = gson.toJson(user.getChatByLogin(chat.getChatMember()));

        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("user_login", user.getLogin());
        bundle.putString("chat_member_login", chat.getChatMember());
        bundle.putString("chat", chatAsString);
        intent.putExtras(bundle);

        Log.d(TAG, "length: " + user.getChatByLogin(chat.getChatMember()));

        startActivity(intent);
    }
}
