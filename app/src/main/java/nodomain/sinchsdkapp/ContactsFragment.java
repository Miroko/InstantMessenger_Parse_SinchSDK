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

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class ContactsFragment extends Fragment {

    private Listener listener;

	private ListView contactsList;

	class ContactsListAdapter extends ParseQueryAdapter<ParseUser> {

		public ContactsListAdapter(Context context, ParseQueryAdapter.QueryFactory<ParseUser> queryFactory){
			super(context, queryFactory);
		}

		@Override
		public View getItemView(ParseUser user, View view, ViewGroup parent) {
			TextView contactName;
			if(view == null){
				view = getActivity().getLayoutInflater().inflate(R.layout.contact_layout, parent, false);
			}
			// TODO: set image

			// Set contact name
			contactName = (TextView) view.findViewById(R.id.contactNameText);
			contactName.setText(user.getUsername());
			return view;
		}
	}

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
        initList();
    }

    private void initList(){
	    ParseQueryAdapter.QueryFactory<ParseUser> factory = new ParseQueryAdapter.QueryFactory<ParseUser>() {
		    @Override
		    public ParseQuery<ParseUser> create() {
			    ParseQuery<ParseUser> allUsersNotCurrent = ParseUser.getQuery();
			    allUsersNotCurrent.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
				return allUsersNotCurrent;
		    }
	    };

	    ContactsListAdapter contactsListAdapter = new ContactsListAdapter(getActivity().getApplicationContext(), factory);
	    contactsList.setAdapter(contactsListAdapter);

	    contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    ParseUser parseUser = (ParseUser) contactsList.getItemAtPosition(position);
			    listener.startConversation(parseUser);
		    }
	    });
    }

}
