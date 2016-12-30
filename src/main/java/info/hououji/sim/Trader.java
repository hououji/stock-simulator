package info.hououji.sim;
import java.io.File;
import java.util.Date;


public abstract class Trader {

	public abstract Result trade(CSV csv, int backDays) ;
	
	class Result {
		Date finalDate ;
		int startBackDays;
		int finalBackDays;
		double inValue ;
		double outValue;
	}
}
