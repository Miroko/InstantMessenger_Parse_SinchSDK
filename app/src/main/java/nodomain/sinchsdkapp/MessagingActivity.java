package nodomain.sinchsdkapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessagingActivity extends ActionBarActivity implements MessageClientListener{

    private MessagesAdapter messagesAdapter;
    private class MessagesAdapter extends BaseAdapter{

        public static final int DIRECTION_INCOMING = 0;
        public static final int DIRECTION_OUTGOING = 1;

        private LayoutInflater layoutInflater;
        private ArrayList<Pair<WritableMessage, Integer>> messages;

        public MessagesAdapter(Activity activity) {
            layoutInflater = activity.getLayoutInflater();
            messages = new ArrayList<>();
        }

        public void addMessage(WritableMessage message, int direction) {
            messages.add(new Pair(message, direction));
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
            return messages.get(i).second;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            int direction = getItemViewType(i);

            if (convertView == null) {
                int res = 0;
                if (direction == DIRECTION_INCOMING) {
                    res = R.layout.message_left;
                } else if (direction == DIRECTION_OUTGOING) {
                    res = R.layout.message_right;
                }
                convertView = layoutInflater.inflate(res, viewGroup, false);
            }

            WritableMessage message = messages.get(i).first;

            TextView txtMessage = (TextView) convertView.findViewById(R.id.message_text);
            txtMessage.setText(message.getTextBody());

            return convertView;
        }
    }

    private SinchService.SinchServiceBinder sinchServiceBinder;
    private SinchServiceConnection sinchServiceConnection = new SinchServiceConnection();

    private String recipientUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bind sinch service
        bindService(new Intent(this, SinchService.class), sinchServiceConnection, BIND_AUTO_CREATE);
        // UI
        setContentView(R.layout.activity_messaging);
        recipientUserId = getIntent().getExtras().getString("userId");
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setTitle(recipientUserId);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        // Messages
        ListView messages = (ListView) findViewById(R.id.messagingMessages);
        messagesAdapter = new MessagesAdapter(this);
        messages.setAdapter(messagesAdapter);
        // Query stored message
        final String[] userIds = {ParseUser.getCurrentUser().getObjectId(), recipientUserId};
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseMessage");
        query.whereContainedIn("senderId", Arrays.asList(userIds));
        query.whereContainedIn("recipientId", Arrays.asList(userIds));
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messageList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < messageList.size(); i++) {
                        WritableMessage message = new WritableMessage(messageList.get(i).get("recipientId").toString(), messageList.get(i).get("messageText").toString());
                        if (messageList.get(i).get("senderId").toString().equals(userIds[0])) {
                            messagesAdapter.addMessage(message, MessagesAdapter.DIRECTION_OUTGOING);
                        } else {
                            messagesAdapter.addMessage(message, MessagesAdapter.DIRECTION_INCOMING);
                        }
                    }
                }
            }
        });
        // New message
        final MultiAutoCompleteTextView newMessageText = (MultiAutoCompleteTextView) findViewById(R.id.messagingNewMessage);
        // Send button
        Button send = (Button) findViewById(R.id.messagingSend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WritableMessage message = new WritableMessage();
                message.setTextBody(newMessageText.getText().toString());
                sinchServiceBinder.sendMessage(message, recipientUserId);
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
        if (message.getSenderId().equals(recipientUserId)) {
            WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
            messagesAdapter.addMessage(writableMessage, MessagesAdapter.DIRECTION_INCOMING);
        }
    }

    @Override
    public void onMessageSent(MessageClient messageClient, Message message, String s) {
        // Store message to parse
        final WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseMessage");
        query.whereEqualTo("sinchId", message.getMessageId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messageList, com.parse.ParseException e) {
                if (e == null) {
                    if (messageList.size() == 0) {
                        ParseObject parseMessage = new ParseObject("ParseMessage");
                        parseMessage.put("senderId", ParseUser.getCurrentUser().getObjectId());
                        parseMessage.put("recipientId", writableMessage.getRecipientIds().get(0));
                        parseMessage.put("messageText", writableMessage.getTextBody());
                        parseMessage.put("sinchId", writableMessage.getMessageId());
                        parseMessage.saveInBackground();

                        // Add in messages
                        messagesAdapter.addMessage(writableMessage, MessagesAdapter.DIRECTION_OUTGOING);
                    }
                }
            }
        });
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

    @Override
    protected void onDestroy() {
        sinchServiceBinder.removeMessageClientListener(this);
        unbindService(sinchServiceConnection);
        super.onDestroy();
    }
}
