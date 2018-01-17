package server;

public class ServerHandlerTester extends ServerHandler {

	//test the validatePacketData()
	public void runTests() { 
		
		byte[] dataa = {0, 2, 116, 101, 115, 116, 46, 116, 120, 116, 0, 110, 69, 116, 65, 115, 67, 105, 73, 0};
		byte[] data0 = {0,1,1,0,1,0};			//valid (minimum)
		byte[] data1 = {0,1,32,56,4,0,21,0};	//valid
		byte[] data2 = {0,2,32,0,4,0};			//valid
		byte[] data3 = {0,0,3,0,43,35,6,0};		//starting invalid
		byte[] data4 = {1,1,54,23,0,54,0};		//starting invalid
		byte[] data5 = {0,1,0,32,69,0};			//no filename
		byte[] data6 = {0,1,4,22,0};			//no mode
		byte[] data7 = {0,1,4,22,0,0};			//no mode
		byte[] data8 = {0,1,2,0,4,0,42};		//stuff at end
		byte[] data9 = {0,1,32};				//end after filename
		byte[] data10 = {0,1,32,0,6};			//end after mode

		assert validatePacketData(dataa) == true;
		assert validatePacketData(data0) == true;
		assert validatePacketData(data1) == true;
		assert validatePacketData(data2) == true;
		assert validatePacketData(data3) == false;
		assert validatePacketData(data4) == false;
		assert validatePacketData(data5) == false;
		assert validatePacketData(data6) == false;
		assert validatePacketData(data7) == false;
		assert validatePacketData(data8) == false;
		assert validatePacketData(data9) == false;
		assert validatePacketData(data10) == false;
		
		System.out.println("all tests completed");
	}
}
