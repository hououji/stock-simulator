package info.hououji.sim;
import info.hououji.sim.Log;

import java.io.File;


public class MultiAvgDetector extends Detector {

	static boolean debug = false;
	
	int keepday = 3;
	int testDelay	= 3;
	String testDalayDate = "" ;
//	double sharpRatioMax = 1.0;


	public MultiAvgDetector() {
	}
	

	@Override
	public boolean detect(CSV csv, int backDays) {

		csv.setBaseDay(backDays);
		
		// ignore if too small
//		if(this.isMarketCapGreat(csv.getCode(), 50) == false) return false ;
		if(csv.getLen() < 250) return false;
		if(csv.max(0, 10, CSV.VOL) < 0.1) return false;

//		if(debug) Log.log("file:" + file.getAbsolutePath());
		Log.log("detect:" + csv.getCode());

		try{
			for(int i=keepday; i>=0; i--) {
				csv.setBaseDay(i + testDelay + backDays) ;
//				double a4 = csv.avg(0, 4, CSV.ADJ_CLOSE) ;
				double a10 = csv.avg(0, 10, CSV.ADJ_CLOSE) ;
				double a20 = csv.avg(0, 20, CSV.ADJ_CLOSE) ;
				double a50 = csv.avg(0, 50, CSV.ADJ_CLOSE) ;
				double a100 = csv.avg(0, 100, CSV.ADJ_CLOSE) ;
				double a250 = csv.avg(0, 250, CSV.ADJ_CLOSE) ;
				
				if(	a10 > a20 
					&& a20 > a50
					&& a50 > a100
//					&& (a250 - a10) / a10 > 0.05
					&& a250 > a10 
				){
					// 四線開花加 250 天高, 跌未好耐
//					return true;
				}else{
					return false;
				}
				
				Log.log("a250:"+a250+"\ta100:"+a100+"\ta50:"+a50+"\ta20:"+a20+"\ta10:"+a10+""); 
			}
		}catch(IndexOutOfBoundsException iex){
		}catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
		
		Detail d = new Detail(csv.getCode()) ;
		if(d.marketCap < 50) return false;
		
		csv.setBaseDay(0);
		testDalayDate = csv.getDate(testDelay) ;
		
		return true;
	}

	@Override
	public String getName() {
		return "multi-avg" ;
	}

	@Override
	public String getDesc() {
		return " ~ keepday : "+keepday + " delay(for testing):" + this.testDelay +"["+testDalayDate+"]";
	}
	
	public static void  main(String args[]) throws Exception {
		MultiAvgDetector d = new MultiAvgDetector () ;
		d.keepday = Integer.parseInt(args[0]) ;
		d.testDelay = Integer.parseInt(args[1]) ;
//		d.sharpRatioMax = Double.parseDouble(args[1]) ;
		d.makeHtml() ;
	}
	
	public static void printUsage() {
		System.out.println("MultiAvgDetector <keep days> <test delay (for testing)>");
	}
	
}
