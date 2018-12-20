package corn.chat.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class PreferenceManager {

    public static final String USER_INFO_PREF_NAME = "user_info";
    public static final String USER_LOGIN = "user_login";

    public Context context;

    private PreferenceManager(Context context){
        this.context = context;
    }

    public static PreferenceManager with(Context context){
        return new PreferenceManager(context);
    }

    public boolean isUserLoggedPreviously(){
        SharedPreferences preferences = context.getSharedPreferences(USER_INFO_PREF_NAME, MODE_PRIVATE);
        return preferences.contains(USER_LOGIN);
    }

    public String getUserLogin(){
        SharedPreferences preferences = context.getSharedPreferences(USER_INFO_PREF_NAME, MODE_PRIVATE);
        return preferences.getString(USER_LOGIN, null);
    }

    public void saveUser(String login){
        SharedPreferences preferences = context.getSharedPreferences(USER_INFO_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PreferenceManager.USER_LOGIN, login);
        editor.commit();
    }

    public void clear(){
        SharedPreferences preferences = context.getSharedPreferences(USER_INFO_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
