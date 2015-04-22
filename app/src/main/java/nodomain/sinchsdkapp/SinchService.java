package nodomain.sinchsdkapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.parse.ParseUser;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.WritableMessage;

public class SinchService extends Service implements SinchClientListener {
	private static final String APP_KEY = "daa17844-88fb-449b-834d-086d80722fa5";
	private static final String APP_SECRET = "kVbvjPoR+EyZys89DWp1KA==";
	private static final String ENVIRONMENT = "sandbox.sinch.com"; // TODO: switch to release

    private SinchClient sinchClient;

	public class SinchServiceBinder extends Binder{
		public void sendMessage(InstantMessage message){
			MessageClient messageClient = sinchClient.getMessageClient();
			messageClient.send(new WritableMessage(
					message.getRecipient().getObjectId(),
					message.getText()));
		}

		public void addMessageClientListener(MessageClientListener messageClientListener){
			sinchClient.getMessageClient().addMessageClientListener(messageClientListener);
		}

		public void removeMessageClientListener(MessageClientListener messageClientListener){
			sinchClient.getMessageClient().removeMessageClientListener(messageClientListener);
		}
	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start Sinch client
	    if (sinchClient == null) {
		    sinchClient = Sinch.getSinchClientBuilder()
				    .context(this)
				    .userId(ParseUser.getCurrentUser().getObjectId())
				    .applicationKey(APP_KEY)
				    .applicationSecret(APP_SECRET)
				    .environmentHost(ENVIRONMENT)
				    .build();
		    sinchClient.addSinchClientListener(this);
		    sinchClient.setSupportMessaging(true);
		    sinchClient.setSupportActiveConnectionInBackground(true);
		    sinchClient.checkManifest();
		    sinchClient.start();
	    }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onClientStarted(SinchClient sinchClient) {
        sinchClient.startListeningOnActiveConnection();
    }

    @Override
    public void onClientStopped(SinchClient sinchClient) {

    }

    @Override
    public void onClientFailed(SinchClient sinchClient, SinchError sinchError) {

    }

    @Override
    public void onRegistrationCredentialsRequired(SinchClient sinchClient, ClientRegistration clientRegistration) {

    }

    @Override
    public void onLogMessage(int i, String s, String s2) {
		Log.d("",s + s2);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new SinchServiceBinder();
    }

	@Override
    public void onDestroy() {
        sinchClient.stopListeningOnActiveConnection();
        sinchClient.terminate();
    }

}
