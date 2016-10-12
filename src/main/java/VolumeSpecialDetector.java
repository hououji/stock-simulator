import info.hououji.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;


public class VolumeSpecialDetector extends Detector{

	int ratio = 2 ;
	int detectDayRange = 5; 
	
	public boolean detect(File file) {
		try{
			CSV csv = new CSV(file) ;
			
			// ignore if too small
			if(csv.get(0, CSV.VOL_PRICE) < 10000000) return false;
			if(csv.getLen() < 250) return false;

//			if(debug) Log.log("file:" + file.getAbsolutePath());

			// 5 day avg vol > 30 day avg vol * 10
			double maxRatio = 0 ;
			for(int i = 0; i<csv.getLen() - 31 && i < detectDayRange; i++){
				double avg2 = csv.avg(i, 2, CSV.VOL) ;
				double avg30 = csv.avg(i+30, 10, CSV.VOL) ;
				double ratio = avg2 / avg30 ;
				if(ratio > maxRatio ) maxRatio = ratio ;
				if(ratio > 2 && ratio < 10) {
					Log.log(csv.getName() + " " +csv.getDate(i) + " " + CSV.to2dp(ratio));
					i = i + 30;
				}
			}			
			return true;
		}catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	public String getName() {
		return "volume-speicald-detector" ;
	}
	
	public String getDesc() {
		return " - ratio : " + ratio ; 
	}
	
	public static void main(String args[]) throws Exception {
		
		VolumeSpecialDetector d = new VolumeSpecialDetector() ;
		d.ratio = Integer.parseInt(args[0]) ;
		d.detectDayRange = Integer.parseInt(args[1]) ;
		d.makeHtml();
	}
	
	private void usage() {
		System.err.println("<detect ratio> <detectDayRange>");
	}
}
