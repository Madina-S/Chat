package corn.chat.model;

import android.support.annotation.NonNull;

public class Message implements Comparable<Message> {
    private String message;
    private int message_type;
    private String sender;
    private int sequence;

    private boolean isSent;

    public static final int TEXT_TYPE = 0;
    public static final int IMAGE_TYPE = 1;

    public Message(){}

    public Message(String message, int message_type, String sender, int sequence) {
        this.message = message;
        this.message_type = message_type;
        this.sender = sender;
        this.sequence = sequence;
    }

    public Message(String message, int message_type, String sender, int sequence, boolean isSent) {
        this(message, message_type, sender, sequence);
        this.isSent = isSent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMessage_type() {
        return message_type;
    }

    public void setMessage_type(int message_type) {
        this.message_type = message_type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getSenderInitial(){
        return sender.substring(0, 1);
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    @Override
    public String toString() {
        return String.format("%s: %s %b", sender, message, isSent);
    }

    @Override
    public int compareTo(@NonNull Message o) {
        return sequence - o.sequence;
    }
}
