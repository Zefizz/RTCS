package intermediate;

public class Intermediate {
	public static void main(String[] args) {
		
		//create the handler. The sockets are opened upon construction
		RelayHandler relay = new RelayHandler();
		
		relay.run();
	}
}
