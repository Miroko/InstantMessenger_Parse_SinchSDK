package nodomain.sinchsdkapp;


import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;


@ParseClassName("InstantMessage")
public class InstantMessage extends ParseObject{

	public static final int DIRECTION_IN = 0;
	public static final int DIRECTION_OUT = 1;

	public InstantMessage(){
		// Default
	}

	public void setRecipient(ParseUser recipient){
		put("recipient", recipient);
	}

	public ParseUser getRecipient(){
		return getParseUser("recipient");
	}

	public int getDirection(){
		if(getRecipient().getObjectId() == ParseUser.getCurrentUser().getObjectId()){
			return DIRECTION_IN;
		}else return DIRECTION_OUT;
	}

	public static ParseQuery<InstantMessage> getQuery() {
		return ParseQuery.getQuery(InstantMessage.class);
	}

	public void setConversation(Conversation conversation){
		put("conversation", conversation);
	}

	public Conversation getConversation(){
		return (Conversation) get("conversation");
	}

	public void setTimestamp(Date timestamp){
		put("timestamp", timestamp);
	}

	public Date getTimestamp() {
		return getDate("timestamp");
	}

	public void setText(String text){
		put("text", text);
	}

	public String getText() {
		return getString("text");
	}

	public void setSender(ParseUser sender){
		put("sender", sender);
	}

	public ParseUser getSender() {
		return getParseUser("sender");
	}
}
