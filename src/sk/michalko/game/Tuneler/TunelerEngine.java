package sk.michalko.game.Tuneler;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.TiledLayer;

public class TunelerEngine {

	// Map Definitions
	private TiledLayer map;
	private LayerManager layman;
	private int viewX=0;
	private int viewY=0;
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

	// Game Objects
	private final int MAX_SHOTS =15;

	private TunelerPlayer players[];
	private TunelerShot[] shots;
	private int countShots=0;
	private boolean[] shotsActive;

	private static int[][] mapGround;/* = {{4, 6, 6,  6,  6, 6, 6, 6},    
	{10, 13, 19, 13, 11,  6, -1,  6},
	{6, 6, 9, 6, 9, 6,  6, 6},
	{ 3, 13, 17,  13, 14, 6, -2, 6},
	{ 6, 6,  2,  6,  6, 6, 6, 6}   };*/

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
		
		// Create drop point
		// TODO: create different drop points for players
		mapGround[0][0]=1;
		mapGround[1][0]=1;

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

	private TunelerPlayer createPlayer(String name,int x,int y)
	{
		try{
			TunelerPlayer player = new TunelerPlayer(x,y,2);

			return player;
		}
		catch (Exception e){
			System.out.println("ops: createPlayer :  "+e.getMessage());
		}
		
		return null;	
	}

	public void movePlayers(){
		
	}
	
	
	
	/**
	 * Create a new shot on x,y with direction dir
	 * @param x
	 * @param y
	 * @param dir
	 */
	public void addShot(int x,int y,int dir)
	{
		int i=0;
		while(shotsActive[i]) i++;
		shotsActive[i]=true;
		shots[i].activate(true);
		shots[i].setDir(dir);
		shots[i].moveTo(x,y);
	}

	/**
	 * Deactivate shot (after collision with wall or player)
	 * @param i	- Shot id
	 */
	public void delShot(int i)
	{
		shotsActive[i]=false;
		shots[i].activate(false);
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
					// speed is 1 tile in axis
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
					//fillTiles(map);
				}
			}
		} catch(Exception e)	{
			System.out.println("ops: collideIt :"+e.getMessage());
		}
	}

	public void init() {

		createGround();
		//createPlayer();
		createShots();

		// create players
		players[0] = createPlayer("me",0,1);
		
	}
	
	public void doTick(){

		try {
		movePlayers();
		for (int i = 0; i<players.length; i++) {
			players[i].onTick();
		}
		moveShots();
		//animTanks(); 
		collideIt();               
		//animTiles();
		//centerScreen();
		//netPushGameState();
		//buffer.setColor(8912896);
		//buffer.fillRect(0,0,101,80);
		//layman.paint(buffer,-viewX * TILE_SIZE_X,-viewY * TILE_SIZE_Y);
		//flushGraphics();
		Thread.sleep(100);
		} catch (Exception e) {
			System.out.println("ops: TunelerEngine.run :"+e.getMessage());
		}
		
	}
	
}
