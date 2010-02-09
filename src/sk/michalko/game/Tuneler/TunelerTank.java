package sk.michalko.game.Tuneler;

import javax.microedition.lcdui.game.Sprite;

public class TunelerTank {   
    
	private int x;
	private int y;  
	private Sprite spr; 
	private int[] moveSeq = {0,1,2,3};
	private int[] upSeq = {1,1};
	private int[] burnSeq = {4,5,6,7};
	private boolean explode;
	 
	// image should contain 4 frames for 4 directions + maybe some burning anim another 4?
	public TunelerTank(javax.microedition.lcdui.Image img, int dir) 
	{
	    spr = new Sprite(img,9,9);
		spr.setFrameSequence(moveSeq);
		spr.setFrame(dir);
		explode = false;
	}
	
	public void explode()
	{
		explode = true;
		spr.setFrameSequence(burnSeq);		
	}
	
	public void setDir(int dir)
	{
		spr.setFrame(dir);	
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
