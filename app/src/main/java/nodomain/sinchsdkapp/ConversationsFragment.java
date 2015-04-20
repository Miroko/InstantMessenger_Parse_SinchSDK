package nodomain.sinchsdkapp;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class ConversationsFragment extends Fragment {

    private Listener listener;
	public interface Listener{
		public void openConversation(Conversation conversation);
	}

	private ListView conversationsList;

	private StoredConversations storedConversations;

	public ConversationsFragment(){
		// Default
	}

	public void startConversation(User with) {
		Conversation newConversation = new Conversation(with);

		storedConversations.addConversation(newConversation);

		listener.openConversation(newConversation);
	}

    @Override
    public void onAttach(Activity activity) {
        listener = (Listener) activity;
	    storedConversations = new StoredConversations(activity.getApplicationContext());
	    storedConversations.load();
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
        populateList();
    }


    private void populateList(){
        final ArrayList<String> names = new ArrayList<>();
        for(Conversation conversation : storedConversations.getConversations()){
            names.add(conversation.getWith().getUsername());
        }

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getActivity().getApplicationContext(),
                R.layout.contact_layout,
                names);
        conversationsList.setAdapter(arrayAdapter);

        conversationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		        listener.openConversation(storedConversations.getConversations().get(position));
	        }
        });
    }

	@Override
	public void onDestroy() {
		super.onDestroy();
		storedConversations.save();
	}
}
