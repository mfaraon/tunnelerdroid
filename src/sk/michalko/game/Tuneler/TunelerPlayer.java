package sk.michalko.game.Tuneler;

import javax.microedition.lcdui.game.Sprite;


public class TunelerPlayer {   
    
	public int tX; // tile coords
	public int tY;  
	private int dir;
	private TunelerTank myTank; 
	private boolean dead=false;
	private boolean fired=false;
	 
	public TunelerPlayer(int x,int y,int dir) 
	{
		tX=x;
		tY=y;
		this.dir=dir;
		/* This has to be hanled by rendering engine
		try{
			Image tankImage = Image.createImage("/tankmap.png");
			myTank = new TunelerTank(tankImage,dir);
		}
		catch (Exception e){
			System.out.println("ops: TunelerPlayer :  "+e.getMessage());
		}
		*/
	}
	
	public boolean fire()
	{
		if (!dead)
		{
			if (!fired)
			{
				fired=true;
				return true;
			}
		}
		return false;	
	}
	
	public void onHit()
	{
		dead = true;
		//myTank.explode();	
	}
	
	public void onTick()
	{
		fired=false;
		//myTank.moveTo(tX,tY);
		//myTank.animIt();	
		//System.out.println("Player :"+ tX +" "+tY);

	}
	
	public void setDir(int dir)
	{
		this.dir=dir;
		//myTank.setDir(dir);
	}
	
	public int getDir()
	{
		return dir;
	}
	
	public void moveUp()
	{
		if (tY>0) tY--;
		setDir(0);				
		System.out.println("Player :"+ tX +" "+tY);
	}
	public void moveDown()
	{
		tY++;
		setDir(2);				
		System.out.println("Player :"+ tX +" "+tY);
	}
	public void moveLeft()
	{
		if (tX>0) tX--;				
		setDir(3);				
		System.out.println("Player :"+ tX +" "+tY);
	}
	public void moveRight()
	{
		tX++;				
		setDir(1);				
		System.out.println("Player :"+ tX +" "+tY);
	}
	
	public Sprite getSprite()
	{
		return myTank.getSprite();
	}
}
