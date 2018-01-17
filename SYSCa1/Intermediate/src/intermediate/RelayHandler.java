package intermediate;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class RelayHandler {
	private DatagramSocket dsock;
	private final int recvPort = 8447;
	private final int serverPort = 8889;
	
	public RelayHandler() {
		createSocket();
	}
	
	/**
	 * initialize the sockets. Exit program on failure to initialize
	 */
	private void createSocket() {
		try {
			//create socket on the port for reading/writing
			dsock = new DatagramSocket(recvPort);
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void printData(DatagramPacket pack) {
		System.out.println("got packet from " + pack.getAddress() +
							":" + pack.getPort());
		System.out.println("data:\t" + new String(pack.getData()) + "\n");
	}
	
	/**
	 * receive a datagram packet and display the contained data
	 * @return the packet received
	 */
	private DatagramPacket receivePacket() {
		int length = 128;
		byte[] buffer = new byte[length];
		DatagramPacket pack = new DatagramPacket(buffer, length);
		
		try {
			//listen on the socket for incoming datagram
			dsock.receive(pack);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		printData(pack);
		return pack;
	}
	
	//relay the packet to the server
	private void relayPacket(DatagramPacket pack) {
		DatagramPacket relay = null;
		
		try {
			 relay = new DatagramPacket(
					pack.getData(),pack.getData().length,
					InetAddress.getLocalHost(),serverPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		//send the packet
		try {
			dsock.send(relay);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * 
	 */
	public void run() {
		DatagramPacket received;
		//repeat *forever*
		while (true) {
			received = receivePacket();
			relayPacket(received);
		}
	}
}
