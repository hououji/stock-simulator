package info.hououji.sim;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class BreakThroughDetector extends Detector{

	static boolean debug = false;

	private int periodForVolume = 20;
	private int periodForBreakThrough = 20 ;
	private int periodForWaiting = 60 ;
	private int changeForWaiting = 15 ; // percent
	private double changeForVolume = 3 ;
	private String btStart, btEnd;
	
	Map<String, Double> marketCap = new HashMap<String,Double>() ; 
	
	
	@Override
	public boolean detect(CSV csv, int backDays) {
		try{
			csv.setBaseDay(backDays);
			
			// ignore if too small
//			if(csv.get(0, CSV.VOL_PRICE) < 100000000) return false;
//			if(marketCap.get(csv.getCode()) == null || marketCap.get(csv.getCode()) < 200) return false;
			if(this.isMarketCapGreat(csv.getCode(), 10) == false) return false ;
			if(csv.getLen() < backDays + 250) return false;
			if(csv.max(0, 10, CSV.VOL) < 0.1) return false;
			
			double maxWaiting = csv.max(periodForBreakThrough + periodForWaiting, periodForWaiting, CSV.ADJ_CLOSE) ;
			double minWaiting = csv.min(periodForBreakThrough + periodForWaiting, periodForWaiting, CSV.ADJ_CLOSE) ;
			double change = (maxWaiting - minWaiting) / minWaiting * 100;
			if(change > changeForWaiting) return false ;
			
			double maxBT = csv.max(periodForBreakThrough, periodForBreakThrough, CSV.ADJ_CLOSE) ;
			if(maxWaiting > maxBT) return false;
			
			double volWaiting = csv.avg(periodForBreakThrough + periodForWaiting, periodForWaiting, CSV.VOL) ;
			double volBT = csv.avg(periodForBreakThrough, periodForBreakThrough, CSV.VOL) ;
			if( ! (volBT > changeForVolume * volWaiting) ) return false;
			// pass
			Log.log(csv.getName() + ", adf cls:" + Misc.trim(csv.get(0, CSV.ADJ_CLOSE),3) +", ex:" + Misc.trim(csv.get(0, CSV.VOL_PRICE),3));
			
			if(btStart == null) {
				btStart = csv.getDate(periodForBreakThrough) ;
				btEnd = csv.getDate(0) ;
			}
			
			
			return true;
		}catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public String getName() {
		return "break-through-detector";
	}

	@Override
	public String getDesc() {
		return "waiting(period:"+periodForWaiting+" days, change "+changeForWaiting+"%),break through("+btStart+" ~ "+btEnd+", vol. change:"+changeForVolume+")";
	}
	
	public static void main(String args[]) throws Exception {
		usage() ;
		BreakThroughDetector d = new BreakThroughDetector() ;
		d.makeHtml(20);
	}
	
	private static void usage() {
		System.err.println("Usage : <52week range start %> <52 week range end %>");
	}

}
