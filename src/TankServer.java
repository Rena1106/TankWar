import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;


public class TankServer {
	
	public static final int TCP_PORT = 8888;
	public static final int UDP_PORT =6666;
	private static int ID = 100;
	List<Client> clients = new ArrayList<Client>();
	public void start(){
		new Thread(new UdpThread()).start();;
		ServerSocket ss = null;
		try {
			ss= new ServerSocket(TCP_PORT);
			
		}
		catch (IOException e) {
			
			e.printStackTrace();
		}
			while(true){
				Socket s = null;
				try{
				s = ss.accept();
			DataInputStream dis = new DataInputStream(s.getInputStream());
			String IP = s.getInetAddress().getHostAddress();
			int udp_Port = dis.readInt();
			Client c = new Client(IP,udp_Port);
			clients.add(c);
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeInt(ID++);
			
System.out.println("Server Connected,Address:"+s.getInetAddress()+"-"+s.getPort());
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			
		} 
	}

	public static void main(String[] args) {
	
		new TankServer().start();

	}
	private class Client{
		String IP;
		int udp_Port;
		public Client(String IP, int udp_Port){
			this.IP = IP;
			this.udp_Port =udp_Port;
		}
		
	}
	private class UdpThread implements Runnable{
		byte[] buf = new byte[1024];
		public void run() {
			DatagramSocket ds = null;
			try {
				ds = new DatagramSocket(UDP_PORT);
			} catch (SocketException e) {
				e.printStackTrace();
			}
System.out.println("UDP Thread started at Port:"+UDP_PORT);
			while(ds!= null){
				DatagramPacket dp = new DatagramPacket(buf,buf.length);
				try {
					ds.receive(dp);
					for(int i=0;i<clients.size();i++){
						Client c = clients.get(i);
						dp.setSocketAddress(new InetSocketAddress(c.IP,c.udp_Port));
						ds.send(dp);
					}

System.out.println("Packet Recived");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}

}
