package corn.chat.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import corn.chat.R;
import corn.chat.utilities.FirebaseLoginEventListener;
import corn.chat.utilities.FirebaseLoginManager;
import corn.chat.utilities.PreferenceManager;

//Launch Activity
// TODO: 10/12/18 add progress bar
// TODO: 10/12/18 save activity instance
// TODO: 17/12/18 change edit text for password to password field
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, FirebaseLoginEventListener {

    private LinearLayout passwordLayout;
    private LinearLayout confirmLayout;
    private EditText loginEditText;
    private EditText passwordEditText;
    private EditText confirmEditText;

    private String login;
    private String password;

    private FirebaseLoginManager firebaseDatabaseManager;
    private String TAG = LoginActivity.class.getName();

    private static final int USER_EXISTENCE_CHECK = 0;
    private static final int USER_PASSWORD_CHECK = 1;
    private static final int USER_REGISTRATION = 2;

    private int operation = 0;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth();
    }

    private void auth() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            mAuth.signInAnonymously()
                    .addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {}
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e(TAG, "signInAnonymously:FAILURE", exception);
                            Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void init() {
        PreferenceManager manager = PreferenceManager.with(this);
        if(manager.isUserLoggedPreviously()){
            login = manager.getUserLogin();
            openChatListActivity();
        }


        firebaseDatabaseManager = new FirebaseLoginManager(this);

        initUI();
    }

    private void initUI() {
        //adding back navigation button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(this);
        toolbar.getNavigationIcon()
                .setColorFilter(
                        ContextCompat.getColor(this, R.color.tab_icon_color),
                        PorterDuff.Mode.SRC_ATOP);

        passwordLayout = findViewById(R.id.password_layout);
        confirmLayout = findViewById(R.id.confirm_password_layout);
        loginEditText = findViewById(R.id.login_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmEditText = findViewById(R.id.confirm_password_edit_text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "level: " + operation);

        if (!isValidInput())
            return true;

        String login = loginEditText.getText().toString().trim();


        //for sign and sign up the existence of the user should be checked
        firebaseDatabaseManager.checkWhetherUserExists(login);

        return true;
    }

    private boolean isValidInput() {
        login = loginEditText.getText().toString().trim();
        if (login.isEmpty()) {
            Toast.makeText(this, R.string.fill_all_fields_message, Toast.LENGTH_SHORT).show();
            return false;
        }

        password = passwordEditText.getText().toString().trim();
        if (password.isEmpty() && operation >= USER_PASSWORD_CHECK) {
            Toast.makeText(this, R.string.fill_all_fields_message, Toast.LENGTH_SHORT).show();
            return false;
        }

        String confirmPassword = confirmEditText.getText().toString().trim();
        if (confirmPassword.isEmpty() && operation == USER_REGISTRATION) {
            Toast.makeText(this, R.string.fill_all_fields_message, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!confirmPassword.equals(password) && operation == USER_REGISTRATION) {
            Toast.makeText(this, R.string.no_password_match_message, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void openChatListActivity() {
        //save the user instance
        PreferenceManager.with(this).saveUser(login);

        //open activity
        Intent intent = new Intent(this, ChatListActivity.class);
        intent.putExtra(PreferenceManager.USER_LOGIN, login);
        startActivity(intent);
        finish();
    }

    private void prepareForSignUp() {
        operation = USER_REGISTRATION;
        passwordLayout.setVisibility(View.VISIBLE);

        if (confirmLayout == null)
            confirmLayout = findViewById(R.id.confirm_password_layout);
        confirmLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void userExistence(boolean isExists) {
        if (!isExists && operation == USER_EXISTENCE_CHECK) {
            askForSignUp();
            return;
        }

        switch (operation) {
            case USER_EXISTENCE_CHECK:
                passwordLayout.setVisibility(View.VISIBLE);
                operation = USER_PASSWORD_CHECK;
                break;
            case USER_PASSWORD_CHECK:
                if (firebaseDatabaseManager.checkUserPassword(password))
                    openChatListActivity();
                else
                    Toast.makeText(this, R.string.no_auth_message, Toast.LENGTH_SHORT).show();
                break;
            case USER_REGISTRATION:
                firebaseDatabaseManager.signUser(login, password);
                break;
        }
    }

    @Override
    public void userSigned() {
        openChatListActivity();
    }

    private void askForSignUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("There is no such user");
        builder.setMessage("Do you want to sing up?");
        builder.setNegativeButton("No", null);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                prepareForSignUp();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "level: " + operation);

        if (operation == USER_REGISTRATION) {
            // TODO: 10/12/18 show dialog window for registration cancellation approvement
            Toast.makeText(this, R.string.reg_cancel_message, Toast.LENGTH_SHORT).show();
            operation = USER_PASSWORD_CHECK;
            clearAllFields();
            confirmLayout.setVisibility(View.GONE);
        }
    }

    private void clearAllFields() {
        loginEditText.setText("");
        passwordEditText.setText("");
        confirmEditText.setText("");
    }
}
