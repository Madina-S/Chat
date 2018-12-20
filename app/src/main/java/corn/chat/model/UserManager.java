package corn.chat.model;

import java.util.ArrayList;

public class UserManager {

    public static ArrayList<User> getDefaultUsers(){
        String logins[] = new String[]{"Adam Smith", "Brian Jones", "Taylor Li"};
        ArrayList<User> users = new ArrayList<>();
        for (String login : logins)
            users.add(new User(login));

        return users;
    }
}
