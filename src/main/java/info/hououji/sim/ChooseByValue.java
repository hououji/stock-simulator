package info.hououji.sim;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ChooseByValue {

	public static void main(String args[]) throws Exception {
		List <String > codes = exec() ;
		//List<String> codes = new ArrayList<String>() ;
//		codes.add("3360") ;
		
		double currPs1 =10000;
		code : for(String code  : codes) {
			String msg = "" ;
			try{
				EtnetHistIncome h = new EtnetHistIncome(code) ;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd") ;
				CSV csv = new CSV(code) ;
//				double minLowPs = 10000 ; 
//				double minHighPs = 10000 ;
				ArrayList<Double> lowPsList = new ArrayList<Double>() ;
				ArrayList<Double> highPsList = new ArrayList<Double>() ;
				for(int i=0; i<=4;i++) {
					
					int start = csv.getItemNumFromDate(h.getStartDate(i)) ;
					int end = csv.getItemNumFromDate(h.getEndDate(i)) ;
					
					if(start == -1 || end == -1) continue code ;
					
					double high = csv.max(end, (start-end+1), CSV.HIGH) ;
					double low = csv.min(end, (start-end+1), CSV.LOW) ;
					
					double sale = h.dataset.getDouble("營業額", i) ;
					if(h.lastYearIsHalf && i==0) {
						sale = sale * 2 ;
					}
					sale = sale * h.currRate ;
					double earn = h.dataset.getDouble("股東應佔溢利", i) * h.currRate ;
					double shareEarn = h.dataset.getDouble("每股盈利 (仙)", i) / 100  * h.currRate;
					long share = (long)(earn / shareEarn) ;
		//			System.out.println("share:" + Misc.formatMoney(share)) ;
					
		//			System.out.println("earn ratio:" + earnRate);
		//			double earnRate = h.dataset.getDouble("股東應佔溢利", 0) / h.dataset.getDouble("營業額", 0) ;
					double ps1 = sale / share ;
					if(i==0) currPs1 = ps1;
					
					lowPsList.add(low/ps1) ;
					highPsList.add(high/ps1) ;
					
//					msg = msg + h.dataset.getDouble("營業額", i) + " " + sale + " " + h.currRate + " ";
					
					msg = msg + sdf.format(h.getStartDate(i)) 
					+ " " + sdf.format(h.getEndDate(i))
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
				if(currPs < minLowPs * 1.1 || currPs < minHighPs * 0.7 ) {
					System.out.println("code:" + code + " " + h.name + ",curr price:" + Misc.trim(csv.get(0, CSV.ADJ_CLOSE)) + ",curr PS:" + Misc.trim(currPs) +",2nd min Low Ps:" + Misc.trim(minLowPs) + ",2nd min High Ps:" + Misc.trim(minHighPs)) ;
					System.out.println("                         # of Share     PS=1.0   PRICE/L  PRICE/H  PS/L     PS/H") ;
					System.out.println(msg) ;
				}
			}catch(Exception ex) {
				ex.printStackTrace(); 
			}
		}

	}
	
	public static List<String> exec() throws Exception {
		File dir = Downloader.getRecentDirectory() ;
		File[] files = dir.listFiles() ;
		Arrays.sort(files);
		List<String> codes = new ArrayList<String>() ;
		List<String> results = new ArrayList<String>() ;
		
		
		code: for(File file : files) {
			String code = file.getName().substring(0,4) ;
			System.out.println(code) ;
			try{
				
				CSV csv = new CSV(file) ;
				
				EtnetHistIncome e = new EtnetHistIncome(code) ;
				EtnetHistRatio r = new EtnetHistRatio(code) ;
				
				double roe[] = new double[5] ;
				int passCount = 0 ;
				for(int i=0; i<roe.length; i++) {
					roe[i] = r.dataset.getDouble("股東資金回報率", i) ;
					if(roe[i] > 12) passCount ++ ;
				}
				if(passCount < 4) continue;
				
				double sale = e.dataset.getDouble("營業額", 0) ;
				if(e.lastYearIsHalf) {
					sale = sale * 2 ;
				}
				sale = sale * e.currRate ;
				if(sale < 800000000) continue code;

				double earn = e.dataset.getDouble("股東應佔溢利", 0) - e.dataset.getDouble("投資物業公平值變動及減值", 0) - e.dataset.getDouble("其他項目公平值變動及減值", 0);
				if(e.lastYearIsHalf) {
					earn = earn * 2 ;
				}
				earn = earn * e.currRate ;
				if(earn < 800000000) continue code;

				
//				if(e.dataset.getDouble("投資物業公平值變動及減值", 0) > e.dataset.getDouble("股東應佔溢利", 0) * 0.15 ) continue;
//				if(e.dataset.getDouble("其他項目公平值變動及減值", 0) > e.dataset.getDouble("股東應佔溢利", 0) * 0.15 ) continue;

				
				codes.add(code) ;

				double min = csv.min(0, 1, CSV.ADJ_CLOSE) ;
				String week52 = csv.min(0, 250, CSV.ADJ_CLOSE) + " ~ " + csv.max(0, 250, CSV.ADJ_CLOSE) ;
				Detail d = new Detail(code);
				EtnetHistRatio ehr = new EtnetHistRatio(code) ;
				String s = "";
				s = s + "" + code + Misc.pad(e.name, 10) ;
				s = s +  ";" + min ; 
				s = s + ";=googlefinance(\"HKG:"+code+"\",\"price\")" ;
				s = s + ";52周:"+ week52 + " PE:" + d.pe + " PB:" + d.pb + " 息率:" + d.div + " 市值:" + d.marketCap ;
				
				s = s + ";" +ehr.dataset.getRow("純利率 (%)").toString() ;
				s = s + ";" +ehr.dataset.getRow("股東資金回報率 (%)").toString();

				results.add(s) ;
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}	

		System.out.println("=== Result === : " + codes.size()) ;
		
		for(String s : results) {
			System.out.println(s) ;
		}
		
		return codes;
	}
}
