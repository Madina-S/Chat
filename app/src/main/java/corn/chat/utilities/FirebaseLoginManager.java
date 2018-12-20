package corn.chat.utilities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.firebase.database.*;

public class FirebaseLoginManager {

    private DatabaseReference root;

    private FirebaseLoginEventListener loginListener;

    private static String TAG = FirebaseLoginManager.class.getName();

    private String lastRetrievedPassword;

    public FirebaseLoginManager(FirebaseLoginEventListener listener) {
        root = FirebaseDatabase.getInstance().getReference();
        this.loginListener = listener;
    }

    public void checkWhetherUserExists(String login) {
        DatabaseReference userTable = root.child(FirebaseConfig.USER_KEY);
        DatabaseReference user = userTable.child(login);

        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // TODO: 10/12/18 maybe log should be removed
                loginListener.userExistence(dataSnapshot.exists());
                if (dataSnapshot.exists()) {
                    lastRetrievedPassword = dataSnapshot.child(FirebaseConfig.PASSWORD_KEY).getValue(String.class);
                    // null on sign up
                    if (lastRetrievedPassword != null)
                        Log.d(TAG, lastRetrievedPassword);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public boolean checkUserPassword(String password) {
        return lastRetrievedPassword.equals(password);
    }

    public void signUser(String login, String password) {
        DatabaseReference userTable = root.child(FirebaseConfig.USER_KEY);
        DatabaseReference user = userTable.child(login);

        user.child(FirebaseConfig.LOGIN_KEY).setValue(login);
        user.child(FirebaseConfig.PASSWORD_KEY).setValue(password, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Log.d(TAG, "user signed up successfully");
                if (databaseReference.getKey().equals(FirebaseConfig.PASSWORD_KEY))
                    loginListener.userSigned();
            }
        });
    }
}
