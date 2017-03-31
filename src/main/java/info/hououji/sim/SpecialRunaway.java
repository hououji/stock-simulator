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

public class SpecialRunaway {

	static int period = 55 ;
	static double gap = 9 ;
	static double change = 40 ;
	
	public static String check(CSV csv) {
		if(csv.getLen() < 5 * 250) return null;
		MarketCapExcel excel = new MarketCapExcel() ;
		if(excel.getRow(csv.getCode()) == null) {
//			System.out.println("skip:" + csv.getCode()) ;
			return null ;
		}
		if(excel.getRow(csv.getCode()).cap < 20 ) return null ;
		
		csv.setBaseDay(0);
		for(int i=0; i<=period; i++) {
			double oldHigh = csv.get(i+1, CSV.HIGH) ;
			double newLow = csv.get(i, CSV.LOW) ;
			double oldClose = csv.get(i+1, CSV.LOW) ;
			double newHigh = csv.get(i, CSV.HIGH) ;
			
			double oldAdj = csv.get(i+1, CSV.ADJ_CLOSE) ;
			double newAdj = csv.get(i, CSV.ADJ_CLOSE) ;
			
			double stockGap = (newLow - oldHigh) / oldHigh ;
			
			if( (newLow - oldHigh) / oldHigh > gap 
//				&& newHigh > oldClose * (1 + change/100)
//				&& newAdj > oldAdj * (1 + change/100 * 0.3)
			){
				System.out.println("" + csv.getCode() + "," + csv.getDate(i) +  ",change:" + (int)((newHigh - oldClose)/oldClose * 100) + ",gap:" + (int)(stockGap*100)); 
				return csv.getDate(i+1) ;
			}
		}
		
		return null;
	}
	
	
	public static void main(String args[]) throws Exception {
		
		System.out.println("Usage SpecialRunaway <period> <gap> <change(optional)>");
		
		period = Integer.parseInt(args[0]) ;
		gap = Double.parseDouble(args[1]) ;
		if(args.length >= 3) {
			change = Double.parseDouble(args[2]) ;
		}else{
			change = gap;
		}
		
		System.out.println("period:" + period + ",gap:" + gap + ",change:" + change);
		
		File dir = Downloader.getRecentDirectory() ;
		File[] files = dir.listFiles() ;
		Arrays.sort(files);
		
		String template = Misc.getFile("list-template.html") ;
		String header = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " - " + "Special Runaway" + " period:"  + period + ",gap:" + gap;
		String title = "Special Runaway" ;
		StringBuffer content = new StringBuffer() ;

		MarketCapExcel mc = new MarketCapExcel() ;
		for(File file : files) {
			try{
				CSV csv = new CSV(file) ;
//				if( ! csv.getCode().equals("1668")) continue;
				
				String date = check(csv);
				if(date != null){
//					System.out.println("code:" + csv.getCode());
					String code = Downloader.getName(file) ;

					Row r = mc.getRow(code) ;
//					Detail aa = new Detail(code) ;
					content.append("<div stock='"+code+"'><div class='title'>"+code+" "+r.name
							+"," + date
							+",PE:"+r.pe
							+",PB:"+r.pb
							+",Int:"+r.div+",Cap:"+r.cap+"億</div></div>\r\n") ;
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
		
		FileOutputStream out = new FileOutputStream(new File(outputDir,  "special-runaway" + ".html")) ;
		IOUtils.write(template, out);
		out.flush();
		out.close() ;

	}

	
}
