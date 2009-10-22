package net.abhinavsarkar.jywrapper.example;

import java.util.List;

import net.abhinavsarkar.jywrapper.JyWrapper;
import net.abhinavsarkar.jywrapper.PyAttributeType;
import net.abhinavsarkar.jywrapper.PyMethodType;
import net.abhinavsarkar.jywrapper.annotation.PyAttribute;
import net.abhinavsarkar.jywrapper.annotation.PyMethod;
import net.abhinavsarkar.jywrapper.annotation.Wraps;

@Wraps("datetime")
public interface datetime {
	
	public static final datetime datetime_ = JyWrapper.wrap(datetime.class);

	@PyAttribute(type = PyAttributeType.CONST, attribute = "MINYEAR")
	public abstract int MINYEAR();

	@PyAttribute(type = PyAttributeType.CONST, attribute = "MAXYEAR")
	public abstract int MAXYEAR();

	@Wraps("datetime.datetime")
	public interface Date extends Comparable<Date> {
		
		public static final Date Date_ = JyWrapper.wrap(Date.class);
		
		//init
		@PyMethod(type = PyMethodType.INIT)
		public abstract Date initialize(int year, int month, int day);
		
		// static methods
		@PyMethod(type = PyMethodType.DIRECT)
		public abstract Date today();

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract Date fromtimestamp(long timestamp);

		@PyMethod(type = PyMethodType.DIRECT, method = "fromordinal")
		public abstract Date fromOrdinal(long ordinal);

		@PyAttribute(type = PyAttributeType.CONST)
		public abstract Date max();

		@PyAttribute(type = PyAttributeType.CONST, attribute = "min")
		public abstract Date min();

		@PyAttribute(type = PyAttributeType.CONST, attribute = "resolution")
		public abstract Date resolution();

		// static methods

		// instance attribute getters
		@PyAttribute(type = PyAttributeType.GETTER, attribute = "year")
		public abstract int getYear();

		@PyAttribute(type = PyAttributeType.GETTER, attribute = "month")
		public abstract int getMonth();

		@PyAttribute(type = PyAttributeType.GETTER, attribute = "day")
		public abstract int getDay();

		// instance attribute getters

		// numeric operations
		@PyMethod(type = PyMethodType.NUMERIC)
		public abstract Date add(TimeDelta td);

		@PyMethod(type = PyMethodType.NUMERIC)
		public abstract Date subtract(TimeDelta td);

		@PyMethod(type = PyMethodType.NUMERIC)
		public abstract TimeDelta subtract(Date td);

		// numeric operations

		// instance methods
		@PyMethod(type = PyMethodType.DIRECT)
		public abstract Date replace(int year, int month, int day);

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract List<Number> timetuple();

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract long toordinal();

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract int weekday();

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract int isoweekday();

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract List<Number> isocalendar();

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract String isoformat();
		
		@PyMethod(type = PyMethodType.DIRECT)
		public abstract String ctime();

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract String strftime(String format);
		// instance methods
	}

	@Wraps("datetime.time")
	public interface Time extends Comparable<Time> {
		
		public static Time Time_ = JyWrapper.wrap(Time.class);
		
		//init
		@PyMethod(type = PyMethodType.INIT)
		public abstract Time initialize(int hour, int min, int second);
		
		@PyMethod(type = PyMethodType.INIT)
		public abstract Time initialize(int hour, int min, int second, 
				long microsecond);
		
		@PyMethod(type = PyMethodType.INIT)
		public abstract Date initialize(int hour, int min, int second, 
				long microsecond, TZInfo tzinfo);
		
		// static methods
		@PyAttribute(type = PyAttributeType.CONST, attribute = "max")
		public abstract Time max();

		@PyAttribute(type = PyAttributeType.CONST, attribute = "min")
		public abstract Time min();

		@PyAttribute(type = PyAttributeType.CONST, attribute = "resolution")
		public abstract Time resolution();

		// static methods

		// instance attribute getters
		@PyAttribute(type = PyAttributeType.GETTER, attribute = "hour")
		public abstract int getHour();

		@PyAttribute(type = PyAttributeType.GETTER, attribute = "minute")
		public abstract int getMinute();

		@PyAttribute(type = PyAttributeType.GETTER, attribute = "second")
		public abstract int getSecond();

		@PyAttribute(type = PyAttributeType.GETTER, attribute = "microsecond")
		public abstract long getMicrosecond();

		@PyAttribute(type = PyAttributeType.GETTER, attribute = "tzinfo")
		public abstract TZInfo getTzinfo();

		// instance attribute getters

		// instance methods
		@PyMethod(type = PyMethodType.DIRECT)
		public abstract Time replace(int hour, int minute, int second,
				long microsecond, TZInfo tzinfo);

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract String isoformat();

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract String strftime(String format);

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract TimeDelta utcoffset();

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract TimeDelta dst();

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract String tzname();
		// instance methods
	}

	@Wraps("datetime.datetime")
	public interface DateTime extends Date {
		
		public static final DateTime DateTime_ = JyWrapper.wrap(DateTime.class);
		
		//init
		@PyMethod(type = PyMethodType.INIT)
		public abstract DateTime initialize(int year, int month, int day,
				int hour, int min, int second, long microsecond);
		
		@PyMethod(type = PyMethodType.INIT)
		public abstract DateTime initialize(int year, int month, int day,
				int hour, int min, int second, long microsecond, TZInfo tzinfo);
		
