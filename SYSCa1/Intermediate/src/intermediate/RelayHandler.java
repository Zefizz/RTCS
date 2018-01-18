package intermediate;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class RelayHandler {
	private DatagramSocket dsock;
	private final int recvPort = 8447;
	private final int serverPort = 8889;
	
	public RelayHandler() {
		createSocket();
	}
	
	/**
	 * initialize the socket on the receiving port. Exit program on failure to initialize
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
	
	/**
	 * display the contents of an incoming packet, and information about the sender
	 * @param pack the datagram packet to print
	 */
	private void printData(DatagramPacket pack) {
		String data = new String(pack.getData(),0,pack.getLength());
		System.out.println("got packet from " + pack.getAddress() + ":" + pack.getPort());
		System.out.println("data:\t" + Arrays.toString(data.getBytes()) + "\n");
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
	
	/**
	 * copy a datagram packet to be sent to the specified port 
	 * @param pack the packet to be rerouted
	 * @param port the new destination port
	 * @return DatagramPacket to be sent to the new port
	 */
	private DatagramPacket createRedirectPacket(DatagramPacket pack,int port) {
		DatagramPacket relayPacket = null;
		
		//copy the data into a new byte array with the same size
		//as the received data, without the extra buffer
		String data = new String(pack.getData(),0,pack.getLength());
		byte[] bytes = data.getBytes();
		
		try {
			//new packet copies the data of pack, changes the port
			relayPacket = new DatagramPacket(
					bytes,bytes.length,
					InetAddress.getLocalHost(),port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return relayPacket;
	}
	
	//relay the packet to the server
	private void relayPacket(DatagramPacket pack) {
		int sourcePort = pack.getPort();
		
		//send the packet to the server by copying the data, and changing the target port to be the server port
		DatagramPacket packToServer = createRedirectPacket(pack,serverPort);
		try {
			dsock.send(packToServer);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		//wait on the socket to receive a response from the server
		DatagramPacket packFromServer = receivePacket();
		
		//similarly, copy the data into a new DatagramPacket addressed to the original sender
		DatagramPacket response = createRedirectPacket(packFromServer,sourcePort);
		try {
			dsock.send(response);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * main loop of intermediate. each loop handles routing of packet to server, 
	 * and returning server response packet to the original sender
	 */
	public void run() {
		DatagramPacket received;
		//repeat *forever*
		while (true) {
			//listen for a packet
			received = receivePacket();
			//do the work of relaying to server and relaying the server response
			relayPacket(received);
		}
	}
}
