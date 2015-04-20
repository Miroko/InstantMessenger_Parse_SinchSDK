package nodomain.sinchsdkapp;


import com.parse.ParseUser;

import java.io.Serializable;

public class User  implements Serializable{

	private String username;
	private String parseID;

	public User(String username, String parseID){
		this.username = username;
		this.parseID = parseID;
	}

	public User(ParseUser parseUser){
		this(parseUser.getUsername(), parseUser.getObjectId());
	}

	public String getParseID() {
		return parseID;
	}

	public String getUsername() {
		return username;
	}
}
