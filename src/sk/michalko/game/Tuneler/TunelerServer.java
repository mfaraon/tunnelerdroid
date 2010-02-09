/**
 * 
 */
package sk.michalko.game.Tuneler;

/**
 * @author mmm
 * This class should run the whole game engine 
 * (map updates, shot movement and player movement)
 * and manage multiplayer network communication
 */
public class TunelerServer implements Runnable {

	private boolean running;
	private TunelerEngine engine;
	
	private void handleInput(){
		
		
	};
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Server Running " + running);

		// Create game engine
		engine = new TunelerEngine();
		
		try{
			while (running){
				handleInput();
				//netGetInput();
				engine.doTick();
				/*	
				 	myPlayer.onTick();
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
				*/
			}
		} catch(Exception e)	{
			System.out.println("ops: run :"+e.getMessage());
		}

	}

}
