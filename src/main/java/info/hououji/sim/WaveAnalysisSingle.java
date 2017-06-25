package info.hououji.sim;

import java.io.File;

public class WaveAnalysisSingle {

	public static void main(String args[]) throws Exception {
		
		File dir = Downloader.getRecentDirectory() ;
		File file = new File(dir, "1666.csv") ;

//		File file = new File("C:/Users/hououji/git/stock-simulator/hsi.csv") ;
		
		WaveAnalysis.exec(file, 0.1, true) ;
	}
}
