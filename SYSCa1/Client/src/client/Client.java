package client;

import java.util.ArrayList;

public class Client {
	public static void main(String[] args) {

		//create the handler. The socket to send/receive is opened upon construction
		ArrayList<Thread> tarr = new ArrayList<Thread>();
		
		//simulate multiple client connections spamming requests
		for (int i=0; i<20; ++i) {
			tarr.add(new Thread(new PacketHandler()));
		}
		for (Thread t : tarr) {
			t.start();
		}
	}

}
