package nodomain.sinchsdkapp;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends ActionBarActivity implements ContactsFragment.Listener, ConversationsFragment.Listener {

    private ConversationsFragment conversationsFragment;
    private ContactsFragment contactsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // UI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Fragments
        if(savedInstanceState == null) {
            conversationsFragment = new ConversationsFragment();
            contactsFragment = new ContactsFragment();
        }
    }

    @Override
    protected void onStart() {
	    // Start sinch service
	    startService(new Intent(this, SinchService.class));

        switchToConversations();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_conversations){
            switchToConversations();
            return true;
        }
        else if(id == R.id.action_contacts){
            switchToContacts();
            return true;
        }
        else if(id == R.id.action_signout){
            logOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, SinchService.class));
        super.onDestroy();
    }

    private void logOut(){
        LogOutCallback logOutCallback = new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                Intent login = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(login);

                finish();
            }
        };
        ParseUser.logOutInBackground(logOutCallback);
    }

    public void switchToConversations(){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFragmentContainer, conversationsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void switchToContacts(){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFragmentContainer, contactsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void startConversation(ParseUser with) {
	    conversationsFragment.startConversation(with);
    }

    @Override
    public void openConversation(String conversationUUID) {
        Intent messagingActivity = new Intent(this, MessagingActivity.class);
        messagingActivity.putExtra(MessagingActivity.EXTRA_CONVERSATION_UUID, conversationUUID);
        startActivity(messagingActivity);
    }
}
