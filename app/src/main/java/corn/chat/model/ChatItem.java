package corn.chat.model;

public class ChatItem {
    private String chatMember;
    private String lastMessage;

    public ChatItem(String chatMember) {
        this.chatMember = chatMember;
    }

    public ChatItem(String chatMember, String lastMessage) {
        this.chatMember = chatMember;
        this.lastMessage = lastMessage;
    }

    public String getChatMember() {
        return chatMember;
    }

    public void setChatMember(String chatMember) {
        this.chatMember = chatMember;
    }

    public String getChatMemberInitial(){
        return chatMember.substring(0, 1);
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
