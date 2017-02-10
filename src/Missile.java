import java.awt.*;
import java.util.List;

public class Missile {
	int x,y;
	public static final int XSPEED = 10;
	public static final int YSPEED = 10;
	public static final int WEIGHT = 10;
	public static final int HEIGHT = 10;
	
	public boolean alive = true;
	private TankClient tc;
	public boolean good;
	private static int ID = 1;
	int tankId;
	int id;
	
	public boolean isAlive() {
		return alive;
	}
	Dir dir;
	public Missile(int tankId,int x,int y,Dir dir){
		this.tankId = tankId;
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.id = ID++;
	}
	public Missile(int tankId,int x, int y,boolean good, Dir dir,TankClient tc){
		this(tankId,x,y,dir);
		this.good = good;
		this.tc = tc;
	}
	public void draw(Graphics g){
		if(!alive){
			tc.missiles.remove(this);
			return;
		}
		Color c = g.getColor();
		g.setColor(Color.ORANGE);
		g.fillOval(x, y, WEIGHT,HEIGHT);
		g.setColor(c);
		
		move();
	}
	private void move(){
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
		
		
		}
		if(x<0||y<0||x>TankClient.WINDOW_WIDTH||y>TankClient.WINDOW_HEIGHT){
			alive = false;
			//tc.missiles.remove(this);
		}
	}
		public Rectangle getRect(){
			return new Rectangle(x,y,WEIGHT,HEIGHT);
		}
		public boolean hitTank(Tanks t){
		if(this.alive&&t.isLive()&& this.good!=t.isGood()&&this.getRect().intersects(t.getRect())){
			this.alive = false;
			t.setLive(false);
			Explode e = new Explode(x,y,tc);
			tc.explodes.add(e);
			return true;
		}	
		return false;
		}
		public boolean hitTanks(List<Tanks> tanks){
			for(int i=0;i<tanks.size();i++){
				if(hitTank(tanks.get(i)))
					{return true;}
			}
			return false;
		}
}
