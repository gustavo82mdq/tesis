import app.tesis.server.ConnectionManager;

public class ServerTest {

	public static void main(String[] args) {
		ConnectionManager c = ConnectionManager.getInstance();
		c.start();
	}

}
