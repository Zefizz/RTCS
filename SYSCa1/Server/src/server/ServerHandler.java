package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class ServerHandler {
	private DatagramSocket dsock;
	private final int port = 8889;
	
	public ServerHandler() {
		createSocket();
	}
	/**
	 * initialize the sockets. Exit program on failure to initialize
	 */
	private void createSocket() {
		try {
			//create socket on the port for reading/writing
			dsock = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
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
	 * verify that the data matched the specification
	 * @param data the byte[] containing the data to be verified
	 * @return if the data matches specification
	 */
	protected boolean validatePacketData(byte[] data) {

		//verify the first two bytes are 0, followed by 1 or 2
		//data must have at least 6 bytes 0,x,x,0,x,0
		if (data.length < 6 || data[0] != 0 || (data[1] != 1 && data[1] != 2)) {
			return false;
		}
		
		//read the filename until the next 0
		int i = 2;
		while (i < data.length && data[i] != 0) {
			i++;
		}
		//invalid if there was no text to read / end of array (no 0)
		if (i == 2 || i == data.length) return false;
		
		//read the mode text until the next 0
		int j = i + 1;
		while (j < data.length && data[j] != 0) {
			j++;
		}
		//invalid if there was no text to read / end of array (no 0)
		if (j == i+1 || j == data.length) return false;

		//there should be no more data after the last read
		//the rest of the buffer should contain 0's
		while (j < data.length) {
			if (data[j++] != 0)
				return false;
		}
		return true;
	}
	
	/**
	 * validate the data and return the response
	 * in the form of a datagram packet
	 * @param pack the received datagram
	 * @return the response data to be sent
	 * @throws Exception if the packet is invalid
	 */
	public DatagramPacket handleIncomingPacket(DatagramPacket pack) throws Exception {
		byte[] data = {};
		if (validatePacketData(pack.getData())) {
			if (pack.getData()[1] == 1)
				data = new byte[] {0,3,0,1};
			else if (pack.getData()[1] == 2)
				data = new byte[] {0,4,0,0};
		} else {
			throw new Exception("invalid packet");
		}

		return new DatagramPacket(data,data.length,pack.getAddress(),pack.getPort());
	}
	
	/**
	 * formulate the response to a packet received from a client
	 * @param pack the datagram packet from client
	 */
	public void respondTo(DatagramPacket pack) {
		//create the response datagram packet
		DatagramPacket response = null;
		try {
			response = handleIncomingPacket(pack);
		} catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}

		//create new socket to send back the response
		DatagramSocket replySock = null;
		try {
			replySock = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		//send the packet over the new socket back to the client
		try {
			replySock.send(response);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void run() {
		DatagramPacket received;
		
		//repeat *forever*
		while (true) {
			received = receivePacket();
			respondTo(received);
		}
	}
}
