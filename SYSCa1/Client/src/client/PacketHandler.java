package client;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.io.IOException;

public class PacketHandler {
	private DatagramSocket dsock;
	private final int sendPort = 8447;
	
	/**
	 * the socket is opened during construction
	 */
	public PacketHandler() {
		createSocket();
	}
	
	/**
	 * initialize the socket. Exit program on failure to initialize
	 */
	private void createSocket() {
		try {
			//create socket on the port for reading/writing
			dsock = new DatagramSocket();
			//timeout after 10 seconds
			//dsock.setSoTimeout(10000);
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * receive a datagram packet and display the contained data
	 */
	private void receivePacket() {
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
		
		System.out.println("received packet from:\t" + pack.getAddress() + ":" + pack.getPort());
		System.out.println("data:\t" + pack.getData());	
	}

	/**
	 * the driving loop of the client. create and send 10 alternating 
	 * read/write datagrams to the intermediate, followed by an invalid datagram
	 * the socket waits on a response after each send
	 */
	public void run() {
		DPacket sendPacket;
		
		try {
			//send alternating read and write packets to the intermediate
			for (int i=0; i<5; ++i) {
				sendPacket = new WritePacket("test.txt","nEtAsCiI");
				dsock.send(sendPacket.createDatagram(InetAddress.getLocalHost(),sendPort));
				receivePacket();
				
				sendPacket = new ReadPacket("banlist.txt","octet");
				dsock.send(sendPacket.createDatagram(InetAddress.getLocalHost(),sendPort));
				receivePacket();
			}
			//send invalid request here kthnks
		} catch (IOException e) {
		}
		dsock.close();
	}
}
