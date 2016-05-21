package app.tesis.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import app.tesis.commons.ClientThread;
import app.tesis.commons.Dispatcher;
import app.tesis.commons.Event;
import app.tesis.commons.Message;
import app.tesis.commons.Message.Type;

public class ConnectionManager {

	private static ConnectionManager instance = new ConnectionManager();
	private ServerSocket server_socket;
	private HashMap<String, ClientThread> client_list;
	
	private ConnectionManager() {
		try {
			this.server_socket = new ServerSocket(Integer.valueOf("5432"));
			this.client_list = new HashMap<String, ClientThread>();
			Dispatcher.onEvent("receiveMessage", this, "processMessage", Integer.MAX_VALUE);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ConnectionManager getInstance() {
		return instance;
	}
	
	public void start() {
		while (true){
			try {
				Socket client = this.server_socket.accept();
				ClientThread client_thread = new ClientThread(client);
				byte[] mac = client.getInetAddress().getAddress();
				StringBuilder macAddress = new StringBuilder();
				for (int i = 0; i < mac.length; i++) {
					macAddress.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
				}
				this.client_list.put(macAddress.toString(), client_thread);
				client_thread.start();
				Message<Object> msg = new Message<Object>(Message.Type.REQ_DEVICE_SN, null);
				client_thread.send(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void processMessage(Event e) {
		Message<?> m = (Message<?>) e.getParam("message");
		
		switch (m.getType()) {
			case Type.RES_DEVICE_SN:
				ClientThread c = this.client_list.get(m.getAddress());
				this.client_list.put((String)m.getContent(), c);
				this.client_list.remove(m.getAddress());
				break;
		}
		
	}
}