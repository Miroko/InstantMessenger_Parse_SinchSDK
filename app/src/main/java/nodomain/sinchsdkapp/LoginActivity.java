package nodomain.sinchsdkapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends Activity {

    private EditText username;
    private EditText password;
    private Button register;
    private Button login;
	private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(ParseUser.getCurrentUser() != null){
            start();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.loginFieldUsername);
        password = (EditText) findViewById(R.id.loginFieldPassword);
        register = (Button)findViewById(R.id.loginButtonRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        login = (Button)findViewById(R.id.loginButtonLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
	    progressBar = (ProgressBar) findViewById(R.id.progressBar);
	    progressBar.setVisibility(View.INVISIBLE);

    }

    private void login(){
        lockUI();

        LogInCallback logInCallback = new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    start();
                } else {
                    releaseUI();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), logInCallback);
    }

    private void register(){
        lockUI();

        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(username.getText().toString());
        parseUser.setPassword(password.getText().toString());
        SignUpCallback signUpCallback = new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    start();
                }else{
                    releaseUI();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        parseUser.signUpInBackground(signUpCallback);
    }

    private void start(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void lockUI(){
        username.setEnabled(false);
        password.setEnabled(false);
        register.setEnabled(false);
        login.setEnabled(false);
	    progressBar.setVisibility(View.VISIBLE);
    }

    private void releaseUI(){
        username.setEnabled(true);
        password.setEnabled(true);
        register.setEnabled(true);
        login.setEnabled(true);
	    progressBar.setVisibility(View.INVISIBLE);
    }
}
