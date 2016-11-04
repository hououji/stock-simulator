import info.hououji.Log;

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

	private int avgDay = 10;
	private int keepUpDay = 15;
	
	@Override
	public boolean detect(File file, int backDay) {
		try{
			CSV csv = new CSV(file) ;
			csv.setBaseDay(backDay);
			
			// ignore if too small
			if(this.isMarketCapGreat(csv.getCode(), 200) == false) return false ;
			if(csv.getLen() < 250) return false;
			if(csv.max(0, 10, CSV.VOL) < 0.1) return false;

			if(debug) Log.log("file:" + file.getAbsolutePath());

			for(int i = keepUpDay; i>=0; i--){
				double avg = csv.avg(i, avgDay, CSV.ADJ_CLOSE) ;
				double avgYesterday = csv.avg(i + 1, avgDay, CSV.ADJ_CLOSE) ;
				
				if(avgYesterday > avg) {
					return false; // fail
				}
			}
			
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
		return "keep-up-detector";
	}

	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String args[]) throws Exception {
		usage() ;
		KeepUpDetector d = new KeepUpDetector() ;
		d.avgDay = Integer.parseInt(args[0]) ;
		d.keepUpDay = Integer.parseInt(args[1]) ;
		d.makeHtml();
	}
	
	private static void usage() {
		System.err.println("Usage : <avgDay> <keepUpDay>");
	}

}
