import info.hououji.Log;

import java.io.File;


public class AvgDetector extends Detector {

	static boolean debug = false;
	
	int longAvg;
	int shortAvg ;

	public AvgDetector(int _longAvg, int _shortAvg) {
		longAvg = _longAvg ;
		shortAvg = _shortAvg ;
	}
	

	@Override
	public boolean detect(CSV csv, int backDays) {

		csv.setBaseDay(backDays);
		
		// ignore if too small
		if(this.isMarketCapGreat(csv.getCode(), 200) == false) return false ;
		if(csv.getLen() < 250) return false;
		if(csv.max(0, 10, CSV.VOL) < 0.1) return false;

//		if(debug) Log.log("file:" + file.getAbsolutePath());

		double l = csv.avg(0, longAvg, CSV.ADJ_CLOSE) ;
		double s = csv.avg(0, shortAvg, CSV.ADJ_CLOSE) ;
		
		return s > l ;
	}

	@Override
	public String getName() {
		return "Long short Avg " ;
	}

	@Override
	public String getDesc() {
		return " ~ long : "+longAvg+" short : " + shortAvg;
	}
}
