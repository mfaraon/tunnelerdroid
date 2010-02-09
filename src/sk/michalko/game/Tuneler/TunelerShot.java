package sk.michalko.game.Tuneler;

import javax.microedition.lcdui.game.Sprite;

public class TunelerShot {   
    
	public  int x;
	public  int y;  
	private Sprite spr; 
	private int[] moveSeq = {0,1,0,1};
	private boolean explode;
	private boolean active=false;
	private int dir;
	 
	// image should contain 4 frames for 4 directions + maybe some burning anim another 4?
	public TunelerShot(javax.microedition.lcdui.Image img, int dir) 
	{
		spr = new Sprite(img,9,9);
		spr.setFrameSequence(moveSeq);
		spr.setFrame(dir);
		spr.defineCollisionRectangle(0,0,9,9);
		explode = false;
		this.dir=dir;
	}
	
	public void activate(boolean act)
	{
		active=act;
		spr.setVisible(act);
	}
		
	public void explode()
	{
		explode = true;
		//spr.setFrameSequence(burnSeq);		
	}
	
	public void setDir(int dir)
	{
		spr.setFrame(dir);
		this.dir=dir;	
	}
	
	public int getDir()
	{
		return dir;	
	}

	public void animIt()
	{
		if (explode) {
			spr.nextFrame();
		}	
	}
	
	public Sprite getSprite()
	{
		return spr;
	}
	
	public void moveTo(int x,int y)
	{
		this.x=x;
		this.y=y;
		spr.setPosition(x*15+3,y*15+3);	
	}
}
