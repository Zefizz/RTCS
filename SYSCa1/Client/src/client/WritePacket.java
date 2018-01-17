package client;

public class WritePacket extends DPacket{

	public WritePacket(String filename, String mode) {
		this.filename = filename;
		this.mode = mode;
		writePacketBytes();
		bytes[1] = 2;
		System.out.println(this);
	}
}
