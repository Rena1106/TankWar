import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class TankDeadMsg implements Msg {
	int msgType = Msg.TANK_DEAD_MSG;
	int id;
	TankClient tc;
	public TankDeadMsg(int id){
		this.id = id;
	}
	public TankDeadMsg(TankClient tc){
		this.tc = tc;
	}
	
	public void send(DatagramSocket ds, String IP, int udp_Port) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(id);
		
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] buf = baos.toByteArray();
		
		try {
			DatagramPacket dp = new DatagramPacket(buf, buf.length,new InetSocketAddress(IP,udp_Port));
			ds.send(dp);
		}catch(SocketException e){
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public void parse(DataInputStream dis) {
		try {
			int id = dis.readInt();
			if(tc.myTank.id == id){
				return;
			}
		
			for(int i=0; i<tc.tanks.size();i++){
				Tanks t = tc.tanks.get(i);
				if(t.id == id){
					t.setLive(false);
					break;
				}
			}
//System.out.println("id:"+id+"--"+"x:"+x+"--"+"y:"+y+"--"+"dir:"+dir+"--"+"good:"+good);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
