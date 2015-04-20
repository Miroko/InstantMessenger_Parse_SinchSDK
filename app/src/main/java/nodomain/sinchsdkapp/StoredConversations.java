package nodomain.sinchsdkapp;

import android.content.Context;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class StoredConversations {

	final String filename = "Conversations";

	private ArrayList<Conversation> conversations;

	private Context context;

	public StoredConversations(Context context){
		this.context = context;
	}

	public ArrayList<Conversation> getConversations(){
		if(conversations == null){
			conversations = new ArrayList<>();
		}
		return conversations;
	}

	public void addConversation(Conversation newConversation) {
		if(!conversations.contains(newConversation)) {
			conversations.add(newConversation);
		}
	}

	@SuppressWarnings("unchecked")
	public void load(){
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(context.openFileInput(filename));
			conversations = (ArrayList<Conversation>) objectInputStream.readObject();
			objectInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void save(){
		try {
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(context.openFileOutput(filename,Context.MODE_PRIVATE));
			objectOutputStream.writeObject(conversations);
			objectOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
