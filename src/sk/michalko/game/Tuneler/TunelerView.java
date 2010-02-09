package sk.michalko.game.Tuneler;

import javax.microedition.io.Connection;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;

//import com.siemens.mp.game.Sound;

/**
 * This is basicaly game renderer and client
 * @author mmm
 *
 */
public class TunelerView extends GameCanvas implements CommandListener, Runnable
{
	private Tuneler midlet;
	private Sprite car;
	private Sprite wall;
	private Graphics buffer;
	private Command exitCommand;
	private Command burnCommand;
	private Command explodeCommand;
	private Command connectCommand;

	private TiledLayer map;
	private LayerManager layman;
	private int viewX=0;
	private int viewY=0;
	private TunelerPlayer myPlayer;
	// Map constants (tiles)
	private final int MAP_SIZE_X = 20;
	private final int MAP_SIZE_Y = 20;
	// Resource dependent constants (sizes in pixels)
	private final int TILE_SIZE_X = 15;
	private final int TILE_SIZE_Y = 15;
	// Playground - safe borders triggering screen 
	// centering (in tiles)
	private final int BORDER_X = 4;
	private final int BORDER_Y = 4;
	
	private final int MAX_SHOTS =15;
	
	// players action 
	//	0 - up
	//	1 - right
	//	2 - down
	//	3 - left
	//  4 - fire
	//	5 - wait
	private byte	playerAction	=	5;
	
	private final int PRESSED =1;
	private final int RELEASED =0;
	private int currentKey;
	private int currentKeyState;
	private boolean running=false;
	private TunelerShot[] shots;
	private int countShots=0;
	private boolean[] shotsActive;
	private final int MAX_BUF_SIZE=64;
	private Connection conn;
	private byte[]  inBuffer,outBuffer;
	
	    
	/*private static int[][] mapTiles; = {{4, 6, 6,  6,  6, 6, 6, 6},    
							{10, 13, 19, 13, 11,  6, -1,  6},
							{6, 6, 9, 6, 9, 6,  6, 6},
							{ 3, 13, 17,  13, 14, 6, -2, 6},
							{ 6, 6,  2,  6,  6, 6, 6, 6}   };*/
	private static int[][] mapGround;/* = {{4, 6, 6,  6,  6, 6, 6, 6},    
							{10, 13, 19, 13, 11,  6, -1,  6},
							{6, 6, 9, 6, 9, 6,  6, 6},
							{ 3, 13, 17,  13, 14, 6, -2, 6},
							{ 6, 6,  2,  6,  6, 6, 6, 6}   };*/


	    
	public TunelerView(Tuneler midlet)
	{
		super(false);	        
		this.midlet = midlet;
		buffer = getGraphics();
		exitCommand = new Command("exit", Command.EXIT, 1);
		burnCommand = new Command("start", Command.SCREEN, 0);
		explodeCommand = new Command("explode", Command.SCREEN, 0);
		connectCommand = new Command("connect", Command.SCREEN, 0);
		
		addCommand(exitCommand);
	
		addCommand(burnCommand);
	
		addCommand(connectCommand);
	
		addCommand(explodeCommand);

		setCommandListener(this);
	
		createGround();
		createTiledLayer();
		createLayerManager();
		createShots();
		
		inBuffer=new byte[MAX_BUF_SIZE];
		outBuffer =new byte[MAX_BUF_SIZE];
	}
	
	private void createShots()
	{
		try{
			Image shotImage = Image.createImage("/shot.png");

			shots = new TunelerShot[MAX_SHOTS];
			shotsActive = new boolean[MAX_SHOTS];
			for (int i = 0; i < MAX_SHOTS; i++)
			{
				shots[i] = new TunelerShot(shotImage,1);
				shots[i].activate(false);
				shotsActive[i]=false;
				//shots[i].setCollisionRectangle(0,0,6,3);
				layman.insert(shots[i].getSprite(),0);
			}
		}
		catch (Exception e){
			System.out.println("ops: createShots :  "+e.getMessage());
		}
	}
	
	private void createGround()
	{
		mapGround= new int[MAP_SIZE_X][MAP_SIZE_Y];
		int i,j = 0;
		        
		for (i = 0; i < MAP_SIZE_Y; i++){
			for (j = 0; j < MAP_SIZE_X; j++ )
			{
		    	mapGround[j][i]=0;
		    }
		}
	}
	
