package intermediate;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.xml.crypto.Data;

public class RelayHandler {
	private DatagramSocket recvSock;
	private DatagramSocket sendRecvSock;
	private final int recvPort = 23;
	private final int serverPort = 69;
	
	public RelayHandler() {
		//the sockets are opened during construction
		createSockets();
		System.out.println("relay listening on port " + recvPort +
							" and " + sendRecvSock.getLocalPort() +
							"\n++++++++++++++++++++++++++++++++++++++++++++++\n");
	}
	
	/**
	 * initialize the sockets to send and send/receive
	 * Exit program on failure to initialize
	 */
	private void createSockets() {
		try {
			//create socket on the port for receiving
			//and the send/receive socket on an unspecified port
			recvSock = new DatagramSocket(recvPort);
			sendRecvSock = new DatagramSocket();
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
		System.out.println("containing " + pack.getLength() + " bytes of data");;
		System.out.println("data:\t" + Arrays.toString(data.getBytes()));
		System.out.println(new String(data.getBytes()) + "\n");
	}
	
	/**
	 * listen on the receive socket to receive a datagram packet
	 * and display the contained data
	 * @param sock the socket used to receive the packet
	 * @return the packet received
	 */
	private DatagramPacket receivePacket(DatagramSocket sock) {
		//initialize the buffer
		int length = 127;
		byte[] buffer = new byte[length];
		DatagramPacket pack = new DatagramPacket(buffer, length);
		
		try {
			//listen on the receive socket for incoming datagram
			sock.receive(pack);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		//display the packet info and return the packet
		printData(pack);
		return pack;
	}

	/**
	 * copy a datagram packet to be sent to the specified port 
	 * @param pack the packet to be rerouted
	 * @param port the new destination port
	 * @return DatagramPacket to be sent
	 */
	private DatagramPacket createRedirectPacket(DatagramPacket pack,int port) {
		DatagramPacket relayPacket = null;
		
		//copy the data into a new byte array with the same size
		//as the received data, without the extra buffer
		byte[] noBufferData = Arrays.copyOf(pack.getData(),pack.getLength());
		
		try {
			//new packet copies the data of pack, changes the port
			relayPacket = new DatagramPacket(
					noBufferData,noBufferData.length,
					InetAddress.getLocalHost(),port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		//print out the information about the new packet
		System.out.println("created datagram packet to: "
							+ relayPacket.getAddress() + ":" + relayPacket.getPort());
		System.out.println("containing " + relayPacket.getLength() + " bytes of data");;
		System.out.println("data:\t" + Arrays.toString(relayPacket.getData()));
		System.out.println(new String(relayPacket.getData()) + "\n");

		return relayPacket;
		
	}
	
	/**
	 * relay the packet to the server, and then wait for a response
	 * from the server to send back to the client
	 * inter -> server and inter -> client handled here
	 * @param pack the datagram packet to be relayed
	 */
	private void relayPacket(DatagramPacket pack) {
		//remember which port the client is running on
		int sourcePort = pack.getPort();
		
		//send the packet to the server by copying the data,
		//and changing the port to be that of the server. Print the resulting packet info
		DatagramPacket packToServer = createRedirectPacket(pack,serverPort);
		try {
			//send the packet to the server on the send/receive socket
			sendRecvSock.send(packToServer);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		//wait on the socket to receive a response from the server
		DatagramPacket packFromServer = receivePacket(sendRecvSock);
		
		//similarly, copy the data into a new DatagramPacket addressed to the original client
		//the packet is printed before being sent
		DatagramPacket response = createRedirectPacket(packFromServer,sourcePort);
		
		try {
			//send the data from the server back to the client on the send/receive socket
			sendRecvSock.send(response);
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
			//listen for a packet from the client
			received = receivePacket(recvSock);
			//do the work of relaying to server and relaying the server response to the client
			relayPacket(received);
		}
	}
}
