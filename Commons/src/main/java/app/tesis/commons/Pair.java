package app.tesis.commons;

public class Pair implements Comparable<Pair> {

	private Object object;
	private String callback;
	private int priority;


	public Pair(Object object, String callback, int priority) {
		super();
		this.object = object;
		this.callback = callback;
		this.priority = priority;
	}

	public Object getObject() {
		return object;
	}

	public String getCallback() {
		return callback;
	}
	
	public int getPriority() {
		return this.priority;
	}

	@Override
	public int compareTo(Pair o) {
		return (o.getPriority() < this.getPriority()) ? -1 : (o.getPriority() > this.getPriority()) ? 1 : 0;
	}
}
