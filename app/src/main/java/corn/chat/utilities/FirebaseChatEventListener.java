package corn.chat.utilities;

import corn.chat.model.ChatItem;
import corn.chat.model.Message;

import java.util.ArrayList;
import java.util.HashMap;

public interface FirebaseChatEventListener {

    void gotAllUser(ArrayList<ChatItem> chats);

    // TODO: 10/12/18 get last message sent
    void updateChatEntries(HashMap<String, ArrayList<Message>> conversations);
}
