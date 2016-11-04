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

	double x = 1 ;
	
	public boolean detect(File file, int baseDay) {
		try{
			CSV csv = new CSV(file) ;
			csv.setBaseDay(baseDay);
			
			// ignore if too small
			if(this.isMarketCapGreat(csv.getCode(), 200) == false) return false ;
			if(csv.getLen() < 250) return false;
			if(csv.max(0, 10, CSV.VOL) < 0.1) return false;

//			if(debug) Log.log("file:" + file.getAbsolutePath());

			// 250 vol, out of 1 s.d.
			List<Double> vols = new ArrayList<Double>() ;
			double t = 0 ;
			for(int i=0; i<250; i++) {
				double v = csv.get(i, CSV.VOL) ;
				if(v == 0) continue;
				vols.add(v) ;
				t += v;
			}
			
			double avg = t / vols.size() ;
			double sd = this.sd(vols) ;
			double currVol = (vols.get(0) + vols.get(1) + vols.get(2) + vols.get(3))  / 4;    
			if(currVol < avg + sd * x) return false;
			System.out.println("code:" + csv.getCode() + ",sd:" + csv.to2dp( (currVol - avg )/sd));

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
		return " - volume out of S.D. : " + x ; 
	}
	
	public static void main(String args[]) throws Exception {
		
		VolumeSpecialDetector d = new VolumeSpecialDetector() ;
		d.x = Double.parseDouble(args[0]) ;
		d.makeHtml();
	}
	
	private void usage() {
		System.err.println("<volume out of X * S.D. (double)>");
	}
}
