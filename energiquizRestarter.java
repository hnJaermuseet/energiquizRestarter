import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 * Analyze apache log and restart iexplorer if one of the clients are just waiting to connect
 * 
 * Test with these arguments:
 * 15 "taskkill.exe /IM taskmgr.exe" "C:\WINDOWS\system32\taskmgr.exe" "access.log"
 * 
 * 
 * @author Hallvard Nygard <hn@jaermuseet.no>
 * @author Chris Håland
 * @see https://github.com/hnJaermuseet/energiquizRestarter
 *
 */
public class energiquizRestarter
{
	public static Logger log;
	
	static String killtask; // = "taskkill.exe /IM taskmgr.exe";
	static String startagain; // = "C:\\WINDOWS\\system32\\taskmgr.exe";
	static String accesslog;
	
	/**
	 * Restarts iexplorer if the clients has not checked in correctly
	 * 
	 * @author Hallvard Nygard, hn@jaermuseet.no
	 * @see http://github.com/hnJaermuseet/energiquizRestarter
	 * @param args
	 */
	public static void main(String[] args)
	{
		
		FileHandler fh;
		try {
			fh = new FileHandler("energiquizRestart.log", true);
			fh.setFormatter(new SimpleFormatter());
			log = Logger.getLogger("");
			log.addHandler(fh);

		} catch (SecurityException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		if(args.length < 4)
		{
			log.log(Level.SEVERE,
					"Must give 3 arguments to use this program.\n" +
					"1: Seconds we should wait.\n" +
					"2: What we should execute to kill the task, taskkill.exe /IM iexplorer.exe \n"+
					"3: What we should execute to start again, iexplorer.exe\n" +
					"4: Location of apaches access.log, c:\\wamp\\logs\\access.log"
					);
			System.exit(0);
			}

		int seconds = Integer.parseInt(args[0]);
		if(seconds < 0)
		{
			log.log(Level.SEVERE, "Seconds must be positive or 0.");
			System.exit(0);
		}
		killtask = args[1];
		startagain = args[2];
		accesslog = args[3];
		log.log(Level.INFO, "Argument - kill: "+killtask);
		log.log(Level.INFO, "Argument - startagain: " + startagain);
		log.log(Level.INFO, "Argument - access.log: " + accesslog);
		
		while(true)
		{
			try
			{
				// Executing
				checkApacheLog();

				// Waiting
				log.log(Level.INFO, "Waiting " + seconds +" seconds...");
				Thread.sleep(seconds * 1000);
			}
			catch (Exception err) {
				log.log(Level.SEVERE, "Uncaught exception", err);
				err.printStackTrace();
			 }
		}
	}
	
	public static void checkApacheLog() throws Exception
	{
		log.log(Level.INFO, "Checking Apache log");
		
		/* 
			A successful startup of player 1 looks like this in the log
			192.168.115.73 - - [18/Apr/2011:07:59:55 +0200] "GET /Player1.html HTTP/1.1" 304 -
			192.168.115.73 - - [18/Apr/2011:07:59:55 +0200] "GET /AC_RunActiveContent.js HTTP/1.1" 304 -
			192.168.115.73 - - [18/Apr/2011:07:59:55 +0200] "GET /AC_Flash.js HTTP/1.1" 304 -
			192.168.115.73 - - [18/Apr/2011:07:59:55 +0200] "GET /Player.swf?startup=1 HTTP/1.1" 304 -
			192.168.115.73 - - [18/Apr/2011:07:59:55 +0200] "GET /favicon.ico HTTP/1.1" 404 209
			192.168.115.73 - - [18/Apr/2011:07:59:55 +0200] "GET /XML/config.xml HTTP/1.1" 304 -
			192.168.115.73 - - [18/Apr/2011:07:59:55 +0200] "GET /XML/messages.xml HTTP/1.1" 304 -
			192.168.115.73 - - [18/Apr/2011:07:59:55 +0200] "GET /Player_Attract.swf HTTP/1.1" 304 -
			192.168.115.73 - - [18/Apr/2011:07:59:56 +0200] "GET /MFSocket.swf HTTP/1.1" 304 -
			192.168.115.73 - - [18/Apr/2011:07:59:59 +0200] "GET /crossdomain.xml HTTP/1.1" 404 213
			(...) Up to a 3 minutes (...)
			192.168.115.73 - - [18/Apr/2011:08:00:00 +0200] "GET /Player1.flv HTTP/1.1" 304 -
			
			If the last line ("GET /Player1.flv") is missing, the player is "Loading"/"Waiting".
			A restart of the master might fix this
		 */
		
		// Read access.log file
		ArrayList<AccesslogLine> lines = new ArrayList<AccesslogLine>();
		AccesslogLine currentline;
		String line;
		BufferedReader in = new BufferedReader(new FileReader(accesslog));
		while((line = in.readLine()) != null)
		{
			currentline = new AccesslogLine(line);
			if(
					currentline.dateseconds+(60*60) // One hour
						> 
					(System.currentTimeMillis()/1000)
			)
			{
				lines.add(currentline);
			}
		}
		
		// Looping the last hours backwards
		boolean player1_found = false;
		boolean player2_found = false;
		boolean missing_player = false;
		for (int i = lines.size()-1; i >= 0 && !missing_player; i--)
		{
			currentline = lines.get(i);
			if(currentline.get.equals("GET /Player1.flv"))
			{
				player1_found = true;
			}
			else if(currentline.get.equals("GET /Player2.flv"))
			{
				player2_found = true;
			}
			else if(!player1_found && currentline.get.equals("GET /Player1.html"))
			{
				missing_player = true;
			}
			else if(!player2_found && currentline.get.equals("GET /Player2.html"))
			{
				missing_player = true;
			}
		}
		
		if(missing_player)
		{
			// Aaaaaaah! Die!!!
			restartMaster();
		}
	}
	
	public static void restartMaster() throws InterruptedException, IOException
	{
		log.log(Level.SEVERE, "RESTARTING MASTERS INTERNET EXPLORER");
		
		// Kill
		log.log(Level.INFO, "Killing process: "+killtask);
		Process p = Runtime.getRuntime().exec(killtask);
		p.waitFor();
		log.log(Level.INFO, "Exit value after kill: "+ p.exitValue());
		
		// Start again
		log.log(Level.INFO, "Starting again: " + startagain);
		Runtime.getRuntime().exec(startagain);
	}
}

