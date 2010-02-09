package sk.michalko.game.bt;
// Copyright 2004 Nokia Corporation.
//
// THIS SOURCE CODE IS PROVIDED 'AS IS', WITH NO WARRANTIES WHATSOEVER,
// EXPRESS OR IMPLIED, INCLUDING ANY WARRANTY OF MERCHANTABILITY, FITNESS
// FOR ANY PARTICULAR PURPOSE, OR ARISING FROM A COURSE OF DEALING, USAGE
// OR TRADE PRACTICE, RELATING TO THE SOURCE CODE OR ANY WARRANTY OTHERWISE
// ARISING OUT OF ANY PROPOSAL, SPECIFICATION, OR SAMPLE AND WITH NO
// OBLIGATION OF NOKIA TO PROVIDE THE LICENSEE WITH ANY MAINTENANCE OR
// SUPPORT. FURTHERMORE, NOKIA MAKES NO WARRANTY THAT EXERCISE OF THE
// RIGHTS GRANTED HEREUNDER DOES NOT INFRINGE OR MAY NOT CAUSE INFRINGEMENT
// OF ANY PATENT OR OTHER INTELLECTUAL PROPERTY RIGHTS OWNED OR CONTROLLED
// BY THIRD PARTIES
//
// Furthermore, information provided in this source code is preliminary,
// and may be changed substantially prior to final release. Nokia Corporation
// retains the right to make changes to this source code at
// any time, without notice. This source code is provided for informational
// purposes only.
//
// Nokia and Nokia Connecting People are registered trademarks of Nokia
// Corporation.
// Java and all Java-based marks are trademarks or registered trademarks of
// Sun Microsystems, Inc.
// Other product and company names mentioned herein may be trademarks or
// trade names of their respective owners.
//
// A non-exclusive, non-transferable, worldwide, limited license is hereby
// granted to the Licensee to download, print, reproduce and modify the
// source code. The licensee has the right to market, sell, distribute and
// make available the source code in original or modified form only when
// incorporated into the programs developed by the Licensee. No other
// license, express or implied, by estoppel or otherwise, to any other
// intellectual property rights is granted herein.



//==============================================================================
// Package Statements



//==============================================================================
// Import Statements

import java.io.IOException;
import java.io.OutputStream;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;


//==============================================================================
// CLASS (OR INTERFACE) DECLARATIONS

/** The <code>DiscoverConnect</code> is an example that shows how to use device 
 *  and service discovery.
 *  After device with service has been found a connection is set up
 *  to that device.
 *
 *  Every keystroke is then transmitted to the other device and displayed there
 *  until connection is disconnected.
 *  <br>
 *  After starting the MIDlet you can choose your role: server or client.<br>
 *
 * The server waits that someone connects. After selecting "Server" you have to accept the user confirmation.
 * The device will then wait on a connection:<br>
 * <img src="doc-files/crop_server_select.jpg">
 *
 * <img src="doc-files/crop_server_selected_1_acceptance.jpg">
 *
 * <img src="doc-files/crop_server_selected_2_waiting.jpg"><br>
 *
 * When remote device has connected you see screen below and as soon as you press the number keys
 * (on local and remote device) they are shown on the screen (in this example user at remote device
 * pressed "1" and user on local device pressed "6"):<br>
 * <img src="doc-files/crop_server_conn_ptp_1.jpg">
 * <img src="doc-files/crop_server_conn_ptp_2.jpg"><br>
 *
 * <P>
 * When selecting to be a client you have 4 choices which are different in the way the selection of the
 * device(s) to connect to is done.<br>
 * <P>
 *
 * A) Connect to the device which is the <b>first found</b> device which matches the service:<br>
 * <img src="doc-files/crop_client_first_found_select.jpg"><br>
 * You will see an intermediate "searching" display until remote device is connected:<br>
 * <img src="doc-files/crop_client_conn_ptp_1.jpg">
 * <img src="doc-files/crop_client_conn_ptp_2.jpg"><br>
 * <P>
 *
 * B)  Connect to all devices which match the searched service:<br>
 * <img src="doc-files/crop_client_all_found_select.jpg"><br>
 * You will see an intermediate "searching" display until remote devices are connected:<br>
 * <img src="doc-files/crop_client_conn_ptp_1.jpg">
 * <img src="doc-files/crop_client_conn_ptp_2.jpg"><br>
 * <P>
 *
 * C) Display all found devices with a matching service and allow user to select <b>one</b> of these devices:<br>
 * <img src="doc-files/crop_client_sel_one_select.jpg"><br>
 * Select the device<br>
 * <img src="doc-files/crop_client_sel_one_selected.jpg"><br>
 * which then will be connected:<br>
 * <img src="doc-files/crop_client_conn_ptp_1.jpg">
 * <img src="doc-files/crop_client_conn_ptp_2.jpg"><br>
 * <P>
 *
 * D) Display all found devices with a matching service and allow user to select <b>several</b> of these devices:<br>
 * <img src="doc-files/crop_client_sel_several_select.jpg"><br>
 * The devices are displayed:<br>
 * <img src="doc-files/crop_client_sel_several_selected_1.jpg"><br>
 * Select the devices<br>
 * <img src="doc-files/crop_client_sel_several_selected_2.jpg"><br>
 * which then will be connected:<br>
 * <img src="doc-files/crop_client_conn_ptmp_1.jpg"><br>
 *
 */

