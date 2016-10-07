import info.hououji.Log;

import java.io.File;


public class AvgDetector {

	static boolean debug = false;

	
	public static void run(File file) {
		try{
			CSV csv = new CSV(file) ;

			
			// ignore if too small
			if(csv.get(0, CSV.VOL_PRICE) < 100000000) return;
			if(csv.getLen() < 250) return;

			if(debug) Log.log("file:" + file.getAbsolutePath());

			// 5 day avg vol > 30 day avg vol * 10
			double maxRatio = 0 ;
			for(int i = 30; i>=0; i--){
				double avg2 = csv.avg(i, 5, CSV.ADJ_CLOSE) ;
				double avg30 = csv.avg(i+30, 60, CSV.ADJ_CLOSE) ;
				double ratio = avg2 / avg30 ;
				if(ratio > maxRatio ) maxRatio = ratio ;
				if(ratio > 1.2 && ratio < 5) {
					Log.log(csv.getName() + " " +csv.getDate(i) + ",ratio: " + CSV.to2dp(ratio) + ", ex:" + csv.to2dp(csv.get(i, CSV.VOL_PRICE)));
					i = i - 30;
				}
			}
			if(debug) Log.log("max ratio : " + CSV.to2dp(maxRatio));
		}catch(Exception ex) {
			ex.printStackTrace(); 
		}
		
	}
	
	public static void main(String args[]) throws Exception {
	
		File dir = Downloader.getRecentDirectory() ;
		
//		run(new File(dir,"0015.csv")) ;
		
		for(File file : dir.listFiles()) {
			run(file) ;
		}
	}
}
