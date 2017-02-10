import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Tanks {
	
	public int x,y;
	int id;
	public static final int XSPEED = 5;
	public static final int YSPEED = 5;
	public static final int WIDTH = 30;
	public static final int HEIGHT = 30;
	TankClient tc;
	public boolean good;
	public boolean isGood() {
		return good;
	}
	private boolean  bU= false, bD = false,bL = false, bR = false;
			       
	//enum Direction{L,lU,lD,R,rU,rD,U,D,Stop};
	
	public Dir dir = Dir.Stop;
	private Dir barrelDir = Dir.D;
	private boolean live = true;
	private static Random r = new Random();
	private int step = r.nextInt(12)+3;
	
	public boolean isLive() {
		return live;
	}
	public void setLive(boolean live) {
		this.live = live;
	}
	public  Tanks(int x, int y, boolean good) {
	
		this.x = x;
		this.y = y;
		this.good = good;
	}
	public Tanks(int x, int y, boolean good,Dir dir,TankClient tc){
		this(x,y,good);
		this.dir = dir;
		this.tc = tc;
		
	}
	public void draw(Graphics g){
		if(!live){
			if(!good){
				tc.tanks.remove(this);
			}
			return;
	    } 
		Color c = g.getColor();
		if(good)g.setColor(Color.RED);
		else g.setColor(Color.BLUE);
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.setColor(c);
		g.drawString("ID:"+id, x, y-10);
		switch(barrelDir){
		case L: g.drawLine(x+Tanks.WIDTH/2,y+Tanks.HEIGHT/2,x,y+Tanks.HEIGHT/2);
		break;
		case lU:g.drawLine(x+Tanks.WIDTH/2,y+Tanks.HEIGHT/2,x,y);
		break;
		case lD:g.drawLine(x+Tanks.WIDTH/2,y+Tanks.HEIGHT/2,x,y+Tanks.HEIGHT);
		break;
		case R: g.drawLine(x+Tanks.WIDTH/2,y+Tanks.HEIGHT/2,x+Tanks.WIDTH,y+Tanks.HEIGHT/2);
		break;
		case rU: g.drawLine(x+Tanks.WIDTH/2,y+Tanks.HEIGHT/2,x+Tanks.WIDTH,y);
	    break;
		case rD:g.drawLine(x+Tanks.WIDTH/2,y+Tanks.HEIGHT/2,x+Tanks.WIDTH,y+Tanks.HEIGHT);
		break;
		case U: g.drawLine(x+Tanks.WIDTH/2,y+Tanks.HEIGHT/2,x+Tanks.WIDTH/2,y);
		break;
		case D: g.drawLine(x+Tanks.WIDTH/2,y+Tanks.HEIGHT/2,x+Tanks.WIDTH/2,y+Tanks.HEIGHT);
		break;
		
		
		}
		
		move();
	}
	void move(){
		switch(dir){
		case L: x-=XSPEED;
		break;
		case lU:x-=XSPEED;
				y-=YSPEED;
		break;
		case lD:x-=XSPEED;
				y+=YSPEED;
		break;
		case R: x+=XSPEED;
		break;
		case rU: x+=XSPEED;
				 y-=YSPEED;
	    break;
		case rD: x+=XSPEED;
				 y+=YSPEED;
		break;
		case U: y-=YSPEED;
		break;
		case D: y+=YSPEED;
		break;
		case Stop: break;
		
		}
		if(this.dir != Dir.Stop){
			this.barrelDir = this.dir;
		}
		if(x<0) x=0;
		if(y<30)y=30;
		if(x+Tanks.WIDTH>TankClient.WINDOW_WIDTH)x = TankClient.WINDOW_WIDTH - Tanks.WIDTH;
		if(y+Tanks.HEIGHT>TankClient.WINDOW_HEIGHT)y=TankClient.WINDOW_HEIGHT - Tanks.HEIGHT;
		
		/*if(!good){
			Dir [] dirs = dir.values();
			if(step ==0){
			step = r.nextInt(12)+3;
			int rn = r.nextInt(dirs.length);
			dir = dirs[rn];
			
			}
		
			step--;
			if(r.nextInt(40)>35)this.fire();
		}*/
	}
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		switch(key){
		
			
		case KeyEvent.VK_RIGHT:bR = true;
		break;
		case KeyEvent.VK_UP: bU=true;
		break;
		case KeyEvent.VK_LEFT: bL=true;
		break;
		case KeyEvent.VK_DOWN: bD=true;
		break;
		
	}
		locateDirection();
}
	void locateDirection(){
		Dir oldDir = this.dir;
		if(bL && !bU && !bR && !bD) dir = Dir.L;
		else if(bR && !bU && !bL && !bD) dir = Dir.R;
		else if(bU && !bR && !bL && !bD) dir = Dir.U;
		else if(bD && !bU && !bL && !bR) dir = Dir.D;
		else if(bR && bU && !bL && !bD) dir = Dir.rU;
		else if(bR && bD && !bL && !bU) dir = Dir.rD;
		else if(bL && bU && !bR && !bD) dir = Dir.lU;
		else if(bL && bD && !bR && !bU) dir = Dir.lD;
		else if(!bR && !bU && !bL && !bD) dir = Dir.Stop;
		
		if(dir != oldDir){
			TankMovMsg msg = new TankMovMsg(id,x,y,dir);
			tc.nc.send(msg);
		}
	}
	
	public void keyReleased(KeyEvent e){
		int key = e.getKeyCode();
		switch(key){
		case KeyEvent.VK_CONTROL:
			fire();
			break;
		case KeyEvent.VK_RIGHT:bR = false;
		break;
		case KeyEvent.VK_UP: bU=false;
		break;
		case KeyEvent.VK_LEFT: bL=false;
		break;
		case KeyEvent.VK_DOWN: bD=false;
		break;
		
	}
		locateDirection();
}
	public Missile fire(){
		if(!live)return null;
		int x = this.x + Tanks.WIDTH/2 - Missile.WEIGHT/2;
		int y = this.y + Tanks.HEIGHT/2 - Missile.HEIGHT/2;
		
		Missile m = new Missile(id,x,y,good,barrelDir,this.tc);
		tc.missiles.add(m);
		MissileNewMsg msg = new MissileNewMsg(m);
		tc.nc.send(msg);
		
		return m;
	}
	public Rectangle getRect(){
		return new Rectangle(x,y,WIDTH,HEIGHT);
	}
}
