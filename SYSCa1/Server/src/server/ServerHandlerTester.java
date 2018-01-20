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
		byte[] data11 = {0, 1, 97, 115, 100, 102, 46, 100, 97, 116, 0, 49, 50, 51, 52, 1};	//no terminating 0
		byte[] data12 = {1,2,0,54,0,0,0};		//there is more stuff after the end

		assert validatePacketData(dataa,dataa.length) == true;
		assert validatePacketData(data0,data0.length) == true;
		assert validatePacketData(data1,data1.length) == true;
		assert validatePacketData(data2,data2.length) == true;
		assert validatePacketData(data3,data3.length) == false;
		assert validatePacketData(data4,data4.length) == false;
		assert validatePacketData(data5,data5.length) == false;
		assert validatePacketData(data6,data6.length) == false;
		assert validatePacketData(data7,data7.length) == false;
		assert validatePacketData(data8,data8.length) == false;
		assert validatePacketData(data9,data9.length) == false;
		assert validatePacketData(data10,data10.length) == false;
		assert validatePacketData(data11,data11.length) == false;
		assert validatePacketData(data12,data12.length) == false;
		
		System.out.println("all tests completed");
	}
}
