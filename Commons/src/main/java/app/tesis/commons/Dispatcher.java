package app.tesis.commons;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

public final class Dispatcher {

	private static HashMap<String, Vector<Pair>> listeners = new HashMap<String, Vector<Pair>>();
	private static boolean propagate = true;

	public static void onEvent(String event, Object caller, String callback, int priority) {
		Vector<Pair> vec;
		Pair p = new Pair(caller, callback, priority);
		if ((vec = listeners.get(event)) == null) {
			vec = new Vector<Pair>();
		}
		vec.addElement(p);
		Collections.sort(vec);
		listeners.put(event, vec);
	}
	
	public static void fireEvent(String event, String paramName, Object param) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(paramName, param);
		fireEvent(event, params);
	}

	public static void fireEvent(String event, HashMap<String, Object> params) {
		Vector<Pair> vec = listeners.get(event);

		for (Pair pair : vec) {
			if (propagate) {
				try {
					Method m = pair.getObject().getClass().getMethod(pair.getCallback(), Event.class);
					m.invoke(pair.getObject(), new Event(params));
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				break;
			}
		}
		propagate = true;
	}
	
	public static void stopPropagation() { 
		propagate = false;
	}
}
