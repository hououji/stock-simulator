package info.hououji.sim;
import info.hououji.sim.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;


public class KeepUpDetector extends Detector{

	static boolean debug = false;

	private int longAvg = 20 ;
	private int avgDay = 5;
	private int keepUpDay = 15;
	private int marketCap = 200 ;
	
	@Override
	public boolean detect(CSV csv, int backDay) {
		try{
			csv.setBaseDay(backDay);
			
			// ignore if too small
//			if(this.isMarketCapGreat(csv.getCode(), marketCap) == false) return false ;
			if(csv.getLen() < 250) return false;
			if(csv.max(0, 10, CSV.VOL) < 0.1) return false;

//			if(debug) Log.log("file:" + file.getAbsolutePath());

			for(int i = keepUpDay; i>=0; i--){
				double avg = csv.avg(i, avgDay, CSV.ADJ_CLOSE) ;
				double avgLong = csv.avg(i, longAvg, CSV.ADJ_CLOSE) ;
				
				if(avgLong > avg) {
					return false; // fail
				}
			}

			Detail d = new Detail(csv.getCode()) ;
			if(d.marketCap < marketCap) return false;

			// pass
			Log.log(csv.getName() + ", adf cls:" + csv.to2dp(csv.get(0, CSV.ADJ_CLOSE)) +", vol price:" + csv.to2dp(csv.get(0, CSV.VOL_PRICE)));
			
			return true;
		}catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public String getName() {
		return "keep-up-detector";
	}

	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "avg:"+avgDay+"/"+longAvg+",keep up:" + keepUpDay + ",market cap:" + marketCap;
	}
	
	public static void main(String args[]) throws Exception {
		usage() ;
		KeepUpDetector d = new KeepUpDetector() ;
		d.avgDay = Integer.parseInt(args[0]) ;
		d.longAvg = Integer.parseInt(args[1]) ;
		int keepUpDay = Integer.parseInt(args[2]) ;
		d.marketCap = Integer.parseInt(args[3]) ;
		while(keepUpDay > 0) {
			Log.log("keepUpDay : "  +keepUpDay) ;
			d.keepUpDay = keepUpDay;
			int size = d.makeHtml();
			if(size > 5) break;
			keepUpDay -- ;
		}
	}
	
	private static void usage() {
		System.err.println("Usage : <avgDay> <longAvgDay> <keepUpDay> <market cap>");
	}

}
