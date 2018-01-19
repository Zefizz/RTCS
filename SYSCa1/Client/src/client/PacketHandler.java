package client;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.io.IOException;

public class PacketHandler {
	private DatagramSocket dsock;
	private final int sendPort = 8447;
	
	/**
	 * the socket is opened during construction
	 */
	public PacketHandler() {
		createSocket();
		System.out.println("client running on port " + dsock.getLocalPort() + 
							"\n++++++++++++++++++++++++++++++++++++++++++++++\n");
	}
	
	/**
	 * initialize the socket. Exit program on failure to initialize
	 */
	private void createSocket() {
		try {
			//create socket on unspecified port for reading/writing
			dsock = new DatagramSocket();
			//timeout after 10 seconds
			dsock.setSoTimeout(10000);
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * print data about a datagram packet. Data includes sender address and port,
	 * and the contents of the packet viewed as an array
	 * @param pack the datagram packet to view
	 */
	private void printData(DatagramPacket pack) {
		String data = new String(pack.getData(),0,pack.getLength());
		System.out.println("got packet from " + pack.getAddress() + ":" + pack.getPort());
		System.out.println("containing " + pack.getLength() + " bytes of data");
		System.out.println("data:\t" + Arrays.toString(data.getBytes()) + "\n");
	}

	/**
	 * listen on the socket to receive a datagram packet
	 * and display the contained data
	 */
	private void receivePacket() {
		//initialize the buffer
		int length = 127;
		byte[] buffer = new byte[length];
		DatagramPacket pack = new DatagramPacket(buffer, length);
		
		try {
			//listen on the socket for incoming datagram
			dsock.receive(pack);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		//print the data
		printData(pack);	
	}

	/**
	 * the driving loop of the client. create and send 10 alternating 
	 * write/read datagrams to the intermediate, followed by an invalid datagram
	 * the socket waits on a response after each send
	 */
	public void run() {
		DPacket sendPacket;
		
		try {
			//send alternating read and write packets to the intermediate
			//wait for a response before each send
			for (int i=0; i<5; ++i) {
				
				//create the datagram packet. the data is printed when created
				sendPacket = new WritePacket("backup.sql","nEtAsCiI");
				//send the packet to the intermediate host on the well-known port
				dsock.send(sendPacket.createDatagram(InetAddress.getLocalHost(),sendPort));
				//wait on datagram socket for incoming packet and print the info received
				receivePacket();
				
				//same as above, but sends a read request
				sendPacket = new ReadPacket("banlist.txt","octet");
				dsock.send(sendPacket.createDatagram(InetAddress.getLocalHost(),sendPort));
				receivePacket();
			}
			//send invalid request
			sendPacket = new ReadPacket("asdf.dat","tetco");
			sendPacket.invalidate();
			dsock.send(sendPacket.createDatagram(InetAddress.getLocalHost(),sendPort));
			
		} catch (IOException e) {
			//IOExection occurred, likely a timeout
			System.out.println(e);
			System.exit(1);
		}
		
		//close the socket when run is competed
		dsock.close();
	}
}
