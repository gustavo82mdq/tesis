package app.tesis.client;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import java.io.IOException;
import java.net.Socket;

import app.tesis.commons.ClientThread;
import app.tesis.commons.Dispatcher;
import app.tesis.commons.Event;
import app.tesis.commons.Message;

public class BoundService extends Service {

    private final IBinder myBinder = new MyLocalBinder();
    private Socket socket;
    private ClientThread client_thread;

    public BoundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public void start() {
        try {
            this.socket = new Socket("192.168.1.2", 5432);
            this.client_thread = new ClientThread(this.socket);
            this.client_thread.start();
            Dispatcher.onEvent("receiveMessage", this, "processMessage", 5);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class MyLocalBinder extends Binder {
        BoundService getService() {
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
