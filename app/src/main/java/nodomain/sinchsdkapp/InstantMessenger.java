package nodomain.sinchsdkapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class InstantMessenger extends Application{

	final String appId = "3WH140Acsg0sxhNwW6M7VAquFDt5cGIoiGTM4vrx";
	final String clientId = "JqB6tbysttwheTfONJSiOHdn7Lgo0YRguBIrDEbD";

	@Override
	public void onCreate() {
		super.onCreate();

		ParseObject.registerSubclass(Conversation.class);
		ParseObject.registerSubclass(InstantMessage.class);
		Parse.enableLocalDatastore(this);
		Parse.initialize(this, appId, clientId);
	}
}
