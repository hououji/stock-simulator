package info.hououji.sim;

import java.text.SimpleDateFormat;

public class PsPeForOneStock {

	public static void main(String args[]) throws Exception {
		String code = "3918" ;
		EtnetHistIncome h = new EtnetHistIncome(code) ;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd") ;
		CSV csv = new CSV(code) ;
		for(int i=0; i<=4;i++) {
			
			int start = csv.getItemNumFromDate(h.getStartDate(i)) ;
			int end = csv.getItemNumFromDate(h.getEndDate(i)) ;
			
			double high = csv.max(end, (start-end+1), CSV.CLOSE) ;
			double low = csv.min(end, (start-end+1), CSV.CLOSE) ;
			
			double sale = h.dataset.getDouble("營業額", 0) ;
			if(h.lastYearIsHalf) {
				sale = sale * 2 ;
			}
			sale = sale * h.currRate ;
			double earn = h.dataset.getDouble("股東應佔溢利", 0) * h.currRate ;
			double shareEarn = h.dataset.getDouble("每股盈利 (仙)", 0) / 100  * h.currRate;
			long share = (long)(earn / shareEarn) ;
//			System.out.println("share:" + Misc.formatMoney(share)) ;
			
//			System.out.println("earn ratio:" + earnRate);
			double earnRate = h.dataset.getDouble("股東應佔溢利", 0) / h.dataset.getDouble("營業額", 0) ;
			double ps = sale / share ;
			
			System.out.println(sdf.format(h.getStartDate(i)) + " " + sdf.format(h.getEndDate(i))
			 + " " +start + " " + end
			 + " " +low + " " + high
			 + " " + Misc.trim(low/ps, 2)+ " " + Misc.trim(high/ps, 2) 
					) ;
		}
		
	}
}
