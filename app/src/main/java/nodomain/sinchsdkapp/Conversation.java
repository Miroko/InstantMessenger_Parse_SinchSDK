package nodomain.sinchsdkapp;


import com.parse.ParseUser;

public class Conversation {

    private ParseUser conversationWith;

    public Conversation(ParseUser with){
        conversationWith = with;
    }

    public ParseUser ConversationWith(){
        return conversationWith;
    }

    public ParseUser getConversationWith(){
        return conversationWith;
    }

}
