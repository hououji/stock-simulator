import info.hououji.Log;

import java.io.File;


public class KeepUpDetector {

	static boolean debug = false;

	
	public static void run(File file) {
		try{
			CSV csv = new CSV(file) ;
			
			// ignore if too small
			if(csv.get(0, CSV.VOL_PRICE) < 100000000) return;
			if(csv.getLen() < 250) return;

			if(debug) Log.log("file:" + file.getAbsolutePath());

			for(int i = 40; i>=0; i--){
				double avg10 = csv.avg(i, 10, CSV.ADJ_CLOSE) ;
				double avg10Yesterday = csv.avg(i + 1, 10, CSV.ADJ_CLOSE) ;
				
				if(avg10Yesterday > avg10) {
					return ; // fail
				}
			}
			
			// pass
			Log.log(csv.getName() + ", adf cls:" + csv.to2dp(csv.get(0, CSV.ADJ_CLOSE)) +", ex:" + csv.to2dp(csv.get(0, CSV.VOL_PRICE)));
			
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
