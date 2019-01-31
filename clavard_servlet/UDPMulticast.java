package clavard_servlet;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class UDPMulticast {
			
	private DatagramSocket dgramSocket_;
	private ClavardServlet servlet_=null;	
	
	UDPMulticast(ClavardServlet servlet) {
		servlet_=servlet;
		try {
		    dgramSocket_ = new DatagramSocket();
		} catch(SocketException e) {
		    e.printStackTrace();
		}
			System.out.println("UDPMulticast : crée");
	}
	
	/*on multicast le message udp avec la liste des internautes */
	public void multicastMessageUDP(String message) {
		for (Internaute i : servlet_.getInternautes()) {
            sendMessageUDP(message, i.getAddress(), i.getUDPPort());
        }
	}
	
	public void sendMessageUDP(String message, InetAddress address, int port) {
		try {
		    DatagramPacket outPacket = new DatagramPacket(message.getBytes(), message.length(), address, port);
		    dgramSocket_.send(outPacket);

		    //récéption
		    //byte[] buffer = new byte[256];
		    //DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
		    //dgramSocket_.receive(inPacket);
		    //String response = new String(inPacket.getData(), 0, inPacket.getLength());
		    //System.out.println(response);
		    //dgramSocket_.close();
		} catch(Exception e) {
		    e.printStackTrace();
		}
	}
	
	
	
}
