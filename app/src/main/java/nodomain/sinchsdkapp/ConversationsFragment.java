package nodomain.sinchsdkapp;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class ConversationsFragment extends Fragment {

	private ListView conversationsList;

    private Listener listener;
	public interface Listener{
		public void openConversation(String conversationUUID);
	}

	class ConversationsListAdapter extends ParseQueryAdapter<Conversation>{

		public ConversationsListAdapter(Context context, ParseQueryAdapter.QueryFactory<Conversation> queryFactory){
			super(context, queryFactory);
		}

		@Override
		public View getItemView(Conversation conversation, View view, ViewGroup parent) {
			TextView contactName;
			if(view == null){
				view = getActivity().getLayoutInflater().inflate(R.layout.contact_layout, parent, false);
			}
			// Set contact name
			contactName = (TextView) view.findViewById(R.id.contactName);
			ParseUser parseUser = conversation.getRecipient();
			try {
				parseUser.fetchIfNeeded();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			contactName.setText(parseUser.getUsername());
			return view;
		}
	}

	public ConversationsFragment(){
		// Default
	}

	public void startConversation(final ParseUser with) {
		ParseQuery<Conversation> query = Conversation.getQuery();
		query.fromLocalDatastore();
		query.whereEqualTo("recipient", with);
		query.getFirstInBackground(new GetCallback<Conversation>() {
			@Override
			public void done(Conversation c, ParseException e) {
				if (c != null) {
					listener.openConversation(c.getUUID());
				} else {
					// Create new conversation
					final Conversation conversation = new Conversation();
					conversation.setRecipient(with);
					conversation.setUUID();

					conversation.pinInBackground(new SaveCallback() {
						@Override
						public void done(ParseException e) {
							listener.openConversation(conversation.getUUID());
						}
					});
				}
			}
		});
	}

    @Override
    public void onAttach(Activity activity) {
        listener = (Listener) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversations, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
	    conversationsList = (ListView) view.findViewById(R.id.conversationsConversationsList);
        initList();
    }

    private void initList(){
        ParseQueryAdapter.QueryFactory<Conversation> factory = new ParseQueryAdapter.QueryFactory<Conversation>() {
	        @Override
	        public ParseQuery<Conversation> create() {
		        ParseQuery<Conversation> query = Conversation.getQuery();
		        query.fromLocalDatastore();
		        return query;
	        }
        };

	    conversationsList.setAdapter(new ConversationsListAdapter(getActivity().getApplicationContext(), factory));

	    conversationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    Conversation conversation = (Conversation) conversationsList.getAdapter().getItem(position);
			    listener.openConversation(conversation.getUUID());
		    }
	    });
    }

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
