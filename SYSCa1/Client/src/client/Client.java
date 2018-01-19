package client;

public class Client {
	public static void main(String[] args) {

		//create the handler. The socket to send/receive is opened upon construction
		PacketHandler handler = new PacketHandler();
		
		handler.run();
	}

}
