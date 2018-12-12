package info.hououji.sim;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class USChooseByPs {

	public static void main(String args[]) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd") ;
		
		code:for(File f : USDownloader.getRecentDirectory().listFiles()) {
			try{
				String code = f.getName().split("\\.")[0];
//				System.out.println(code) ;
				
				MarketWatchIncome mwi = new MarketWatchIncome(code) ;
				CSV csv = new CSV(f) ; 
				ArrayList<Double> lowPsList = new ArrayList<Double>() ;
				ArrayList<Double> highPsList = new ArrayList<Double>() ;

				double currPs1 =10000;
				String msg = "" ;

				for(int i = 0; i<=4 ;i ++) {
					
					//System.out.println(mwi.dataset.date.data.get(i) );
					String year = mwi.dataset.date.data.get(i) ;
					Date startDate = sdf.parse(year + "-01-01") ;
					Date endDate = sdf.parse(year + "-12-31") ;
					int start = csv.getItemNumFromDate(startDate) ;
					int end = csv.getItemNumFromDate(endDate) ;
					
					if(start == -1 || end == -1) continue code ;
					
					double high = csv.max(end, (start-end+1), CSV.HIGH) ;
					double low = csv.min(end, (start-end+1), CSV.LOW) ;

					double sale = mwi.dataset.getDouble("Sales/Revenue", i) ;
					double share = mwi.dataset.getDouble("Diluted Shares Outstanding", i) ;
					double ps1 = sale / share ;
					if(i==4) currPs1 = ps1;

					lowPsList.add(low/ps1) ;
					highPsList.add(high/ps1) ;

					msg = msg + sdf.format(startDate) 
					+ " " + sdf.format(endDate)
					 + " " + Misc.lpad(share+"", 13) 
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
				if(currPs < minLowPs * 1.1 || currPs < minHighPs * 0.7 ) 
				{
					System.out.println("code:" + code  + ", curr price:" + Misc.trim(csv.get(0, CSV.ADJ_CLOSE)) + ",curr PS:" + Misc.trim(currPs) +",2nd min Low Ps:" + Misc.trim(minLowPs) + ",2nd min High Ps:" + Misc.trim(minHighPs)) ;
					System.out.println("                         # of Share     PS=1.0   PRICE/L  PRICE/H  PS/L     PS/H") ;
					System.out.println(msg) ;
				}				
			}catch(Exception ex) {
				ex.printStackTrace(); 
			}

			
		}
	}
}
