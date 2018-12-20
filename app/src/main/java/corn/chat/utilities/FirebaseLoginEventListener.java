package corn.chat.utilities;

public interface FirebaseLoginEventListener {

    void userExistence(boolean isExists);

    void userSigned();
}
