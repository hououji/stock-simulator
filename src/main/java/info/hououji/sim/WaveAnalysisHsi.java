package info.hououji.sim;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaveAnalysisHsi {
	
	static class Result{
		public Date start, end ;
		public double startP, endP ;
		public int change;
		public int uptrade = 0;
	}
	
	public static void trendCheck(File file, List<Result> list) {
		CSV csv = new CSV(file) ;
		double lastOpt = 0 ;
		double localOpt ;
		for(Result r : list) {
//			int rangeStart = csv.getItemNumFromDate("2009-01-02") ;
			int rangeStart = csv.getItemNumFromDate(r.start) ;
			int rangeEnd = csv.getItemNumFromDate(r.end) ;
			
			System.out.println(" == NEW trend ==") ;
			
			localOpt = (int)csv.get(rangeStart, CSV.ADJ_CLOSE) ;
			for(int i = rangeStart; i>=rangeEnd; i--) {
				
				double curr = (int)csv.get(i, CSV.ADJ_CLOSE) ; 
				
				if(r.uptrade == 1) {
					if( curr > localOpt) localOpt = curr ; 
				}
				if(r.uptrade == -1) {
					if(curr < localOpt) localOpt = curr ; 
				}
				
				double rate = Misc.trim(Math.abs(localOpt - curr) / curr, 3) ;
				int diff = (int)(localOpt - curr) ;
				
				System.out.print(
					r.uptrade 
					+ "," + csv.getDate(i) 
					+ "," + (int)curr 
					+ ","+(int)localOpt
					+ "," + diff
					+ ","+ rate) ;
				
				if(lastOpt != 0) {
					double lastRate = Misc.trim(Math.abs(curr - lastOpt) / curr, 3);
					System.out.println("," + lastRate) ;
					if(lastRate > 0.02) lastOpt = 0;
				}else{
					System.out.println(",-") ;
				}
			}
			
			lastOpt = localOpt; 
			
		}
	}
	
	public static List<Result> exec(File file, double minChange, boolean debug) throws Exception {
		CSV csv = new CSV(file) ;
		String code = csv.getCode() ;
		int count = 0 ;
//		int rangeStart = csv.getItemNumFromDate("2009-01-02") ;
		int rangeStart = csv.getLen()-1;
		int rangeEnd =  0;
		System.out.println("start:" + csv.getDate(rangeStart) + ",end:" + csv.getDate(rangeEnd)) ;
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
					System.out.println("start:" + csv.getDate(wStart) 
							+ "," + (int)csv.get(wStart, CSV.CLOSE) 
							+ ",end:" + csv.getDate(wEnd) + "," + (int)csv.get(wEnd, CSV.CLOSE)
							+ ",change:" + rate)  ;
				}
				
				Result r = new Result() ;
				r.start = CSV.sdf.parse(csv.getDate(wStart)) ;
				r.end = CSV.sdf.parse(csv.getDate(wEnd)) ;
				r.startP = csv.get(wStart, CSV.CLOSE) ;
				r.endP = csv.get(wEnd, CSV.CLOSE) ;
				r.change = rate;
				r.uptrade = uptrade ;
				results.add(r) ;
				
				uptrade = uptrade * -1 ;
				wStartP = wEndP ;
				wStart = wEnd ;
				wEndP = csv.get(curr, CSV.CLOSE) ;
				wEnd = curr ;
				count ++ ;

			}
			
		}
		
		System.out.println("count:" + count) ;
		
		return results;
		
//		System.out.println("\n\n - All Dates --") ;
//		List<Integer> dateList = new ArrayList<Integer>() ;
//		dateList.addAll(allDateResult.keySet()) ;
//		Collections.sort(dateList);
//		for(int i : dateList) {
//			System.out.println(csv.getDate(i) +"\t"+ csv.get(i, CSV.CLOSE) + "\tuptrand:" + allDateResult.get(i));
//		}
	}
	
	public static int str2int(String s) {
		return (int)Double.parseDouble(s);
	}
	
	public static void main(String args[]) throws Exception {
		
		File file = new File("C:/Users/hououji/git/stock-simulator/hsi.csv") ;
		
		List<Result> list = WaveAnalysisHsi.exec(file, 0.04, true) ;
		trendCheck(file, list) ;

	}
}
