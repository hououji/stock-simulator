package info.hououji.sim;

import java.text.SimpleDateFormat;

public class PsPeForOneStock {

	public static void main(String args[]) throws Exception {
		String code = "1193" ;
		EtnetHistIncome h = new EtnetHistIncome(code) ;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd") ;
		CSV csv = new CSV(code) ;
		System.out.println("                           PS=1      Price/L  Price/H     PS/L     PS/H") ;
		for(int i=0; i<=4;i++) {
			
			int start = csv.getItemNumFromDate(h.getStartDate(i)) ;
			int end = csv.getItemNumFromDate(h.getEndDate(i)) ;
			
			double high = csv.max(end, (start-end+1), CSV.HIGH) ;
			double low = csv.min(end, (start-end+1), CSV.LOW) ;
			
			double sale = h.dataset.getDouble("營業額", i) ;
			if(h.lastYearIsHalf && i == 0) {
				sale = sale * 2 ;
			}
			sale = sale * h.currRate ;
			double earn = h.dataset.getDouble("股東應佔溢利", i) * h.currRate ;
			double shareEarn = h.dataset.getDouble("每股盈利 (仙)", i) / 100  * h.currRate;
			long share = (long)(earn / shareEarn) ;
			double ps = sale / share ;
			
			System.out.println(sdf.format(h.getStartDate(i)) + " " + sdf.format(h.getEndDate(i))
//			 + " " + Misc.lpad(share+"",12) 
			 + " " + Misc.formatPrice(ps,9)
			 + " " +Misc.formatPrice(low,9) + " " + Misc.formatPrice(high, 9)
			 + " " + Misc.formatPrice(low/ps,9)+ " " + Misc.formatPrice(high/ps,9) 
					) ;
		}

		System.out.println() ;
		System.out.println("                         #of shares    PS=1      Price/L  Price/H     PS/L     PS/H") ;
		for(int i=0; i<=4;i++) {
			
			int start = csv.getItemNumFromDate(h.getStartDate(i)) ;
			int end = csv.getItemNumFromDate(h.getEndDate(i)) ;
			
			double high = csv.max(end, (start-end+1), CSV.HIGH) ;
			double low = csv.min(end, (start-end+1), CSV.LOW) ;
			
			double sale = h.dataset.getDouble("營業額", i) ;
			if(h.lastYearIsHalf && i == 0) {
				sale = sale * 2 ;
			}
			sale = sale * h.currRate ;
			double earn = h.dataset.getDouble("股東應佔溢利", i) * h.currRate ;
			double shareEarn = h.dataset.getDouble("每股盈利 (仙)", i) / 100  * h.currRate;
			long share = (long)(earn / shareEarn) ;
			double ps = sale / share ;
			
			System.out.println(sdf.format(h.getStartDate(i)) + " " + sdf.format(h.getEndDate(i))
			 + " " + Misc.lpad(share+"",12) 
			 + " " + Misc.formatPrice(ps,9)
			 + " " +Misc.formatPrice(low,9) + " " + Misc.formatPrice(high, 9)
			 + " " + Misc.formatPrice(low/ps,9)+ " " + Misc.formatPrice(high/ps,9) 
					) ;
		}

		
	}
}
