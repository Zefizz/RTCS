package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

public class ServerHandler implements Runnable {
	private DatagramSocket sock;
	private DatagramPacket received;
	
	public ServerHandler(DatagramPacket pack) {
		createSocket();
		this.received = pack;
		System.out.println("handler running on port " + sock.getLocalPort() +
							"\n++++++++++++++++++++++++++++++++++++++++++++++\n");
	}
	/**
	 * initialize the sockets. Exit program on failure to initialize
	 */
	private void createSocket() {
		try {
			//create socket on the port for reading/writing
			sock = new DatagramSocket();
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
		System.out.println("data:\t" + Arrays.toString(data.getBytes()));
		System.out.println(new String(data.getBytes()) + "\n");
	}
	
	/**
	 * listen on the socket to receive a datagram packet
	 * and display the contained data
	 * @return the packet received
	 */
	private DatagramPacket receivePacket() {
		//initialize the buffer
		int length = 127;
		byte[] buffer = new byte[length];
		DatagramPacket pack = new DatagramPacket(buffer, length);
		
		try {
			//listen on the socket for incoming datagram
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
	 * verify that the data matched the specification
	 * @param data the byte[] containing the data to be verified
	 * 		  this byte[] may include trailing 0's from the buffer
	 * @param dataLength the length of the message. This is <= the length of byte[]
	 * 		  not including trailing 0's from the buffer
	 * @return true/false if the data matches specification
	 */
	protected boolean validatePacketData(byte[] data, int dataLength) {

		//verify the first two bytes are 0, followed by 1 or 2
		//data must have at least 6 bytes 0,x,x,0,x,0
		if (dataLength < 6 || data[0] != 0 || (data[1] != 1 && data[1] != 2)) {
			return false;
		}
		
		//read the filename until the next 0
		int i = 2;
		while (i < dataLength && data[i] != 0) {
			i++;
		}
		//invalid if there was no text to read / end of array (no 0)
		if (i == 2 || i == dataLength) return false;
		
		//read the mode text until the next 0
		int j = i + 1;
		while (j < dataLength && data[j] != 0) {
			j++;
		}
		//invalid if there was no text to read / end of array (no 0)
		if (j == i+1 || j == dataLength) return false;

		//read to end of the message with no specification violations
		return true;
	}
	
	/**
	 * validate the data and return the response
	 * in the form of a datagram packet
	 * @param pack the received datagram
	 * @return the response datagram packet to be sent
	 * @throws Exception if the packet is invalid
	 */
	public DatagramPacket handleIncomingPacket(DatagramPacket pack) throws Exception {
		byte[] data = {};
		
		//check that the packet is valid. If so, set data to be the appropriate response
		if (validatePacketData(pack.getData(),pack.getLength())) {
			
			//response to send if the packet is a read request
			if (pack.getData()[1] == 1) {
				data = new byte[] {0,3,0,1};
			//response to send if the packet is a write request
			} else if (pack.getData()[1] == 2) {
				data = new byte[] {0,4,0,0};
			}
			
		} else {
			//the packet was invalid. the server throws an exception and quits
			throw new Exception("invalid packet");
		}
		
		//return a new datagram packet addressed to the sender, containing the response
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
			//quit if an exception was thrown ie packet was invalid
			System.out.println(e);
			e.printStackTrace();
			System.exit(1);
		}
		
		//print out the information about the new packet
		System.out.println("created datagram packet to: "
							+ response.getAddress() + ":" + response.getPort());
		System.out.println("containing " + response.getLength() + " bytes of data");
		System.out.println("data:\t" + Arrays.toString(response.getData()));
		System.out.println(new String(response.getData()) + "\n");
		
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
		
		//close the send socket created for this request
		replySock.close();
	}
	
	public void run() {
		respondTo(received);
	}
}
