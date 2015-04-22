package nodomain.sinchsdkapp;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.UUID;

@ParseClassName("Conversation")
public class Conversation extends ParseObject{

    public Conversation(){
	   // Default
    }

	public void setRecipient(ParseUser recipient){
		put("recipient", recipient);
	}

	public ParseUser getRecipient() {
		return getParseUser("recipient");
	}

	public void setUUID(){
		put("uuid", UUID.randomUUID().toString());
	}

	public static ParseQuery<Conversation> getQuery() {
		return ParseQuery.getQuery(Conversation.class);
	}

	public String getUUID() {
		return getString("uuid");
	}
}
