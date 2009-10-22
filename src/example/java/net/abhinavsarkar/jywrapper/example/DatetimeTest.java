package net.abhinavsarkar.jywrapper.example;

import static net.abhinavsarkar.jywrapper.example.datetime.Date.Date_;
import static net.abhinavsarkar.jywrapper.example.datetime.DateTime.DateTime_;
import static net.abhinavsarkar.jywrapper.example.datetime.Time.Time_;
import static net.abhinavsarkar.jywrapper.example.datetime.TimeDelta.TimeDelta_;

import java.util.List;

import net.abhinavsarkar.jywrapper.example.datetime.Date;
import net.abhinavsarkar.jywrapper.example.datetime.DateTime;
import net.abhinavsarkar.jywrapper.example.datetime.Time;
import net.abhinavsarkar.jywrapper.example.datetime.TimeDelta;

public class DatetimeTest {
	
	private static void testDate() {
		Date today = Date_.today();
		int day = today.getDay();
		int month = today.getMonth();
		int year = today.getYear();
		System.out.println(today);
		Date myBirthday = Date_.initialize(today.getYear(), 12, 14);
		if (myBirthday.compareTo(today) < 0) {
			myBirthday = myBirthday.replace(today.getYear() + 1, 0, 0);
		}
		System.out.println(myBirthday);
		TimeDelta timeToBirthday = myBirthday.subtract(today).abs();
		System.out.println(timeToBirthday.getDays());
		
		Date d = Date_.fromOrdinal(730920); //730920th day after 1. 1. 0001
		System.out.println(d);
		//datetime.date(2002, 3, 11)
		List<Number> t = d.timetuple();
		for (Number i : t) {
			System.out.println(i);
		}	
//		2002                # year
//		3                   # month
//		11                  # day
//		0
//		0
//		0
//		0                   # weekday (0 = Monday)
//		70                  # 70th day in the year
//		-1
		List<Number> ic = d.isocalendar();
		for (Number i : ic) {
			System.out.println(i);
		}
//		2002                # ISO year
//		11                  # ISO week number
//		1                   # ISO day number ( 1 = Monday )
		System.out.println(d.isoformat());
		//'2002-03-11'
		System.out.println(d.strftime("%d/%m/%y"));
		//'11/03/02'
		System.out.println(d.strftime("%A %d. %B %Y"));
		//'Monday 11. March 2002'
	}

	private static void testTimeDelta() {
		TimeDelta year = TimeDelta_.initialize(365, 0, 0);
		TimeDelta anotherYear = TimeDelta_.initialize(84, 600, 0, 0, 50, 23, 40);//adds up to 365 days
		System.out.println(year.equals(anotherYear));
		//True
		TimeDelta tenYears = year.multiply(10);
		System.out.println(tenYears + " | " +  Math.floor(tenYears.getDays() / 365));
		//(datetime.timedelta(3650), 10)
		TimeDelta nineYears = tenYears.subtract(year);
		System.out.println(nineYears + " | " +  Math.floor(nineYears.getDays() / 365));
		//(datetime.timedelta(3285), 9)
		TimeDelta threeYears = nineYears.divide(3);
		System.out.println(threeYears + " | " + Math.floor(threeYears.getDays() / 365));
		//(datetime.timedelta(1095), 3)
		System.out.println((threeYears.subtract(tenYears)).abs()
				.equals(threeYears.multiply(2).add(year)));
	}

	private static void testDateTime() {
		//Using datetime.combine()
		Date d = Date_.initialize(2005, 7, 14);
		Time t = Time_.initialize(12, 30, 0);
		System.out.println(DateTime_.combine(d, t));
		//datetime.datetime(2005, 7, 14, 12, 30)

		//Using datetime.now() or datetime.utcnow()
		System.out.println(DateTime_.now());   
		//datetime.datetime(2007, 12, 6, 16, 29, 43, 79043)   # GMT +1
		System.out.println(DateTime_.utcnow());   
		//datetime.datetime(2007, 12, 6, 15, 29, 43, 79060)
		
		//Using datetime.strptime()
		DateTime dt = DateTime_.strptime("21/11/06 16:30", "%d/%m/%y %H:%M");
		System.out.println(dt);
		//datetime.datetime(2006, 11, 21, 16, 30)
		
		// Using datetime.timetuple() to get tuple of all attributes
		List<Number> tt = dt.timetuple();
		for (Number it : tt) {   
			System.out.println(it);
		}
//		2006    # year
//		11      # month
//		21      # day
//		16      # hour
//		30      # minute
//		0       # second
//		1       # weekday (0 = Monday)
//		325     # number of days since 1st January
//		-1      # dst - method tzinfo.dst() returned None
		
		// Date in ISO format
		List<Number> ic = dt.isocalendar();
		for (Number it : ic) {
			System.out.println(it);
		}
//		2006    # ISO year
//		47      # ISO week
//		2       # ISO weekday
		
		// Formatting datetime
		System.out.println(dt.strftime("%A, %d. %B %Y %I:%M%p"));
		//'Tuesday, 21. November 2006 04:30PM'		
	}

	public static void main(String[] args) {
		testDate();
		testTimeDelta();	
		testDateTime();
	}

}
