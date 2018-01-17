package server;

public class Server {
	
	public static void main(String[] args) {
		
		//ServerHandlerTester test = new ServerHandlerTester();
		//test.runTests();
		
		ServerHandler server = new ServerHandler();
		server.run();
	}
}
