package app.tesis.commons;

import java.io.Serializable;

public class Message<T> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public class Type {
		public final static int REQ_DEVICE_INFO = 1;
		public final static int RES_DEVICE_INFO = 2;
		public final static int REQ_DEVICE_DISCONNECT = 3;
	}
	
	private Integer type;

	private T content;
	
	private String address;
	
	public Message() {
		this.type = null;
		this.content = null;
		this.address = null;
	}

	public Message(int type, T content) {
		this.type = type;
		this.content = content;
		this.address = null;
	}

	public Message(int type, T content, String address) {
		this.type = type;
		this.content = content;
		this.address = address;
	}

	public int getType() {
		return type;
	}

	public T getContent() {
		return content;
	}

	public String getAddress() {
		return address;
	}

	public void setContent(T content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return String.format("[%s,%s]", String.valueOf(this.type),(this.content != null) ? this.content.toString() : "null");
	}
}
