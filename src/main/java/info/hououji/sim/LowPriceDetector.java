package info.hououji.sim;
import info.hououji.sim.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;


public class LowPriceDetector extends Detector{

	static boolean debug = false;

	private int start = 10;
	private int end = 30;
	
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
			
			double max = csv.max(0, 250, CSV.ADJ_CLOSE) ;
			double min = csv.min(0, 250, CSV.ADJ_CLOSE) ;
			double curr = csv.get(0, CSV.ADJ_CLOSE) ;
			
			double p_start = min + (max-min) * start / 100 ;
			double p_end = min + (max-min) * end / 100 ;
			if(curr > p_end || curr < p_start) return false;
			
			double avg18 = csv.avg(0, 18, CSV.ADJ_CLOSE) ; 
			double avg4 = csv.avg(0, 4, CSV.ADJ_CLOSE) ;
			if(avg4 < avg18) return false ;
			
			// pass
			Log.log(csv.getName() + ", adf cls:" + csv.to2dp(csv.get(0, CSV.ADJ_CLOSE)) +", ex:" + csv.to2dp(csv.get(0, CSV.VOL_PRICE)));
			
			return true;
		}catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public String getName() {
		return "low-price-detector";
	}

	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return start + " - " + end;
	}
	
	public static void main(String args[]) throws Exception {
		usage() ;
		LowPriceDetector d = new LowPriceDetector() ;
		d.start = Integer.parseInt(args[0]) ;
		d.end = Integer.parseInt(args[1]) ;
		d.makeHtml();
	}
	
	private static void usage() {
		System.err.println("Usage : <52week range start %> <52 week range end %>");
	}

}
