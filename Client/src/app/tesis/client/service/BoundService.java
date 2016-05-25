package app.tesis.client.service;

import java.io.IOException;
import java.net.Socket;

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
            this.socket = new Socket(
            		sharedPref.getString(
            				this.context.getResources().getString(R.string.pref_server_host_key),
            				this.context.getResources().getString(R.string.pref_server_host_default)),
            		sharedPref.getInt(
            				this.context.getResources().getString(R.string.pref_server_port_key),
            				this.context.getResources().getInteger(R.integer.pref_server_port_default))
            		);
            this.client_thread = new ClientThread(this.socket);
            this.client_thread.start();
            Dispatcher.onEvent("receiveMessage", this, "processMessage", 5);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class MyLocalBinder extends Binder {
        public BoundService getService() {
            return BoundService.this;
        }
    }

    public void processMessage(Event e) {
        Message<?> m = (Message) e.getParam("message");

        switch (m.getType()) {
            case Message.Type.REQ_DEVICE_SN:
                byte[] mac = new byte[0];
                mac = socket.getLocalAddress().getAddress();
                StringBuilder macAddress = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    macAddress.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
                }

                Message<String> msg = new Message<String>(Message.Type.RES_DEVICE_SN, Build.SERIAL,macAddress.toString());
                this.client_thread.send(msg);
                break;
        }

    }
}
