import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


public class DateParse {
	
	/**
	 * @author Chris Håland
	 * @param arg Format: "dd/MMM/yyyy:HH:mm:ss"
	 */
	public static int parse(String arg) throws ParseException {
		try
		{
			int date, month, year, hours, minutes, seconds, timezone;
			
			//09/Apr/2011:11:06:02
			
			
			String[] args1 = arg.split("/");
			// args1[0] = 09 = date
			// args1[1] = Apr = month
			// args1[2] = 2011:11:06:02 +0200 = args1[args1.length-1]
			String[] args2 = args1[args1.length-1].split(":");
			// args2[0] = 2011 = year
			// args2[1] = 11 = hours
			// args2[2] = 06 = minutes
			// args2[3] = 02 +0200
			String[] args3 = args2[3].split(" ");
			// args3[0] = 02 = seconds
			// args3[1] = +0200 = timezone
			
			date = Integer.parseInt(args1[0]);
			month = getMonth(args1[1]); 
			year = Integer.parseInt(args2[0]); 
			hours = Integer.parseInt(args2[1]); 
			minutes = Integer.parseInt(args2[2]); 
			seconds = Integer.parseInt(args3[0]);
			timezone = Integer.parseInt(args3[1].substring(1))/100;
			
			GregorianCalendar gc = new GregorianCalendar(year, month-1, date, hours, minutes, seconds);
			//gc.setTimeZone(TimeZone.getDefault());
			Date date4 = gc.getTime();
			//return (int)(gc.getTimeInMillis()/1000);
			return (int)(date4.getTime()/1000);
		} catch (IndexOutOfBoundsException err) {
			throw new ParseException(err.toString(), 0);
		}
		
	}
	
	private static int getMonth(String arg) {
		if (arg.equals("Jan")) {
			return 1;
		} else if (arg.equals("Feb")) {
			return 2;
		} else if (arg.equals("Mar")) {
			return 3;
		} else if (arg.equals("Apr")) {
			return 4;
		} else if (arg.equals("May")) {
			return 5;
		} else if (arg.equals("Jun")) {
			return 6;
		} else if (arg.equals("Jul")) {
			return 7;
		} else if (arg.equals("Aug")) {
			return 8;
		} else if (arg.equals("Sep")) {
			return 9;
		} else if (arg.equals("Oct")) {
			return 10;
		} else if (arg.equals("Nov")) {
			return 11;
		} else if (arg.equals("Dec")) {
			return 12;
		} else {
			throw new IndexOutOfBoundsException("Wrong parsing month: " + arg);
		}
	}
}
