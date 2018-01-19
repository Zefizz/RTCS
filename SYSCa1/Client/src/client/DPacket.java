package client;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public abstract class DPacket {
	protected String filename;
	protected String mode;
	protected byte[] bytes;
	protected DatagramPacket datagram;
	
	protected void writePacketBytes() {
		byte[] fileBytes = filename.getBytes();
		byte[] modeBytes = mode.getBytes();
		bytes = new byte[fileBytes.length + modeBytes.length + 4];

		int i = 2;
		for (byte b : fileBytes) {
			bytes[i++] = b;
		}
		bytes[i++] = 0;
		for (byte b : modeBytes) {
			bytes[i++] = b;
		}
		bytes[i++] = 0;
	}
	
	/**
	 * invalidate packet by changing the last bit to a non-zero value
	 */
	protected void invalidate() {
		bytes[bytes.length-1] = 1;
	}
	
	public String toString() {
		String str = new String("Created new packet\ndata:\t\t");
		str += Arrays.toString(bytes) + '\n';
		str += "filename:\t" + filename + '\n';
		str += "mode:\t\t" + mode + "\n";
		return str;
	}
	
	public DatagramPacket createDatagram(InetAddress addr, int port) {
		if (datagram == null) {
			datagram = new DatagramPacket(bytes, bytes.length,addr,port);
		}
		return datagram;
	}
}
