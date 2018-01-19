package server;

public class Server {
	
	public static void main(String[] args) {
		
		//ServerHandlerTester test = new ServerHandlerTester();
		//test.runTests();

		//create the handler. The socket to receive is opened upon construction
		ServerHandler server = new ServerHandler();
		
		server.run();
	}
}
