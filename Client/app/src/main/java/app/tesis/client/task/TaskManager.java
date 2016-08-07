package app.tesis.client.task;

import java.util.concurrent.LinkedBlockingDeque;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import app.tesis.client.R;
import app.tesis.commons.Task;

public class TaskManager {

	private static LinkedBlockingDeque<Task> deque;
	private static TaskManager instance;

	private TaskManager(Context c) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
		int size = sharedPref.getInt(c.getResources().getString(R.string.pref_pool_size_key), c.getResources().getInteger(R.integer.pref_pool_size_default));
		deque = new LinkedBlockingDeque<Task>(size);
	}
	
	public static TaskManager getInstance(Context c) {
		if (instance == null) {
			instance = new TaskManager(c);
		}
		return instance;
	}
}
