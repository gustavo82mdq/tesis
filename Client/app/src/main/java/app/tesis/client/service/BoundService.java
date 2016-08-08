package app.tesis.client.service;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;

import android.R.bool;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import app.tesis.client.R;
import app.tesis.commons.ClientThread;
import app.tesis.commons.Dispatcher;
import app.tesis.commons.Event;
import app.tesis.commons.Message;

public class BoundService extends Service {

	private final IBinder myBinder = new MyLocalBinder();
	private Socket socket;
	private ClientThread client_thread;
	private SharedPreferences sharedPref;
	private Context context;

	public BoundService() {
		super();
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return myBinder;
	}

	public void start() {
		if (context == null) {
			return;
		}
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this.context);
		try {
			String host = sharedPref.getString(this.context.getResources().getString(R.string.pref_server_host_key),
					this.context.getResources().getString(R.string.pref_server_host_default));
			int port = Integer.valueOf(sharedPref.getString(this.context.getResources().getString(R.string.pref_server_port_key),
					String.valueOf(this.context.getResources().getInteger(R.integer.pref_server_port_default))));
			this.socket = new Socket( host, port);
			this.client_thread = new ClientThread(this.socket);
			this.client_thread.start();
			Dispatcher.onEvent("receiveMessage", this, "processMessage", 5);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		//this.client_thread.send(new Message<Object>(Message.Type.REQ_DEVICE_DISCONNECT, null,));
		this.client_thread.disconnect(Build.SERIAL);
		this.client_thread = null;
	}


	public class MyLocalBinder extends Binder {
		public BoundService getService() {
			return BoundService.this;
		}
	}

	@SuppressWarnings("unchecked")
	public void processMessage(Event e) {
		Message<?> m = (Message<?>) e.getParam("message");

		switch (m.getType()) {
		case Message.Type.REQ_DEVICE_INFO:
			byte[] mac = new byte[0];
			mac = socket.getLocalAddress().getAddress();
			StringBuilder macAddress = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				macAddress.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
			}
			JSONObject obj = new JSONObject();
			obj.put("serial", Build.SERIAL);
			obj.put("poolSize", sharedPref.getString(context.getResources().getString(R.string.pref_pool_size_key),
					String.valueOf(context.getResources().getInteger(R.integer.pref_pool_size_default))));
			Message<String> msg = new Message<String>(Message.Type.RES_DEVICE_INFO, obj.toString(),
					macAddress.toString());
			this.client_thread.send(msg);
			break;
		}
	}
	
	public boolean isConnected() {
		if (this.client_thread != null)
			return this.client_thread.isConnected();
		else
			return false;
	}
}
