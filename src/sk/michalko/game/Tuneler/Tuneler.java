package sk.michalko.game.Tuneler;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import android.app.Activity;


public class Tuneler extends Activity {   
    
	private Command exitCommand;
	private Display display;    
	    
	public Tuneler() 
	{
	    display = Display.getDisplay(this);
	    System.out.println("TunelerMIDlet");

	}
	    
	public void startApp() 
	{
		try{
		//com.siemens.mp.io.File.debugWrite("debug.log", "\r\n***** startApp *****");
			TunelerCanvas canvas = new TunelerCanvas(this);
		       display.setCurrent(canvas);
			System.out.println("startApp");
		}
		catch (Exception e){
			System.out.println("Tuneler.startApp() Exception: startApp :  "+e.getMessage());
		}
	}
	    
	public void pauseApp() {}
	    
	public void destroyApp(boolean unconditional) {}
	     
	public void quit()
	{
		destroyApp(false);
		notifyDestroyed();
	}        
}
