import info.hououji.Log;

import java.io.File;


public class VolumeSpecialDetector{

	public static void main(String args[]) throws Exception {
		boolean debug = false;
		
		File dir = Downloader.getRecentDirectory() ;
		for(File file : dir.listFiles()) {
			CSV csv = new CSV(file) ;
			
			// ignore if too small
			if(csv.get(0, CSV.VOL_PRICE) < 10000000) continue;
			if(csv.getLen() < 250) continue;

			if(debug) Log.log("file:" + file.getAbsolutePath());

			// 5 day avg vol > 30 day avg vol * 10
			double maxRatio = 0 ;
			for(int i = 0; i<csv.getLen() - 31 && i < 250; i++){
				double avg2 = csv.avg(i, 2, CSV.VOL) ;
				double avg30 = csv.avg(i+1, 30, CSV.VOL) ;
				double ratio = avg2 / avg30 ;
				if(ratio > maxRatio ) maxRatio = ratio ;
				if(ratio > 5 && ratio < 10) {
					Log.log(csv.getName() + " " +csv.getDate(i) + " " + CSV.to2dp(ratio));
					i = i + 30;
				}
			}
			if(debug) Log.log("max ratio : " + CSV.to2dp(maxRatio));
			
		}
	}
}
