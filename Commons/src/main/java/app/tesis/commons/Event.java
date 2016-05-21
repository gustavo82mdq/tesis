package app.tesis.commons;

import java.util.HashMap;

public class Event {
	
	private HashMap<String, Object> params;

	public Event(HashMap<String, Object> params) {
		this.params = params;
	}

	public HashMap<String, Object> getParams() {
		return params;
	}
	
	public Object getParam(String name) {
		return params.get(name);
	}

}