	private int getTileFromMap(int j,int i)
	{
		int l,r,u,d;
		if (j>0) l=mapGround[j-1][i]; else l=0;
		if (j<MAP_SIZE_X-1) r=mapGround[j+1][i]; else r=0;
		if (i>0) u=mapGround[j][i-1]; else u=0;
		if (i<MAP_SIZE_Y-1) d=mapGround[j][i+1]; else d=0;
		//System.out.println("debug: getTileFromMap :  "+ j +" " + i +" " + u+r*2+d*4+l*8);
		return (u+r*2+d*4+l*8);
	}
	
	private void fillTiles(TiledLayer map)
	{
		int i,j = 0;
		
		//map.fillCells(0,0,MAP_SIZE_X,MAP_SIZE_Y,17);
		try{
			for (i = 0; i < map.getRows(); i++){
				for (j = 0; j < map.getColumns(); j++ )
				{
					switch (mapGround[j][i])
					{
						case 0:         // ground               
							map.setCell(j,i,17);                        
							break;
						case 1:        // tunel
							map.setCell(j,i,getTileFromMap(j,i)+1);
							break;
					}
			        }
			}
		}
		catch (Exception e){
			System.out.println("ops: fillTiles :  "+e.getMessage());
		}
	}
	    
	    
	private void createTiledLayer()
	{
		try{
			Image mapImage = Image.createImage("/tilemap.png");
			map = new TiledLayer(MAP_SIZE_X,MAP_SIZE_Y, mapImage, 15, 15);
			fillTiles(map);
			map.createAnimatedTile(27);
			map.createAnimatedTile(29);
			//map.paint(buffer);
			//flushGraphics();
		}
		catch (Exception e){
			System.out.println("ops: createTiledLayer :  "+e.getMessage());
		}
	}
	
	private void createPlayers()
	{
		try{
			myPlayer = new TunelerPlayer(0,0,2);
			mapGround[0][0]=1;
			mapGround[1][0]=1;
			/*mapGround[2][0]=1;
			mapGround[3][0]=1;
			mapGround[3][1]=1;
			mapGround[3][2]=1;
			mapGround[3][3]=1;*/
		}
		catch (Exception e){
			System.out.println("ops: createTanks :  "+e.getMessage());
		}
		
	}
	
	private void createLayerManager()
	{
		try{
			layman = new LayerManager();
			layman.append(myPlayer.getSprite());
			layman.append(map);
			layman.paint(buffer,viewX,viewY);
			flushGraphics();
		}
		catch (Exception e){
			System.out.println("ops: createLayerManager :  "+e.getMessage());
		}
	
	}
	
	public void addShot(int x,int y,int dir)
	{
		int i=0;
		while(shotsActive[i]) i++;
		shotsActive[i]=true;
		shots[i].activate(true);
		shots[i].setDir(dir);
		shots[i].moveTo(x,y);
	}
	
	public void delShot(int i)
	{
		shotsActive[i]=false;
		shots[i].activate(false);
	}
	
	
	protected void keyPressed(int keyCode)
	{
		currentKey = keyCode;
		System.out.println("fired " + keyCode + "game: " + getGameAction(keyCode));
		//switch(keyCode)//
		switch(getGameAction(keyCode))
		{
		case Canvas.FIRE : currentKey = Canvas.FIRE; break;
		case Canvas.UP : currentKey = Canvas.UP ; break;
		case Canvas.DOWN : currentKey = Canvas.DOWN; break;
		case Canvas.RIGHT : currentKey = Canvas.RIGHT ; break;
		case Canvas.LEFT : currentKey = Canvas.LEFT; break;
		}

		currentKeyState = PRESSED;

		//if (currentKey == Canvas.FIRE) 
//bRunning = false;
	}

	protected void keyReleased(int keyCode)
	{
		System.out.println("Released " + keyCode);
		currentKey = keyCode;
		switch(getGameAction(keyCode))
		{
			case Canvas.FIRE : currentKey = Canvas.FIRE; break;
			case Canvas.UP : currentKey = Canvas.UP ; break;
			case Canvas.DOWN : currentKey = Canvas.DOWN; break;
			case Canvas.RIGHT : currentKey = Canvas.RIGHT ; break;
			case Canvas.LEFT : currentKey = Canvas.LEFT; break;
		}

		currentKeyState = RELEASED;
	}

	private void burnTrees()
	{
		try{
			if (!running) new Thread(this).start();
			running=!running;
		} catch(Exception e)	{
			System.out.println("ops: burnTrees :"+e.getMessage());
		}
	}

