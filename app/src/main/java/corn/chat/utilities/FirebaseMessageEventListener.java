package corn.chat.utilities;

import corn.chat.model.Message;

public interface FirebaseMessageEventListener {

    void sentMessage(Message message);
    void receivedMessage(Message message);
}