public class DiscoverConnect
    extends MIDlet
    implements CommandListener
{
    //==============================================================================
    // Final variables (Class constants)

    //==============================================================================
    // Class (static) variables

    //==============================================================================
    // Instance variables
    private Display display;
    private BluetoothDiscovery disc;
    private NumberCanvas nc = null;

    //==============================================================================
    // Constructors and miscellaneous (finalize method, initialization block,...)

    /**
     * Creates a <code>DiscoverConnect</code> object.
     */
    public DiscoverConnect()
    {
        display = Display.getDisplay( this );
        ErrorScreen.init(null, display);

        // Create Discovery Object
        disc = new BluetoothDiscovery( display );
    }

    /** Called when MIDlet is started.
     */
    public void startApp()
    {
        String name;

        // Set UUID
        disc.setServiceUUID( "20000000000010008000006057028C19" );

        // Check if Bluetooth is turned on
        try
        {
            name = LocalDevice.getLocalDevice().getFriendlyName();
        }
        catch( BluetoothStateException e )
        {   // display user notification
            showAlertAndExit( "", "Please switch Bluetooth on!", AlertType.ERROR );
            return;
        }

        // Sets the name how this device is shown to the remote user
        disc.setName( name );

        // Start the UI
        startUI();
    }

    /** Offers the user a selection: Client or Server
     */
    private void startUI()
    {
        // Create new screen object for selecting the role: client or server
        ClientServerSelect select = new ClientServerSelect();

        display.setCurrent( select );
   }

    /**
     * Pause is a no-op since there are no background activities or
     * record stores that need to be closed.
     */
    public void pauseApp()
    {
    }

    /**
     * Destroy should cleanup everything.
     * In the case here there is nothing to cleanup.
     * @param unconditional not used
     */
    public void destroyApp( boolean unconditional )
    {
    }

    /**
     * Displays the string that is passed. Waits for user confirmation
     * and offers possibility to exist the Midlet. Used to show notifications to the user.
     * @param t title string that is displayed to the user.
     * @param s string that is displayed to the user.
     * @param type AlertType: ALARM, CONFIRMATION, ERROR, INFO, WARNING
     */
    private void showAlertAndExit( String t, String s, AlertType type )
    {
        Alert a = new Alert( t, s, null, type );
        a.addCommand( new Command( "Exit", Command.EXIT, 1 ) );
        a.setCommandListener( this );
        display.setCurrent( a );
    }


    /** Terminate Midlet.
     */
    public void Exit()
    {
        destroyApp( false );
        notifyDestroyed();
    }

    /** Respond to commands, including exit.
     * On the exit command, cleanup and notify that the MIDlet has been destroyed.
     * @param c The command.
     * @param s The displayable object. */
    public void commandAction(Command c, Displayable s)
    {
        switch( c.getCommandType() )
        {
            case Command.EXIT:
                // User chose to quit the application
                Exit();
                break;
        }
    }


    // Inner class
    /** UI to select client or server role.
     * User can choose between 4 different behaviours of the client, which mainly
     * with the way the found devices are presented to the user.
     * The 4 different possibilities directly map to search type parameter of
     * method {@link BluetoothDiscovery#searchService BluetoothDiscovery.searchService}: <br>
     * {@link BluetoothDiscovery#SEARCH_CONNECT_FIRST_FOUND SEARCH_CONNECT_FIRST_FOUND},
     * {@link BluetoothDiscovery#SEARCH_CONNECT_ALL_FOUND SEARCH_CONNECT_ALL_FOUND},
     * {@link BluetoothDiscovery#SEARCH_ALL_DEVICES_SELECT_ONE SEARCH_ALL_DEVICES_SELECT_ONE},
     * {@link BluetoothDiscovery#SEARCH_ALL_DEVICES_SELECT_SEVERAL SEARCH_ALL_DEVICES_SELECT_SEVERAL}.
     */
    public class ClientServerSelect
    extends List
    implements CommandListener
    {
        /**
         * Constructor
         */
        public ClientServerSelect()
        {
            super( "Select Role:", List.IMPLICIT );

            // Set text wrap around
            setFitPolicy( Choice.TEXT_WRAP_ON );
            // Create List
            append( "Server", null );
            append( "Client (FIRST_FOUND)", null );
            append( "Client (ALL_FOUND)", null );
            append( "Client (SELECT_ONE)", null );
            append( "Client (SELECT_SEVERAL)", null );
            // append commands
            addCommand( new Command( "Select", Command.OK, 1 ) );
            addCommand( new Command( "Quit", Command.EXIT, 1 ) );
            setCommandListener( this );
        }

        /** Starts server or client (or cancels/terminate the midlet).
         * @param c The command.
         * @param d The displayable object. */
        public void commandAction( Command c, Displayable d )
        {
            if( c.equals(List.SELECT_COMMAND) || (c.getCommandType() == Command.OK) )
            {
                int i = getSelectedIndex();
                String s = getString( i );
                if( s.equals( "Server" ) )
                {
                    // Start Server
                    ServerThread st = new ServerThread();
                    st.start();
                }
                if( s.equals( "Client (FIRST_FOUND)" ) )
                {   // Start Client with SEARCH_CONNECT_FIRST_FOUND
                    ClientThread ct = new ClientThread( disc.SEARCH_CONNECT_FIRST_FOUND );
                    ct.start();
                }
                if( s.equals( "Client (ALL_FOUND)" ) )
                {   // Start Client with SEARCH_CONNECT_ALL_FOUND
                    ClientThread ct = new ClientThread( disc.SEARCH_CONNECT_ALL_FOUND );
                    ct.start();
                }
                if( s.equals( "Client (SELECT_ONE)" ) )
                {   // Start Client with SEARCH_ALL_DEVICES_SELECT_ONE
                    ClientThread ct = new ClientThread( disc.SEARCH_ALL_DEVICES_SELECT_ONE );
                    ct.start();
                }
                if( s.equals( "Client (SELECT_SEVERAL)" ) )
                {   // Start Client with SEARCH_ALL_DEVICES_SELECT_SEVERAL
                    ClientThread ct = new ClientThread( disc.SEARCH_ALL_DEVICES_SELECT_SEVERAL );
                    ct.start();
                }
            }
            else if( c.getCommandType() == Command.EXIT )
            {
                // pass through to midlet
                Exit();
            }
        }
    }


    // Innerclass
    /** The ServerThread is used to wait until someone connects. <br
     * A thread is needed otherwise it would not be possible to display
     * anything to the user.
     */
    private class ServerThread
    extends Thread
    {
        /**
         * This method runs the server.
         */
        public void run()
        {
            try
            {
                // Wait on client
                BluetoothConnection[] con = disc.waitOnConnection();
                if( con[0] == null )
                {   // Connection cancelled
                    startUI();
                    return;
                }

                // Create Canvas to display keystrokes
                nc = new NumberCanvas( con );

                // Set as new display
                display.setCurrent( nc );
            }
            catch( Exception e )
            {    // display error message
                showAlertAndExit( "Error:", e.getMessage(), AlertType.ERROR );
                return;
            }
        }
    }

    // Innerclass
    /** The ClientThread is used to search for devices/Services and connect to them. <br>
     * A thread is needed otherwise it would not be possible to display
     * anything to the user.
     */
    private class ClientThread
    extends Thread
    {
        // Search type
        private int searchType;

        /** Constructor
         * @param st The search type. Possible values:
         * {@link BluetoothDiscovery.SEARCH_CONNECT_FIRST_FOUND SEARCH_CONNECT_FIRST_FOUND},
         * {@link BluetoothDiscovery.SEARCH_CONNECT_ALL_FOUND SEARCH_CONNECT_ALL_FOUND},
         * {@link BluetoothDiscovery.SEARCH_ALL_DEVICES_SELECT_ONE SEARCH_ALL_DEVICES_SELECT_ONE},
         * {@link BluetoothDiscovery.SEARCH_ALL_DEVICES_SELECT_SEVERAL SEARCH_ALL_DEVICES_SELECT_SEVERAL}.
         */
        protected ClientThread( int st )
        {
            // store search type
            searchType = st;
        }


        /**
         * This method runs the client.
         */
        public void run()
        {
            try
            {
                BluetoothConnection conn[] = disc.searchService( searchType );
                if( conn.length != 0 )
                {   // Create Canvas object which deals with receive and send
                    nc = new NumberCanvas( conn );
                    // Set as new display
                    display.setCurrent( nc );
                }
                else
                {   // nothing found
                    startUI();
                }
            }
            catch( Exception e )
            {    // display error message
                showAlertAndExit( "Error:", e.getMessage(), AlertType.ERROR );
                return;
            }
        }
    }


    /*
     * Displays own and received keystrokes. <br>
     * This class is called on client and server side after connection has
     * been established.
     * If user presses one of the number keys this is displayed to the user.
     * The keypress is sent as well as character to the remote device.
     * If a character is received from remote device it is also displayed
     * in the Canvas. <br>
     * So user can see what key he has pressed and as well which key the
     * remote user has pressed.
     */
    private class NumberCanvas
    extends Canvas
    implements CommandListener
    {
        private String local_char;   // char from local device (ie. keypress)
        private String[] remote_char;  // char that was received from remote device(s)
        private BluetoothConnection[] btConnections;

        /** Constructor
         * @param con A list with the connected Bluetooth devices. */
        private NumberCanvas( BluetoothConnection[] btConns )
        {
            // Store stream connection
            btConnections = btConns;
            // Initial displayed chars
            local_char = "Conn.";
            remote_char = new String[btConnections.length];
            // Start receive thread
            for( int i=0; i<btConnections.length; i++ )
            {   // loop through all connections
                remote_char[i] = "Conn.";
                ReceiveThread rt = new ReceiveThread( i );
                rt.start();
            }

            // Add cancel command
            addCommand( new Command( "Cancel", Command.CANCEL, 1 ) );
            setCommandListener( this );
        }

        /**
         * Only processes "Cancel": Disconnect and return to start screen.
         * @param c The command.
         * @param s The displayable object. */
        public void commandAction( Command c, Displayable s )
        {
            if( c.getCommandType() == Command.CANCEL )
            {
                // Close all connections
                close();
                // Start again
                startUI();
            }
        }

        /**
         * Closes all connections
         */
        private synchronized void close()
        {
            // Disconnect
            for( int i=0; i<btConnections.length; i++ )
            {   // loop through all connections
                btConnections[i].close();
            }
        }

        /**
         * Draws the locally and the remote received keystrokes.
         * @param g Graphics object for 2d drawings.
         */
        public void paint( Graphics g )
        {
            Font f;
            int y, h;

            // Clear screen
            g.setColor( 255, 255, 255 ); // White
            g.fillRect( 0, 0, getWidth(), getHeight() );

            // Draw local char
            g.setColor( 0, 0, 255 ); // Blue
            // Set bigger Font
            f = Font.getFont( Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_MEDIUM );
            g.setFont(f);

            y = 0;
            h = 16;
            g.drawString( "Local:", 0, y, Graphics.TOP | Graphics.LEFT );
            y += h;
            g.drawString( btConnections[0].getLocalName(), 0, y, Graphics.TOP | Graphics.LEFT );
            g.drawString( local_char, getWidth(),y, Graphics.TOP | Graphics.RIGHT);
            y += h;
            // Draw remote char(s)
            g.setColor( 255, 0, 0 ); // Red
            // remote names
            g.drawString( "Remote:", 0, y, Graphics.TOP | Graphics.LEFT );
            y += h;
            for( int i=0; i<btConnections.length; i++ )
            {
                g.drawString( btConnections[i].getRemoteName(), 0, y, Graphics.TOP | Graphics.LEFT );
                g.drawString( remote_char[i], getWidth(), y, Graphics.TOP | Graphics.RIGHT );
                y += 16;
            }
        }

        /**
         * Handles the (local) key presses. <br>
         * Keycode is updated on local display and sent to remote device(s).
         * @param keyCode Key that was pressed.
         */
        public void keyPressed( int keyCode )
        {
            char c;

            switch( keyCode )
            {
                case KEY_NUM0:
                    c = '0';
                    break;
                case KEY_NUM1:
                    c = '1';
                    break;
                case KEY_NUM2:
                    c = '2';
                    break;
                case KEY_NUM3:
                    c = '3';
                    break;
                case KEY_NUM4:
                    c = '4';
                    break;
                case KEY_NUM5:
                    c = '5';
                    break;
                case KEY_NUM6:
                    c = '6';
                    break;
                case KEY_NUM7:
                    c = '7';
                    break;
                case KEY_NUM8:
                    c = '8';
                    break;
                case KEY_NUM9:
                    c = '9';
                    break;
                case KEY_STAR:
                    c = '*';
                    break;
                case KEY_POUND:
                    c = '#';
                    break;
                default:
                    // Any other key does nothing
                    return;
            }

            // Update display
            updateLocalChar( (new Character(c)).toString() );
            // Send character to all connected devices
            for( int i=0; i<btConnections.length; i++ )
            {
                try
                {
                    OutputStream os = btConnections[i].getOutputStream();
                    os.write( (int) c );
                    os.flush();
                }
                catch( IOException e )
                {   // If error, then close
                    btConnections[i].close();
                    // Check if all connections are closed
                    if( checkIfAllClosed() )
                    {   // all closed -> return
                        return;
                    }
                }
            }
        }

        /**
         * Checks if all connections are closed.
         * If so, jumps to main menu.
         * @return true if all connection are closed, otherwise false.
         */
        private boolean checkIfAllClosed()
        {
            // Check if all connections are closed
            boolean allclosed = true;
            for( int l=0; l<btConnections.length; l++ )
            {
                if( btConnections[l].isClosed() != true  )
                {   // still open
                    allclosed = false;
                }
            }
            // If all connections closed then restart
            if( allclosed )
            {   // And restart
                startUI();
            }
            // return
            return allclosed;
        }

        /**
         * Updates the local key that is displayed to user.
         * It also generates a repaint to the Canvas.
         * @param c String that is displayed to the user.
         */
        private void updateLocalChar( String c )
        {
            // Store char
            local_char = c;
            // Repaint
            repaint();
        }

        /**
         * Updates one of the remote keys that are displayed to user.
         * It also generates a repaint to the Canvas.
         * @param index Index to the remote device for which the string
         * should be updated.
         * @param c String that is displayed to the user.
         */
        private void updateRemoteChar( int index, String c )
        {
            // Store char
            remote_char[index] = c;
            // Repaint
            repaint();
        }

        // Inner class
        /**
         * The ReceiveThread is used to receive the remote keypresses. <br>
         * For each remote device there exists an own RecieveThread.
         */
        private class ReceiveThread
        extends Thread
        {
            int index;

            /**
             * Constructor.
             * @param i Index, that corresponds to the number of the BluetoothConnection.
             */
            public ReceiveThread( int i )
            {
                // Store
                index = i;
            }

            /**
             * Reads from stream until end of stream reached (disconnect).<br>
             * The read character (which is the key the remote user pressed) is
             * displayed to the local user.
             */
            public void run()
            {
                // Read input stream (data from remote device)
                int inp;

                while( true )
                {
                    // Read (blocking)
                    try
                    {
                        inp = btConnections[index].getInputStream().read();
                    }
                    catch( IOException e )
                    {    // If error, then disconnect
                        btConnections[index].close();
                        // Check if all connections are closed
                        checkIfAllClosed();
                        // show that device is disconnected
                        updateRemoteChar( index, "Disc." );
                        return;
                    }

                    if( inp == -1 )
                    {   // Close
                        btConnections[index].close();
                        // Check if all connections are closed
                        checkIfAllClosed();
                        // show that device is disconnected
                        updateRemoteChar( index, "Disc." );
                        return;
                    }
                    updateRemoteChar( index,  (new Character((char)inp)).toString() );
                }
            }
        }
    }

}

