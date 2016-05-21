package app.tesis.commons;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientThread extends Thread{

	private boolean isConnected;
	private Socket socket;
	private ObjectOutputStream output;
	private ObjectInputStream input;

	public ClientThread(Socket client) {
		this.isConnected = true;
		this.socket = client;
		
		try {
			this.output = new ObjectOutputStream(new FileOutputStream("out.out"));
			this.input = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disconnect(){
		this.isConnected = false;
		try {
			ClientThread.sleep(5000);
			this.input.close();
			this.socket.close();
			this.output.flush();
			this.output.close();		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (this.isConnected) {
			Message data = null;
			try {
				data = (Message) this.input.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(data.toString()); // TODO remove debug line 
			Dispatcher.fireEvent("receiveMessage", "message", data);
		}
	}
	
	public void send(Message msg) {
		try {
			this.output.writeObject(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
