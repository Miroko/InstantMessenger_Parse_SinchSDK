package nodomain.sinchsdkapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;

import java.util.ArrayList;
import java.util.List;

public class MessagingActivity extends ActionBarActivity implements MessageClientListener{

	// TODO: store messages to parse
	// TODO: query stored messages

	public static final String EXTRA_CONVERSATION = "Conversation";

	private Conversation conversation;

	private SinchService.SinchServiceBinder sinchServiceBinder;
	private SinchServiceConnection sinchServiceConnection = new SinchServiceConnection();
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

    private MessagesAdapter messagesAdapter;
    private class MessagesAdapter extends BaseAdapter{
        private LayoutInflater layoutInflater;
        private ArrayList<InstantMessage> messages;

        public MessagesAdapter(Activity activity) {
            layoutInflater = activity.getLayoutInflater();
            messages = new ArrayList<>();
        }

        public void addMessage(InstantMessage message) {
            messages.add(message);
            notifyDataSetChanged();
        }
        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public Object getItem(int i) {
            return messages.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int i) {
            InstantMessage message = messages.get(i);
	        return message.getDirection();
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
	        int direction = getItemViewType(i);
            if (convertView == null) {
	            int res;
                if (direction == InstantMessage.DIRECTION_IN) {
                    res = R.layout.message_left;
                } else {
                    res = R.layout.message_right;
                }
	            // Inflate message view
	            convertView = layoutInflater.inflate(res, viewGroup, false);
            }

	        // Set message sender
	        InstantMessage message = messages.get(i);
	        TextView sender = (TextView) convertView.findViewById(R.id.message_sender);
	        sender.setText(message.getSender().getUsername());

	        // Set message text
	        TextView text = (TextView) convertView.findViewById(R.id.message_text);
	        text.setText(message.getText());

	        // Set message date
	        TextView date = (TextView) convertView.findViewById(R.id.message_date);
	        date.setText(message.getTimestamp().toString());

            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bind sinch service
        bindService(new Intent(this, SinchService.class), sinchServiceConnection, BIND_AUTO_CREATE);

        // UI
        setContentView(R.layout.activity_messaging);
        conversation = (Conversation) getIntent().getExtras().get(EXTRA_CONVERSATION);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
	    getSupportActionBar().setTitle(conversation.getWith().getUsername());

        // Messages
        final ListView messages = (ListView) findViewById(R.id.messagingMessages);
        messagesAdapter = new MessagesAdapter(this);
        messages.setAdapter(messagesAdapter);

        // New message text field
        final MultiAutoCompleteTextView newMessageText = (MultiAutoCompleteTextView) findViewById(R.id.messagingNewMessage);

        // Send button
        Button send = (Button) findViewById(R.id.messagingSend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InstantMessage instantMessage = new InstantMessage(
		                new User(ParseUser.getCurrentUser()),
				        conversation.getWith(),
				        newMessageText.getText().toString(),
				        null);
                sinchServiceBinder.sendMessage(instantMessage);
            }
        });
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
	    User with = conversation.getWith();
        if (message.getSenderId().equals(with.getParseID())) {
	        // If message belongs to this conversation
	        messagesAdapter.addMessage(new InstantMessage(
			     with,
			     new User(ParseUser.getCurrentUser()),
				 message.getTextBody(),
				 message.getTimestamp()));
        }
    }

    @Override
    public void onMessageSent(MessageClient messageClient, Message message, String s) {
	    messagesAdapter.addMessage(new InstantMessage(
			    new User(ParseUser.getCurrentUser()),
			    conversation.getWith(),
			    message.getTextBody(),
			    message.getTimestamp()));
    }

    @Override
    public void onMessageFailed(MessageClient messageClient, Message message, MessageFailureInfo messageFailureInfo) {
        Log.e("Sinch", message.getRecipientIds().get(0));
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
