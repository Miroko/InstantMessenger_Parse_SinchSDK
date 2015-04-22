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
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {

    private Listener listener;

	private ListView contactsList;

    public interface Listener{
        public void startConversation(ParseUser with);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (Listener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
	    contactsList = (ListView) view.findViewById(R.id.contactsContactsList);
        populateList();
    }

    private void populateList(){
        ParseQuery<ParseUser> allUsersNotCurrent = ParseUser.getQuery();
        allUsersNotCurrent.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());

        allUsersNotCurrent.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(final List<ParseUser> parseUsers, ParseException e) {
                if(e == null){
                    final ArrayList<String> names = new ArrayList<>();
                    for(ParseUser user : parseUsers){
                        names.add(user.getUsername());
                    }

                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                            getActivity().getApplicationContext(),
                            R.layout.contact_layout,
                            names);
                    contactsList.setAdapter(arrayAdapter);

                    contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	                        ParseUser parseUser = parseUsers.get(position);
                            listener.startConversation(parseUser);
                        }
                    });

                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "Error loading contacts", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
