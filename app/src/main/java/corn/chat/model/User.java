package corn.chat.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

// TODO: 08/12/18 login = email should be splitted
public class User implements Serializable {
    private String login;
    private String password;

    private HashMap<String, ArrayList<Message>> chats;

    public User() {}

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public User(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setChats(HashMap<String, ArrayList<Message>> chats) {
        if(this.chats != null){
            this.chats.clear();
            this.chats.putAll(chats);
        }else{
            this.chats = chats;
        }
    }

    public ArrayList<Message> getChatByLogin(String user2Login){
        return chats.get(user2Login);
    }

    public String getLastMessage(String user2Login){
        ArrayList<Message> chat = chats.get(user2Login);

        if(chat == null)
            return "no message yet";

        if(chat.isEmpty())
            return "no message yet";

        Message lastMessage = chat.get(chat.size() - 1);
        String lastMessageText = lastMessage.getMessage();
        if (lastMessage.getMessage_type() == Message.IMAGE_TYPE)
            lastMessageText = "photo";

        return lastMessageText;
    }
}
