package nodomain.sinchsdkapp;


import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class ConversationsFragment extends ListFragment {

    private Listener listener;

    public interface Listener{
        public void openConversation(Conversation conversation);
    }

    private ArrayList<Conversation> conversations;

    public ConversationsFragment() {
        conversations = new ArrayList<>();
    }

    @Override
    public void onAttach(Activity activity) {
        listener = (Listener) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversations, container, false);
    }

    public void addConversation(Conversation conversation){
        conversations.add(conversation);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateList();
    }

    private void populateList(){
        final ArrayList<String> names = new ArrayList<>();
        for(Conversation conversation : conversations){
            names.add(conversation.getConversationWith().getUsername());
        }

        ListView listView = getListView();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity().getApplicationContext(),
                R.layout.contact_layout,
                names);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.openConversation(conversations.get(position));
            }
        });

    }


}
