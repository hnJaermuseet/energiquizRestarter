import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;


public class AccesslogLine
{
	public String ip, date, get, responsecode;
	public int dateseconds;
	public AccesslogLine (String line)
	{
		ip = "";
		date = "";
		get = "";
		responsecode = "";
		dateseconds = -1;
		try {
			// 192.168.115.73 - - [18/Apr/2011:07:59:55 +0200] "GET /Player_Attract.swf HTTP/1.1" 304 -
			String[] args = line.split(" ", 6);
			
			
			// args[0] => 192.168.115.73
			ip = args[0];
			
			
			// args[1] => -
			// args[2] => -
			
			
			// args[3] => [18/Apr/2011:07:59:55
			// args[4] => +0200]
			date = args[3].substring(1) +" "+args[4].substring(0, args[4].length()-1);
			// 18/Apr/2011:07:59:55 +0200
			//DateFormat df = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss");
			try {
				dateseconds = DateParse.parse(date);
			} catch (ParseException err) {
				energiquizRestarter.log.log(Level.SEVERE, "Uncaught exception", err);
				err.printStackTrace();
				System.exit(0);
			}
			
			// args[5] => "GET /Player_Attract.swf HTTP/1.1" 304 -
			String[] args2 = args[5].substring(1).split(" HTTP/1.1\" ");
			// args2[0] = GET /Player_Attract.swf
			get = args2[0];
			
			if(args2.length >= 2)
			{
				// args2[1] = 304 -
				String[] args3 = args2[1].split(" ");
				// args3[0] = 304
				responsecode = args3[0];
				// args3[1] = -
			}
		}
		catch (ArrayIndexOutOfBoundsException err)
		{
			energiquizRestarter.log.log(Level.SEVERE, "Uncaught exception", err);
			err.printStackTrace();
		}
	}
}