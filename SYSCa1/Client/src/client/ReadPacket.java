package client;

public class ReadPacket extends DPacket{

	public ReadPacket(String filename, String mode) {
		this.filename = filename;
		this.mode = mode;
		writePacketBytes();
		bytes[1] = 1;
		System.out.println(this);
	}
}
