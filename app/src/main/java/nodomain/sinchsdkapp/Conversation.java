package nodomain.sinchsdkapp;

import java.io.Serializable;

public class Conversation implements Serializable{	// TODO: Change seriealizable to parcelable

    private User with;

    public Conversation(User with){
	    this.with = with;
    }

	public User getWith() {
		return with;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof  Conversation) {
			Conversation conversation = (Conversation) o;
			return with.getParseID().equals(conversation.getWith().getParseID());
		}
		else return false;
	}
}
