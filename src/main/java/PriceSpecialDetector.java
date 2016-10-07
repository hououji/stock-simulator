import info.hououji.Log;

import java.io.File;


public class PriceSpecialDetector {

	static boolean debug = false;

	
	public static void run(File file) {
		try{
			CSV csv = new CSV(file) ;

			
			// ignore if too small
			if(csv.get(0, CSV.VOL_PRICE) < 100000000) return;
			if(csv.getLen() < 250) return;

			if(debug) Log.log("file:" + file.getAbsolutePath());

			for(int i = 30; i>=0; i--){
				double avg10 = csv.avg(i, 10, CSV.ADJ_CLOSE) ;
				double avg20 = csv.avg(i, 20, CSV.ADJ_CLOSE) ;
				double avg50 = csv.avg(i, 50, CSV.ADJ_CLOSE) ;
				double avg100 = csv.avg(i, 100, CSV.ADJ_CLOSE) ;
				double avg200 = csv.avg(i, 200, CSV.ADJ_CLOSE) ;

				if(avg10 > avg20 && avg20 > avg50 && avg50 > avg100 && avg100 > avg200)
				{
					Log.log(csv.getName() + " " +csv.getDate(i) + ", adf cls:" + csv.to2dp(csv.get(i, CSV.ADJ_CLOSE)) +", ex:" + csv.to2dp(csv.get(i, CSV.VOL_PRICE)));
					i = i - 30;
				}
			}
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