	private void moveShots()
	{
		//System.out.println("moving shots :");
		try{
			int i;
			for (i = 0; i < MAX_SHOTS; i++)
			{
				if (shotsActive[i])
				{
	
					// move shot in direction
					// speed is 1tile in axis
					switch(shots[i].getDir()){
						case 0: 
							if ((shots[i].y)>0) 
							{
								//move up
								shots[i].moveTo(shots[i].x,shots[i].y-1);
							}else
							{
								 delShot(i);
							}
							break;
						case 1: 
							if ((shots[i].x)<MAP_SIZE_X-1) 
							{
								//move up
								shots[i].moveTo(shots[i].x+1,shots[i].y);
							}else
							{
								 delShot(i);
							}
							break;
						case 2: 
							if ((shots[i].y)<MAP_SIZE_Y-1) 
							{
								//move up
								shots[i].moveTo(shots[i].x,shots[i].y+1);
							}else
							{
								 delShot(i);
							}
							break;
						case 3: 
							if ((shots[i].x)>0) 
							{
								//move up
								shots[i].moveTo(shots[i].x-1,shots[i].y);
							}else
							{
								 delShot(i);
							}
							break;
					}
					//System.out.println("moving shot :"+i+" dir"+shots[i].getDir()+"to "+shots[i].x+" "+shots[i].y);
				}
			}
		} catch(Exception e)	{
			System.out.println("ops: moveshots :"+e.getMessage());
		}
		
	}

	private void animTanks()
	{
		try{
			myPlayer.onTick();
		} catch(Exception e)	{
			System.out.println("ops: animTiles :"+e.getMessage());
		}
	}
	    
	private void animTiles()
	{
		try{
			switch (map.getAnimatedTile(-1))
			{
				case 27:                        
					map.setAnimatedTile(-1, 28);                        
					break;
				case 28:
					map.setAnimatedTile(-1, 36);
					break;
				case 36:
					map.setAnimatedTile(-1, 35);
					break;
				case 35:
					map.setAnimatedTile(-1, 27);
					break;
			}
			switch (map.getAnimatedTile(-2))
			{
				case 29:                        
					map.setAnimatedTile(-2, 30);                        
					break;
				case 30:
					map.setAnimatedTile(-2, 38);
					break;
				case 38:
					map.setAnimatedTile(-2, 37);
					break;
				case 37:
					map.setAnimatedTile(-2, 29);
					break;
			}
		}catch(Exception e)	{
			System.out.println("ops: animTiles :"+e.getMessage());
		}
	}
	
	public void handleInput()
	{
		boolean fire = false, up = false, down = false, left=false, right=false;
		if (currentKeyState != RELEASED)
		{
			switch(currentKey)
			{
			case Canvas.KEY_NUM2 :
			case Canvas.UP: 
				playerAction	=	0;
				up = true;
				break;

			case Canvas.KEY_NUM8 :
			case Canvas.DOWN:
				playerAction	=	2;
				down = true;
				break;

			case Canvas.KEY_NUM4 : 
			case Canvas.LEFT: 
				playerAction	=	3; 
				left = true; 
				break;

			case Canvas.KEY_NUM6 : 
			case Canvas.RIGHT: 
				playerAction	=	1; 
				right = true; 
				break;

			case Canvas.FIRE : 
			case Canvas.KEY_NUM5 : 
				playerAction	=	4; 
				break;
			}
		}
		
		// TODO: check for array index out of range
		//System.out.println("input: "+right+ " " +left + " " +down+ " " +up+" f:"+fire);
/*
		if (up && myPlayer.tY>0) 
		{
			if (mapGround[myPlayer.tX][myPlayer.tY-1] == 1) myPlayer.moveUp();
		}
		if (down && myPlayer.tY<MAP_SIZE_Y-1)
		{
			if (mapGround[myPlayer.tX][myPlayer.tY+1] == 1) myPlayer.moveDown();
		}
		if (left && myPlayer.tX>0)
		{
			if (mapGround[myPlayer.tX-1][myPlayer.tY] == 1) myPlayer.moveLeft();
		}
		if (right && myPlayer.tX<MAP_SIZE_X-1)
		{
			if (mapGround[myPlayer.tX+1][myPlayer.tY] == 1) myPlayer.moveRight();
		}
		if (fire)
		{
			if (myPlayer.fire())
			{
				addShot(myPlayer.tX,myPlayer.tY,myPlayer.getDir());
				System.out.println("fired");

			}
		}
		*/
		//if (down && heroY < GAME_SCREEN_HEIGHT - 13)	heroY++;
	};
	
