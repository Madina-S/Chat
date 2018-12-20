package corn.chat.utilities;

import android.support.annotation.NonNull;
import android.util.Log;
import com.google.firebase.database.*;
import corn.chat.model.ChatItem;
import corn.chat.model.Message;

import java.util.ArrayList;
import java.util.HashMap;

public class FirebaseChatManager {
    private DatabaseReference root;

    private FirebaseChatEventListener chatListener;

    private static String TAG = FirebaseChatManager.class.getName();

    public FirebaseChatManager() {
        root = FirebaseDatabase.getInstance().getReference();
    }

    public FirebaseChatManager(FirebaseChatEventListener listener) {
        this();
        this.chatListener = listener;
    }

    public void getAllUsers(final String login) {
        DatabaseReference userTable = root.child(FirebaseConfig.USER_KEY);

        userTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<ChatItem> chats = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String userName = ds.child(FirebaseConfig.USER_LOGIN_KEY).getValue().toString();
                    ChatItem chat = new ChatItem(userName);
                    if (!userName.equals(login))
                        chats.add(chat);
                }
                chatListener.gotAllUser(chats);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getAllMessagesOfUser(final String login) {
        DatabaseReference chatTable = root.child(FirebaseConfig.CHAT_KEY);
        chatTable.child(login)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HashMap<String, ArrayList<Message>> conversationMap = new HashMap<>();
                        if(dataSnapshot.exists()){
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                ArrayList<Message> cons = new ArrayList<>();
                                for (DataSnapshot dsInner : ds.getChildren()){
                                    Message c = dsInner.getValue(Message.class);
                                    c.setSent(!ds.getKey().equals(c.getSender()));
                                    cons.add(c);
                                }
                                conversationMap.put(ds.getKey(), cons);
                            }
                        }


                        for (String key : conversationMap.keySet()){
                            Log.d(TAG, "---------------\n" + key);
                            for (Message message : conversationMap.get(key)){
                                Log.d(TAG, message.toString());
                            }
                        }

                        chatListener.updateChatEntries(conversationMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

}
