package info.hououji.sim;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaveAnalysis {
	
	static class Result{
		public Date start, end ;
		public double startP, endP ;
		public int change;
	}
	
	public static List<Result> exec(File file, double minChange, boolean debug) throws Exception {
		CSV csv = new CSV(file) ;
		String code = csv.getCode() ;
		int rangeStart = csv.getItemNumFromDate("2009-01-02") ;
//		int rangeStart = csv.getItemNumFromDate("2012-01-02") ;
		int rangeEnd = csv.getItemNumFromDate("2017-01-02") ;
		if(rangeStart == -1 || rangeEnd == -1) return null ;
		
		int wStart=-1, wEnd=-1 ;
		double wStartP = -1, wEndP = -1 ;
		int uptrade = 0;
		
		Map<Integer, Integer> allDateResult = new HashMap<Integer, Integer>() ;
		List<Result> results = new ArrayList<Result>() ;
		
		// 1) Start, find the first trade
		int curr;
		double min = csv.get(rangeStart, CSV.CLOSE);
		String minDate = csv.getDate(rangeStart) ;
		double max = csv.get(rangeStart, CSV.CLOSE);
		String maxDate = csv.getDate(rangeStart) ;
		for(curr = rangeStart; curr > rangeEnd; curr--) {
			if(csv.get(curr, CSV.CLOSE) > max) {
				max = csv.get(curr, CSV.CLOSE) ;
				maxDate = csv.getDate(curr) ;
			}
			if(csv.get(curr, CSV.CLOSE) < min) {
				min = csv.get(curr, CSV.CLOSE) ;
				minDate = csv.getDate(curr) ;
			}
			if( (max - min) / min > minChange) {
				// bingo
				if(minDate.compareTo(maxDate) > 0) {
					uptrade = -1;
					wStartP = max ;
					wStart = csv.getItemNumFromDate(maxDate) ;
					wEndP = min ;
					wEnd = csv.getItemNumFromDate(minDate) ;
				}else{
					uptrade = 1;
					wStartP = min ;
					wStart = csv.getItemNumFromDate(minDate) ;
					wEndP = max ;
					wEnd = csv.getItemNumFromDate(maxDate) ;
				}
				
//				System.out.println("uptrade:" + uptrade + "," + csv.getDate(wEnd));
				
				break; 
			}
		}
		
		if(uptrade == 0) {
			System.out.println("Initial fail") ;
			return null;
		}
		
		// 2) run to trade change
		while(true) {
			curr -- ;
			if(curr <=rangeEnd) break; // range end, finish
			
			// a) the wave con't
			if( (csv.get(curr, CSV.CLOSE) - wEndP) * uptrade > 0 ) {
				wEndP = csv.get(curr, CSV.CLOSE) ;
				wEnd = curr; 
			}
			
			// b) confirm the wave changed
			double currChange = (csv.get(curr, CSV.CLOSE) - csv.get(wEnd, CSV.CLOSE)) / csv.get(wEnd, CSV.CLOSE) ; 
			if( currChange * -1 * uptrade > minChange ) {
				// bingo, confirm trade change, then confirm last wave
				for(int i=wStart; i>=wEnd ;i--) {
					allDateResult.put(i, uptrade) ;
				}
				
				int rate = (int)((wStartP - wEndP) / wStartP *  -1 * 100) ;
				if(debug) {
					System.out.println("start:" + csv.getDate(wStart) + "," + csv.get(wStart, CSV.CLOSE) 
							+ ",end:" + csv.getDate(wEnd) + "," + csv.get(wEnd, CSV.CLOSE)
							+ ",change:" + rate)  ;
				}
				
				Result r = new Result() ;
				r.start = CSV.sdf.parse(csv.getDate(wStart)) ;
				r.end = CSV.sdf.parse(csv.getDate(wEnd)) ;
				r.startP = csv.get(wStart, CSV.CLOSE) ;
				r.endP = csv.get(wEnd, CSV.CLOSE) ;
				r.change = rate;
				results.add(r) ;
				
				uptrade = uptrade * -1 ;
				wStartP = wEndP ;
				wStart = wEnd ;
				wEndP = csv.get(curr, CSV.CLOSE) ;
				wEnd = curr ;
			}
			
		}
		
		return results;
		
//		System.out.println("\n\n - All Dates --") ;
//		List<Integer> dateList = new ArrayList<Integer>() ;
//		dateList.addAll(allDateResult.keySet()) ;
//		Collections.sort(dateList);
//		for(int i : dateList) {
//			System.out.println(csv.getDate(i) +"\t"+ csv.get(i, CSV.CLOSE) + "\tuptrand:" + allDateResult.get(i));
//		}
	}
	
	public static void main(String args[]) throws Exception {
		File[] files = Downloader.getRecentDirectory().listFiles() ;
		MarketCapExcel excel = new MarketCapExcel() ;
		for(File file : files) {
			try{
				CSV csv = new CSV(file) ;
				if(!excel.isMarketCapGreat(csv.getCode(), 200)) continue ;
				
				List<Result> results = exec(file, 0.2, false) ;
				if(results == null) continue;
				int years[] = {2016, 2015, 2014, 2013} ;
				boolean pass = true;
				for(int year : years) {
					int count = 0 ;
					for(Result r: results) {
						if(r.start.getYear() + 1900 == year ){
							count ++ ;
						}
					}
					if(count < 2) pass = false;
				}
				if(pass) System.out.println(file.getName() + " cap:" + excel.getRow(csv.getCode()).cap);
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}

	}
}