	private void collideIt()
	{
		int i;
		try{
			for (i = 0; i < MAX_SHOTS; i++)
			{
				// check collision with wall
				if (mapGround[shots[i].x][shots[i].y]==0)
				{
					mapGround[shots[i].x][shots[i].y]=1;
					delShot(i);
					fillTiles(map);
				}
			}
		} catch(Exception e)	{
			System.out.println("ops: collideIt :"+e.getMessage());
		}
	}
	
	private void centerScreen()
	{
		int dx=0;
		int dy=0;
		// if player comes too near screen edges, shift screen
		if ((myPlayer.tX-viewX)>(getWidth()/TILE_SIZE_X-BORDER_X)) dx=BORDER_X;	
		if ((myPlayer.tX-viewX)<BORDER_X) dx=-BORDER_X;	
		if ((myPlayer.tY-viewY)>(getHeight()/TILE_SIZE_Y-BORDER_Y)) dy=BORDER_Y;	
		if ((myPlayer.tY-viewY)<BORDER_Y) dy=-BORDER_Y;	
		viewX+=dx;
		viewY+=dy;
		if (viewX<0) viewX=0;
		if (viewY<0) viewY=0;
		if (viewX>(MAP_SIZE_X-1)) viewX=(MAP_SIZE_X-1);
		if (viewY>(MAP_SIZE_Y-1)) viewY=(MAP_SIZE_Y-1);
		/*try{
			//layman.setViewWindow(viewX,viewY,100,79);
			System.out.println("centerScreen :"+viewX+" "+viewY);
		} catch(Exception e)	{
			System.out.println("ops: centerScreen :"+e.getMessage());
		}*/
				
	}
	
	// receive IrDA connection replies here
	public void receiveData(byte[] data)
	{
		int i;
		for (i=0;i<data.length;i++) inBuffer[i]=data[i];
		System.out.println("received :"+inBuffer.toString());
		
		
	}
	
/*	private void netGetInput()
	{
		try{
			//outBuffer[0]='G';
			conn.send(String.valueOf("G\n").getBytes());
			Thread.sleep(100);
			//parse response
			if (inBuffer[0]!='I')
			{
				System.out.println("ops: netGetInput : Error in response");
				return;
			}
			// get input 'I','F'/'M',dir,'\n'
			
		} catch(Exception e)	{
			System.out.println("ops: netConnect :"+e.getMessage());
		}
	}
	
	private void netPushGameState()
	{
		int i,j;
		try{
			outBuffer[0]='P';
			outBuffer[1]=MAP_SIZE_X;;
			outBuffer[2]=MAP_SIZE_Y;
			for (i = 0; i < MAP_SIZE_Y; i++){
				for (j = 0; j < MAP_SIZE_X; j++ )
				{
					outBuffer[3+i*MAP_SIZE_Y+j]=(byte)mapGround[j][i];
			    	}
			}			
			outBuffer[3+MAP_SIZE_X*MAP_SIZE_Y+1]=0x0d;
			conn.send(outBuffer);
		} catch(Exception e)	{
			System.out.println("ops: netPushGameState :"+e.getMessage());
		}
		
		
	}
	
	private void netConnect()
	{
		try{
			conn= new Connection("IRDA:");
			conn.setListener(this);
			conn.send(String.valueOf("AHOJ").getBytes());
		} catch(Exception e)	{
			System.out.println("ops: netConnect :"+e.getMessage());
		}
	}
	*/
	public void run()
	{
		System.out.println("Running " + running);

		try{
			while (running){
				handleInput();
				//netGetInput();
				// TODO: for each player
				//myPlayer.onTick();
				moveTanks();
				moveShots();
				//animTanks(); 
				collideIt();               
				animTiles();
				centerScreen();
				//netPushGameState();
				buffer.setColor(8912896);
				buffer.fillRect(0,0,101,80);
				layman.paint(buffer,-viewX * TILE_SIZE_X,-viewY * TILE_SIZE_Y);
				flushGraphics();
				Thread.sleep(100);
			}
		} catch(Exception e)	{
			System.out.println("ops: run :"+e.getMessage());
		}
	}
	    
	    
	public void commandAction(Command c, Displayable s) 
	{
		if (c == exitCommand) {
			midlet.quit();
		} else if (c == burnCommand){
			burnTrees();
		} else if (c == explodeCommand){
			myPlayer.onHit();
		} else if (c == connectCommand){
			//netConnect();
		}
	}            
}
