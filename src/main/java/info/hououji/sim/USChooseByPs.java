package info.hououji.sim;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class USChooseByPs {

	public static void main(String args[]) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd") ;
		DecimalFormat df = new DecimalFormat("0.##E0");
		
		code:for(File f : USDownloader.getRecentDirectory().listFiles()) {
			try{
				String code = f.getName().split("\\.")[0];
//				System.out.println(code) ;
				
				FinancialModeling mwi = new FinancialModeling(code) ;
				CSV csv = new CSV(f) ; 
				ArrayList<Double> lowPsList = new ArrayList<Double>() ;
				ArrayList<Double> highPsList = new ArrayList<Double>() ;

				double currPs1 =10000;
				String msg = "" ;

				for(int i = 0; i<=4 ;i ++) {
					
					//System.out.println(mwi.dataset.date.data.get(i) );
					//String year = mwi.data.get(i).get("fiscalDateEnding").substring(0,4) ;
					Date endDate = sdf.parse(mwi.data.get(i).get("date")) ;
					Calendar c = Calendar.getInstance();
					c.setTime(endDate);
					c.add(Calendar.YEAR, -1);
					c.add(Calendar.DATE, 1);
					Date startDate = c.getTime() ;
					
					int start = csv.getItemNumFromDate(startDate) ;
					int end = csv.getItemNumFromDate(endDate) ;
					
					if(start == -1 || end == -1) continue code ;
					
					double high = csv.max(end, (start-end+1), CSV.HIGH) ;
					double low = csv.min(end, (start-end+1), CSV.LOW) ;

//					double sale = 0;
////					try{
//						sale = Double.parseDouble(mwi.data.get(i).get("totalRevenue"));
////					}catch(Exception ex) {
////						sale = mwi.dataset.getDouble("Net Income", i) ;
////					}
//
//					double share = Double.parseDouble(mwi.data.get(i).get("commonStockSharesOutstanding"));
					
					double ps1 = Double.parseDouble(mwi.data.get(i).get("revenuePerShare")) ;
					if(i==0) currPs1 = ps1;

					lowPsList.add(low/ps1) ;
					highPsList.add(high/ps1) ;

					msg = msg + sdf.format(startDate) 
					+ " " + sdf.format(endDate)
//					 + " " + Misc.lpad(df.format(share), 13) 
					 + " " + Misc.formatPrice(ps1,8)
					 + " " +Misc.formatPrice(low,8) 
					 + " " + Misc.formatPrice(high, 8)
					 + " " + Misc.formatPrice(low/ps1,8)
					 + " " + Misc.formatPrice(high/ps1,8) 
					+ "\n" ;
				}
				double currPs = csv.get(0, CSV.ADJ_CLOSE) / currPs1 ;
				Collections.sort(lowPsList);
				Collections.sort(highPsList);
				double minLowPs = lowPsList.get(1) ;
				double minHighPs = highPsList.get(1) ;
				//if(currPs < minLowPs * 1.1 || currPs < minHighPs * 0.7 ) 
				{
					System.out.println("code:" + code  + ", curr price:" + Misc.trim(csv.get(0, CSV.ADJ_CLOSE)) + ",curr PS:" + Misc.trim(currPs) +",2nd min Low Ps:" + Misc.trim(minLowPs) + ",2nd min High Ps:" + Misc.trim(minHighPs)) ;
					System.out.println("                         PS=1.0   PRICE/L  PRICE/H  PS/L     PS/H") ;
					System.out.println(msg) ;
				}				
			}catch(Exception ex) {
				ex.printStackTrace(); 
			}

			
		}
	}
}
