import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class NetConnection {
	TankClient tc;
	private int udp_Port;
	public int getUdp_Port() {
		return udp_Port;
	}


	public void setUdp_Port(int udp_Port) {
		this.udp_Port = udp_Port;
	}
	DatagramSocket ds = null; 
	
	public NetConnection(TankClient tc){
		this.tc = tc;
		
	}
	

	public void getConnection(String IP,int port){
		try {
			ds = new DatagramSocket(udp_Port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		    Socket s = null;
		
			try {
				s = new Socket(IP,port);
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				dos.writeInt(udp_Port);
				DataInputStream dis = new DataInputStream(s.getInputStream());
				int id = dis.readInt();
				tc.myTank.id = id;
				if(id%2 == 0)tc.myTank.good = false;
				else tc.myTank.good = true;
System.out.println("Connected to Server and Server ID:"+id);
			} catch (UnknownHostException e) {
			
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}finally{
				if(s!=null){
					try {
						s.close();
						s = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				
				}
			}
		Msg msg = new TankMessage(tc.myTank);
		send(msg);
		new Thread(new UDPRevThread()).start();
	
	}


	public void send(Msg msg) {
		msg.send(ds,"127.0.0.1",TankServer.UDP_PORT);
	}
	private class UDPRevThread implements Runnable{

		byte[] buf = new byte[1024];

		public void run() {
			while(ds!= null){
				DatagramPacket dp = new DatagramPacket(buf,buf.length);
				try {
					ds.receive(dp);
					parse(dp);
System.out.println("Packet Recived from server");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		private void parse(DatagramPacket dp){
			
			
			ByteArrayInputStream bais = new ByteArrayInputStream(buf,0,dp.getLength());
			DataInputStream dis = new DataInputStream(bais);
			int msgType = 0;
			
			try {
				 msgType = dis.readInt();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			Msg msg = null;
			switch (msgType){
			case Msg.TANK_NEW_MSG:
			
			msg = new TankMessage(NetConnection.this.tc);
			msg.parse(dis);
			break;
			case Msg.TANK_MOV_MSG:
			msg = new TankMovMsg(NetConnection.this.tc);
			msg.parse(dis);
			break;
			case Msg.MISSILE_NEW_MSG:
			msg = new MissileNewMsg(NetConnection.this.tc);
			msg.parse(dis);
			break;
			case Msg.TANK_DEAD_MSG:
			msg = new TankDeadMsg(NetConnection.this.tc);
			msg.parse(dis);
			break;
			case Msg.MISSILE_DEAD_MSG:
			msg = new MissileDeadMsg(NetConnection.this.tc);
			msg.parse(dis);
			break;

			}
		
			
			
		}
		
	}
		
}
