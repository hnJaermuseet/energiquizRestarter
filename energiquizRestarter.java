import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;



public class energiquizRestarter
{
	public static Logger log;
	
	static String killtask; // = "taskkill.exe /IM taskmgr.exe";
	static String startagain; // = "C:\\WINDOWS\\system32\\taskmgr.exe";
	
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
		
		if(args.length < 3)
		{
			log.log(Level.SEVERE,
					"Must give 3 arguments to use this program.\n" +
					"1: Seconds we should wait.\n" +
					"2: What we should execute to kill the task, taskkill.exe /IM iexplorer.exe \n"+
					"3: What we should execute to start again, iexplorer.exe"
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
		log.log(Level.INFO, "Argument - kill: "+killtask);
		log.log(Level.INFO, "Argument - startagain: " + startagain);
		
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
		
		boolean missingPlayer;
		
		
		
		missingPlayer = true; // TODO: remove
		
		if(missingPlayer)
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
