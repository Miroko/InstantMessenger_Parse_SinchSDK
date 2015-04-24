package nodomain.sinchsdkapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends ActionBarActivity implements ContactsFragment.Listener, ConversationsFragment.Listener {

	private ViewPager viewPager;

	private ConversationsFragment conversationsFragment;
	private ContactsFragment contactsFragment;

	private CustomFragmentPagerAdapter customFragmentPagerAdapter;
	private class CustomFragmentPagerAdapter extends FragmentPagerAdapter{
		public CustomFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position){
				case 0: return conversationsFragment;
				case 1: return contactsFragment;
			}
			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // UI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

	    // Fragments
	    conversationsFragment = new ConversationsFragment();
	    contactsFragment = new ContactsFragment();
	    customFragmentPagerAdapter = new CustomFragmentPagerAdapter(getSupportFragmentManager());

	    // Pager
	    viewPager = (ViewPager) findViewById(R.id.main_pager);
		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
			@Override
			public void onPageSelected(int position) {
				getSupportActionBar().setSelectedNavigationItem(position);
			}
		});
	    viewPager.setAdapter(customFragmentPagerAdapter);

	    // Action bar tabs
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

	    // Conversations tab
	    actionBar.addTab(actionBar.newTab()
			    .setText(R.string.action_conversations)
			    .setTabListener(new ActionBar.TabListener() {
				    @Override
				    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
					    viewPager.setCurrentItem(tab.getPosition());
				    }

				    @Override
				    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

				    }

				    @Override
				    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

				    }
			    }));

	    // Contacts tab
	    actionBar.addTab(actionBar.newTab()
			    .setText(R.string.action_contacts)
			    .setTabListener(new ActionBar.TabListener() {
				    @Override
				    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
					    viewPager.setCurrentItem(tab.getPosition());
				    }

				    @Override
				    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

				    }

				    @Override
				    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

				    }
			    }));
    }

    @Override
    protected void onStart() {
	    // Start sinch service
	    startService(new Intent(this, SinchService.class));

       // switchToConversations();
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

		if(id == R.id.action_signout){
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
