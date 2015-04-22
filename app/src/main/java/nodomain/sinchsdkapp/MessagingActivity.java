package nodomain.sinchsdkapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;

import java.util.Date;
import java.util.List;

public class MessagingActivity extends ActionBarActivity implements MessageClientListener{

	public static final String EXTRA_CONVERSATION_UUID = "extra_uuid";

	private ListView messagesList;

	private Conversation conversation;

	private SinchService.SinchServiceBinder sinchServiceBinder;
	private SinchServiceConnection sinchServiceConnection;
	private class SinchServiceConnection implements ServiceConnection{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			sinchServiceBinder = (SinchService.SinchServiceBinder)service;
			sinchServiceBinder.addMessageClientListener(MessagingActivity.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			sinchServiceBinder = null;
		}
	}

	private MessagesListAdapter messagesListAdapter;
	private class MessagesListAdapter extends ParseQueryAdapter<InstantMessage> {

		public MessagesListAdapter(Context context, ParseQueryAdapter.QueryFactory<InstantMessage> queryFactory){
			super(context, queryFactory);
		}

		@Override
		public View getItemView(InstantMessage instantMessage, View view, ViewGroup parent) {
			int direction = instantMessage.getDirection();
			if (view == null) {
				int res;
				if (direction == InstantMessage.DIRECTION_IN) {
					res = R.layout.message_left;
				} else {
					res = R.layout.message_right;
				}
				// Inflate message view
				view = getLayoutInflater().inflate(res, parent, false);
			}

			// Set message sender
			TextView sender = (TextView) view.findViewById(R.id.message_sender);
			sender.setText(instantMessage.getSender().getUsername());

			// Set message text
			TextView text = (TextView) view.findViewById(R.id.message_text);
			text.setText(instantMessage.getText());

			// Set message date
			TextView date = (TextView) view.findViewById(R.id.message_date);
			date.setText(instantMessage.getTimestamp().toString());

			return view;
		}
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		// Bind sinch service
		sinchServiceConnection = new SinchServiceConnection();
		bindService(new Intent(this, SinchService.class), sinchServiceConnection, BIND_AUTO_CREATE);

		// Load stored conversation
	    ParseQuery<Conversation> query = Conversation.getQuery();
	    query.fromLocalDatastore();
	    query.whereEqualTo("uuid", getIntent().getExtras().get(EXTRA_CONVERSATION_UUID));
	    query.getFirstInBackground(new GetCallback<Conversation>() {
		    @Override
		    public void done(Conversation c, ParseException e) {
			    conversation = c;

			    // UI
			    setContentView(R.layout.activity_messaging);
			    getSupportActionBar().setDisplayShowHomeEnabled(false);
			    getSupportActionBar().setDisplayShowTitleEnabled(true);
			    getSupportActionBar().setTitle(conversation.getRecipient().getUsername());

			    // Messages list
			    messagesList = (ListView) findViewById(R.id.messagingMessages);
			    initList();

			    // New message text field
			    final MultiAutoCompleteTextView newMessageText = (MultiAutoCompleteTextView) findViewById(R.id.messagingNewMessage);

			    // Send button
			    Button send = (Button) findViewById(R.id.messagingSend);
			    send.setOnClickListener(new View.OnClickListener() {
				    @Override
				    public void onClick(View v) {
					    InstantMessage instantMessage = new InstantMessage();
					    instantMessage.setRecipient(conversation.getRecipient());
					    instantMessage.setSender(ParseUser.getCurrentUser());
					    instantMessage.setText(newMessageText.getText().toString());
					    instantMessage.setTimestamp(new Date());
					    sinchServiceBinder.sendMessage(instantMessage);
				    }
			    });
		    }
	    });
    }

	private void initList(){
		ParseQueryAdapter.QueryFactory<InstantMessage> factory = new ParseQueryAdapter.QueryFactory<InstantMessage>() {
			@Override
			public ParseQuery<InstantMessage> create() {
				ParseQuery<InstantMessage> query = InstantMessage.getQuery();
				query.whereEqualTo("recipient", conversation.getRecipient());
				query.fromLocalDatastore();
				return query;
			}
		};

		messagesListAdapter = new MessagesListAdapter(getApplicationContext(), factory);
		messagesList.setAdapter(messagesListAdapter);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_messaging, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onIncomingMessage(MessageClient messageClient, Message message) {
	    ParseUser conversationRecipient = conversation.getRecipient();
	    if(message.getSenderId().equals(conversationRecipient.getObjectId())){
		    // Message to this conversation
		    InstantMessage instantMessage = new InstantMessage();
		    instantMessage.setRecipient(ParseUser.getCurrentUser());
		    instantMessage.setSender(conversationRecipient);
		    instantMessage.setText(message.getTextBody());
		    instantMessage.setTimestamp(message.getTimestamp());

		    instantMessage.pinInBackground(new SaveCallback() {
			    @Override
			    public void done(ParseException e) {
				    messagesListAdapter.loadObjects();
			    }
		    });
	    }
    }

    @Override
    public void onMessageSent(MessageClient messageClient, Message message, String s) {
	    InstantMessage instantMessage = new InstantMessage();
	    instantMessage.setRecipient(conversation.getRecipient());
	    instantMessage.setSender(ParseUser.getCurrentUser());
	    instantMessage.setText(message.getTextBody());
	    instantMessage.setTimestamp(message.getTimestamp());

	    instantMessage.pinInBackground(new SaveCallback() {
		    @Override
		    public void done(ParseException e) {
			    messagesListAdapter.loadObjects();
		    }
	    });
    }

    @Override
    public void onMessageFailed(MessageClient messageClient, Message message, MessageFailureInfo messageFailureInfo) {
        Log.e("", message.getRecipientIds().get(0));
    }

    @Override
    public void onMessageDelivered(MessageClient messageClient, MessageDeliveryInfo messageDeliveryInfo) {

    }

    @Override
    public void onShouldSendPushData(MessageClient messageClient, Message message, List<PushPair> pushPairs) {

    }

    @Override
    protected void onDestroy() {
        sinchServiceBinder.removeMessageClientListener(this);
        unbindService(sinchServiceConnection);
        super.onDestroy();
    }
}
