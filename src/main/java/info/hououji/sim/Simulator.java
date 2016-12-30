package info.hououji.sim;
import info.hououji.sim.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class Simulator {

	boolean debug = false; 
	
	public void run(CSV csv, Detector d, Trader t) {
		if(csv.getLen() < 250) Log.log("file too short, code : " + csv.getCode());
		
		int count = 0 ;
		double totalRate = 0 ;
		
		int startBackDay = Math.min(250 * 8 , csv.getLen()-1) ;
//		int startBackDay = Math.min(250 * 10 , csv.getLen()-1) ;
//		int startBackDay = Math.min(100 , csv.getLen()) ;
		for(int i=startBackDay; i>0; i--) {
			csv.setBaseDay(0);
			boolean trigger = d.detect(csv, i) ;
			if(! trigger) continue;
			
			csv.setBaseDay(0);
			if(debug) System.out.println("trigger(i="+i+") : " + csv.getDate(i)) ;
			
			Trader.Result r = t.trade(csv, i) ;
			if(r != null) {
				double rate = (r.outValue - r.inValue) / r.inValue ;
				totalRate += rate ;
				count ++ ;
				
//				System.out.println( csv.getDate(r.startBackDays) + "," + csv.getDate(r.finalBackDays) 
//						+ "," + r.inValue + "," + r.outValue + "," + CSV.to2dp(rate*100)+"%");
				i = r.finalBackDays ;
			}else{
				// trade NOT end, it the last one
				break;
			}
		}
		System.out.println(csv.getCode() + " rate :" + totalRate) ;
	}
	
	public static void main (String args[]) throws Exception {
		
		Simulator s = new Simulator() ;
		Detector d = new AvgDetector(5,20) ;
		Trader t = new PercentageStopTrader(5) ;
		
		File dir = Downloader.getRecentDirectory() ;
		
//		run(new File(dir,"0015.csv")) ;
//		VolumeSpecialDetector d = new VolumeSpecialDetector() ;
		
//		HashMap<String, Double> map = new LinkedHashMap<String,Double>() ;
		
		File[] files = dir.listFiles() ;
		Arrays.sort(files);
		List<Integer> keepDays = new ArrayList<Integer>();
		for(File file : files) {
			try{
	//			if(file.getName().indexOf("0016") == -1) continue;
				System.out.println(file.getAbsolutePath());
				CSV csv = new CSV(file) ;
				s.run(csv, d, t);
	//			break;
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}

	}
}
