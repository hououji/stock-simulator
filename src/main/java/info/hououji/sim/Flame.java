package info.hououji.sim;

import info.hououji.sim.MarketCapExcel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class Flame {

	static int searchPeriod = 250 ;
	static int period = 100 ;
	static int shortPeriod = 0 ;
	static double range = 5 ;		// %
	static double withinRate = 95;	// % 
	
	public static String check(CSV csv) {
		if(csv.getLen() < period + shortPeriod + 30) return null;
//		MarketCapExcel excel = new MarketCapExcel() ;
//		if(excel.getRow(csv.getCode()) == null) {
//			System.out.println("skip:" + csv.getCode()) ;
//			return null ;
//		}
//		if(excel.getRow(csv.getCode()).cap < 20 ) return null ;
		
		for(int i = 0; i<searchPeriod; i++) {
			csv.setBaseDay(i);
			double totalTx = 0 ;
			double totalVol = 0 ;
			for(int j = shortPeriod; j<period; j++) {
				totalTx += csv.get(j, CSV.ADJ_CLOSE) * csv.get(j, CSV.VOL) ;
				totalVol += csv.get(j, CSV.VOL) ;
			}
			if(totalVol == 0) return null;
			double avgPrice = totalTx / totalVol ;  
			double volWithinAvg = 0 ;
			for(int j = shortPeriod; j<period; j++) {
				double close = csv.get(j, CSV.ADJ_CLOSE) ;
				if(close > avgPrice * (1 - range/2/100) && close < avgPrice * (1+range/2/100)) {
					volWithinAvg += csv.get(j, CSV.VOL) ;
				}
			}
			
			if( volWithinAvg/totalVol > withinRate / 100 ) {
				// bingo
				System.out.println("" + csv.getCode() + "," + csv.getDate(0) + ",avgPrice:" + Misc.trim(avgPrice, 3) + ",with rate:" + Misc.trim(volWithinAvg/totalVol,2) ); 
				return csv.getDate(0) + ",price:" + Misc.trim(avgPrice, 3) + ",rate:" + Misc.trim(volWithinAvg/totalVol,2) ;
			}
		}
		
		return null;
	}
	
	
	public static void main(String args[]) throws Exception {
		
		System.out.println("Usage Concentration <searchPeriod(250)> <period(100)> <shortPeriod(10)> <range(% of flame)> <within rate (%, water mark of hit rate)>");
		
		searchPeriod = Integer.parseInt(args[0]) ;
		period = Integer.parseInt(args[1]) ;
		shortPeriod = Integer.parseInt(args[2]) ;
		range = Double.parseDouble(args[3]) ;
		withinRate = Double.parseDouble(args[4]) ;
		
		
		File dir = Downloader.getRecentDirectory() ;
		File[] files = dir.listFiles() ;
		Arrays.sort(files);
		
		String template = Misc.getFile("list-template.html") ;
		String header = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " - " + "Flame" ;
		String title = "Flame" ;
		StringBuffer content = new StringBuffer() ;

//		MarketCapExcel mc = new MarketCapExcel() ;
		for(File file : files) {
			try{
				CSV csv = new CSV(file) ;
//				if( ! csv.getCode().equals("1668")) continue;
				System.out.println("testing code:" + csv.getCode());
				
				String date = check(csv);
				if(date != null){
					String code = Downloader.getName(file) ;

//					Row r = mc.getRow(code) ;
					Detail r = new Detail(code) ;
					content.append("<div stock='"+code+"'><div class='title'>"+code+" "+r.name
							+"," + date
							+",PE:"+r.pe
							+",PB:"+r.pb
							+",Int:"+r.div+",Cap:"+r.marketCap+"億</div></div>\r\n") ;
				}
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}

		
//		for(int i=0; i<codes.size(); i++ ) {
//			try{
//				String code = codes.get(i) ;
//				MarketCapExcel mc = new MarketCapExcel() ;
//				Row r = mc.getRow(code) ;
////				Detail aa = new Detail(code) ;
//				content.append("<div stock='"+code+"'><div class='title'>"+code+" "+r.name+",PE:"+r.pe
//						+",PB:"+r.pb
//						+",Int:"+r.div+",Cap:"+r.cap+"億</div></div>\r\n") ;
//			}catch(Exception ex){}
//		}
		template = template.replace("#HEADER#", header + ", period:" + period) ;
		template = template.replace("#TITLE#", title) ;
		template = template.replace("#CONTENT#", content) ;
		
		File outputDir = new File("output") ;
		outputDir.mkdirs() ;
		
		FileOutputStream out = new FileOutputStream(new File(outputDir,  "flame" + ".html")) ;
		IOUtils.write(template, out);
		out.flush();
		out.close() ;

	}

	
}