		// static methods
		@PyMethod(type = PyMethodType.DIRECT)
		public abstract DateTime today();

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract DateTime now();

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract DateTime now(TZInfo tz);

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract DateTime utcnow();

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract DateTime fromtimestamp(long timestamp);

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract DateTime utcfromtimestamp(long timestamp);

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract DateTime fromordinal(long ordinal);

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract DateTime combine(Date date, Time t);

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract DateTime strptime(String dateString, String format);

		@PyAttribute(type = PyAttributeType.CONST, attribute = "max")
		public abstract DateTime max();

		@PyAttribute(type = PyAttributeType.CONST, attribute = "min")
		public abstract DateTime min();

		@PyAttribute(type = PyAttributeType.CONST, attribute = "resolution")
		public abstract DateTime resolution();

		// static methods

		// instance attribute getters
		@PyAttribute(type = PyAttributeType.GETTER, attribute = "hour")
		public abstract int getHour();

		@PyAttribute(type = PyAttributeType.GETTER, attribute = "minute")
		public abstract int getMinute();

		@PyAttribute(type = PyAttributeType.GETTER, attribute = "second")
		public abstract int getSecond();

		@PyAttribute(type = PyAttributeType.GETTER, attribute = "microsecond")
		public abstract long getMicrosecond();

		@PyAttribute(type = PyAttributeType.GETTER, attribute = "tzinfo")
		public abstract TZInfo getTzinfo();

		// instance attribute getters

		// numeric operations
		@PyMethod(type = PyMethodType.NUMERIC)
		public abstract DateTime add(TimeDelta td);

		@PyMethod(type = PyMethodType.NUMERIC)
		public abstract DateTime subtract(TimeDelta td);

		@PyMethod(type = PyMethodType.NUMERIC)
		public abstract TimeDelta subtract(Date td);

		// numeric operations

		// instance methods
		@PyMethod(type = PyMethodType.DIRECT)
		public abstract Date date();

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract Time time();

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract Time timetz();

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract DateTime replace(int year, int month, int day);
		
		@PyMethod(type = PyMethodType.DIRECT)
		public abstract DateTime replace(int year, int month, int day, int hour,
				int minute, int second);
		
		@PyMethod(type = PyMethodType.DIRECT)
		public abstract DateTime replace(int year, int month, int day, int hour,
				int minute, int second, long microsecond);
		
		@PyMethod(type = PyMethodType.DIRECT)
		public abstract DateTime replace(int year, int month, int day, int hour,
				int minute, int second, long microsecond, TZInfo tzinfo);

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract DateTime astimezone(TZInfo tzinfo);

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract TimeDelta utcoffset();

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract TimeDelta dst();

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract String tzname();

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract List<Number> utctimetuple();

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract String isoformat(String sep);
		// instance methods
	}

	@Wraps("datetime.timedelta")
	public interface TimeDelta {
		
		public static final TimeDelta TimeDelta_ = JyWrapper.wrap(TimeDelta.class);
		
		//init
		@PyMethod(type = PyMethodType.INIT)
		public abstract TimeDelta initialize(long days, long seconds, 
				long microseconds);
		
		@PyMethod(type = PyMethodType.INIT)
		public abstract TimeDelta initialize(long days, long seconds, 
				long microseconds, long milliseconds, long minutes, 
				long hours, long weeks);
		
		// static methods
		@PyAttribute(type = PyAttributeType.CONST, attribute = "min")
		public abstract TimeDelta min();

		@PyAttribute(type = PyAttributeType.CONST, attribute = "max")
		public abstract TimeDelta max();

		@PyAttribute(type = PyAttributeType.CONST, attribute = "resolution")
		public abstract TimeDelta resolution();

		// static methods

		// instance attribute getters
		@PyAttribute(type = PyAttributeType.GETTER, attribute = "days")
		public abstract long getDays();

		@PyAttribute(type = PyAttributeType.GETTER, attribute = "seconds")
		public abstract long getSeconds();

		@PyAttribute(type = PyAttributeType.GETTER, attribute = "microseconds")
		public abstract long getMicroseconds();

		// instance attribute getters

		// numeric operations
		@PyMethod(type = PyMethodType.NUMERIC)
		public abstract TimeDelta add(TimeDelta td);

		@PyMethod(type = PyMethodType.NUMERIC)
		public abstract TimeDelta subtract(TimeDelta td);

		@PyMethod(type = PyMethodType.NUMERIC)
		public abstract TimeDelta multiply(long n);

		@PyMethod(type = PyMethodType.NUMERIC)
		public abstract TimeDelta divide(long n);

		@PyMethod(type = PyMethodType.NUMERIC)
		public abstract TimeDelta plus();

		@PyMethod(type = PyMethodType.NUMERIC)
		public abstract TimeDelta negate();

		@PyMethod(type = PyMethodType.NUMERIC)
		public abstract TimeDelta abs();
		// numeric operations
	}

	@Wraps("datetime.tzinfo")
	public interface TZInfo {
		
		public static final TZInfo TZInfo_ = JyWrapper.wrap(TZInfo.class);		
		
		// instance methods
		@PyMethod(type = PyMethodType.DIRECT)
		public abstract TimeDelta utcoffset(DateTime dt);

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract TimeDelta dst(DateTime dt);

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract String tzname(DateTime dt);

		@PyMethod(type = PyMethodType.DIRECT)
		public abstract String fromutc(DateTime dt);
		// instance methods
	}

}