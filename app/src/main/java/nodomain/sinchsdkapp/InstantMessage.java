package nodomain.sinchsdkapp;


import com.parse.ParseUser;

import java.util.Date;

public class InstantMessage {

	public static final int DIRECTION_IN = 0;
	public static final int DIRECTION_OUT = 1;

	private User sender;
	private User receiver;

	private String text;
	private Date timestamp;

	public InstantMessage(User sender, User receiver, String text, Date timestamp){
		this.sender = sender;
		this.receiver = receiver;
		this.text = text;
		this.timestamp = timestamp;
	}

	public int getDirection() {
		if(sender.getParseID().equals(ParseUser.getCurrentUser().getObjectId())){
			return DIRECTION_OUT;
		} else{
			return DIRECTION_IN;
		}
	}

	public User getSender() {
		return sender;
	}

	public String getText() {
		return text;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public User getReceiver() {
		return receiver;
	}
}
