import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class TankClient extends Frame{
	
	public static final int WINDOW_WIDTH = 800;
	public static final int WINDOW_HEIGHT = 600;
	
	Tanks myTank = new Tanks(50,50,true,Dir.Stop,this);
	//Tanks enemyTank = new Tanks(100,100,false,this);
	List<Explode> explodes = new ArrayList<Explode>(); 
	List<Missile> missiles = new ArrayList<Missile>();
	List<Tanks> tanks = new ArrayList<Tanks>();
	NetConnection nc = new NetConnection(this);
	ConDialog dialog = new ConDialog();
	
	Image offScreenImage = null;
	public void paint(Graphics g){
	
	g.drawString("Missile Count:"+missiles.size(), 10, 50);
	g.drawString("Explode Count:"+explodes.size(),10,70);
	g.drawString("Tanks Count:"+tanks.size(),10,90);
	
	for(int i = 0; i<missiles.size();i++){
		Missile m = missiles.get(i);
		//if(!m.isAlive()) missiles.remove(m);
		//m.hitTank(enemyTank);
		//m.hitTanks(tanks);
		if(m.hitTank(myTank)){
			TankDeadMsg msg = new TankDeadMsg(myTank.id);
			nc.send(msg);
			MissileDeadMsg mdm = new MissileDeadMsg(m.tankId,m.id);
			nc.send(mdm);
		}
		m.draw(g);
	}
	for(int i = 0;i<explodes.size();i++){
		Explode e = explodes.get(i);
		e.draw(g);
	}
	for (int i = 0;i<tanks.size();i++){
		Tanks t = tanks.get(i);
		t.draw(g);
	}
	myTank.draw(g);
	//enemyTank.draw(g);
	
	
	
}
	public void update(Graphics g){
		if (offScreenImage==null){
			offScreenImage = this.createImage(WINDOW_WIDTH,WINDOW_HEIGHT);
		}
		Graphics gOffScreenImage = offScreenImage.getGraphics();
		Color c = gOffScreenImage.getColor();
		gOffScreenImage.setColor(Color.GREEN);
		gOffScreenImage.fillRect(0,0,WINDOW_WIDTH,WINDOW_HEIGHT);
		gOffScreenImage.setColor(c);
		paint(gOffScreenImage);
		g.drawImage(offScreenImage,0,0,null);
		
	}
	public void launchFrame(){
		
//		for(int i = 0;i<10;i++){
//			tanks.add(new Tanks(50+40*(i+1),50,false,Dir.D,this));
//		}
		
		this.setLocation(400,300);
		this.setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
		this.addWindowListener(new WindowAdapter(){
		public void windowClosing(WindowEvent e){
			System.exit(0);
		}
	});
		this.setTitle("TankWar");
		this.setVisible(true);
		this.setBackground(Color.GREEN);
		this.setResizable(false);
		
		this.addKeyListener(new KeyMonitor());
		
		new Thread(new PaintThread()).start();
		nc.getConnection("127.0.0.1", TankServer.TCP_PORT);
}
	public static void main(String[] args){
		TankClient tc = new TankClient();
		tc.launchFrame();
}
	private class PaintThread implements Runnable{
		public void run(){
			while(true){
			repaint();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			}
		}
	}
	private class KeyMonitor extends KeyAdapter{
	

		public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_C){
			dialog.setVisible(true);
		}
		else{	
			myTank.keyPressed(e);
          }
			
		}
		public void keyReleased(KeyEvent e) {
			
			myTank.keyReleased(e);
		}
		
	}
	class ConDialog extends Dialog{
		Button b = new Button("OK");
		TextField tfIP = new TextField("127.0.0.1",12);
		TextField tfPort = new TextField(""+TankServer.TCP_PORT,4);
		TextField tfMyUdpPort = new TextField("2223",4);


		public ConDialog(){
			super(TankClient.this,true);
			this.setLayout(new FlowLayout());
			this.add(new Label("IP:"));
			this.add(tfIP);
			this.add(new Label("Port:"));
			this.add(tfPort);
			this.add(new Label("My UDP Port:"));
			this.add(tfMyUdpPort);
			this.add(b);
			this.setLocation(300,300);
			this.pack();
			this.addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent e) {
					setVisible(false);			}
				});
			b.addActionListener(new ActionListener(){

				
				public void actionPerformed(ActionEvent e) {
				  
				  String IP = tfIP.getText().trim();
				  int port = Integer.parseInt(tfPort.getText().trim());
				  int myUdpPort = Integer.parseInt(tfMyUdpPort.getText().trim());
				  nc.setUdp_Port(myUdpPort);
				  nc.getConnection(IP, port);
				  setVisible(false);
				}
				
			});
			
		}
	}
}
