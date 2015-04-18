package nodomain.sinchsdkapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class LoginActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Parse
        final String appId = "3WH140Acsg0sxhNwW6M7VAquFDt5cGIoiGTM4vrx";
        final String clientId = "JqB6tbysttwheTfONJSiOHdn7Lgo0YRguBIrDEbD";
        Parse.initialize(this, appId, clientId);

        Button login = (Button)findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        Button register = (Button)findViewById(R.id.loginRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void login(){
        EditText username = (EditText) findViewById(R.id.loginUsername);
        EditText password = (EditText) findViewById(R.id.loginPassword);
        ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    start();
                } else {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void register(){
        EditText username = (EditText)findViewById(R.id.loginUsername);
        EditText password = (EditText)findViewById(R.id.loginPassword);

        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(username.getText().toString());
        parseUser.setPassword(password.getText().toString());
        parseUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                 if(e == null){
                    start();
                 }else{
                     Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                 }
            }
        });
    }

    private void start(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

}
