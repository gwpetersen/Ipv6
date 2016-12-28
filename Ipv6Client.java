
/**
 * Garett Petersen
 * 2/10/2016
 * CS380
 * Project4
 * 
 * This program was basically an easier version of the ipv4 project
 * mainly because we didn't have to implement checksum. The purpose was 
 * to implement specified fields of an ipv6 packet. Overall was a success 
 * and much easier to figure out once you get the ipv4 figured out.
 * 
 * 
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random; // data cannot be 0 it must be random data

import javax.xml.bind.DatatypeConverter;


public class Ipv6Client {
	private final static short HEADERSIZE = 40;
	static byte[] destip;

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		String host = "cs380.codebank.xyz";
		int port = 38004;
		short counter = 1;
		short DATASIZE = 2;
		
		
		try (Socket socket = new Socket(host, port)) { 
			InputStream socketIn = (socket.getInputStream());
			PrintStream out = new PrintStream(socket.getOutputStream());
			System.out.println("Connected to " + host + ":" + port + "!\n");
			InetAddress ServerIP = socket.getInetAddress();
			destip = ServerIP.getAddress();
			
			
			while(counter < 13) { //send 12 packets
				byte[] packet = buildPacket(DATASIZE);
				out.write(packet);
				byte[] b = new byte[4];
				int count = socketIn.read(b);
				System.out.println(counter + ") " + DatatypeConverter.printHexBinary(b));
				DATASIZE *= 2;
				counter++;
			}
			System.out.println("\nAll packets sent...Closing connection");
			socketIn.close();
			out.close();
		} //end try
		System.exit(0);
	}
	
	private static byte[] buildPacket(short DATASIZE) { 
		byte packet[] = new byte[HEADERSIZE + DATASIZE];
		byte data[] = new byte[DATASIZE];
		new Random().nextBytes(data); // data cannot be just zeros
		
		packet[0] = 0b0110; //ip version 6
		packet[0] <<= 4;	//dont implement traffic class
		
		packet[1] = 0b0;	//dont implement flow label
		packet[2] = 0b0;
		packet[3] = 0b0;
		packet[4] = 0b0;	//end flow label
		
		short payload = DATASIZE;
		byte byte2 = (byte) ((payload >>> 8) & 0xFF);
		byte byte1 = (byte) (payload & 0xFF); 
		packet[4] = byte2;
		packet[5] = byte1;
		
		packet[6] = 0x11; //udp
		
		packet[7] = 0b010100; //ttl = 20
		
		for (int i=8; i<18; ++i) //source address
			packet[i] = 0b0;
		packet[18] = (byte)0xFF;	//1111 Implement assuming it is a valid IPv4 address that has been 
		packet[19] = (byte)0xFF;	//1111 extended to IPv6 for a device that does not use IPv6
		packet[20] = 0b0; //0
		packet[21] = 0b0;		//0
		packet[22] = 0b01; //1
		packet[23] = 0b01; //1	*END SOURCE ADDR*
		
		// destip contain the ip address of the server...obtained above
		
		for(int i=24; i<34; ++i)  //destination address - same implementation as source
			packet[i] = 0b0;
		packet[34] = (byte) 0xFF; 
		packet[35] = (byte) 0xFF;
		packet[36] = destip[0]; //52
		packet[37] = destip[1]; //33
		packet[38] = destip[2]; //131
		packet[39] = destip[3]; //161- end destination address
		
		for(int i=0; i<DATASIZE; ++i)
			packet[HEADERSIZE + i] = data[i]; 
		
		return packet;
	}
}