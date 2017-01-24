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

public class SpecialChange {

	static int period = 55 ;
	static double rate = 40 ;
	
	public static String check(CSV csv) {
		if(csv.getLen() < 5 * 250) return null;
		MarketCapExcel excel = new MarketCapExcel() ;
		if(excel.getRow(csv.getCode()).cap < 50 ) return null ;
		
		csv.setBaseDay(0);
		for(int i=period; i>0; i--) {
			double lowClose = csv.min(i, 5, CSV.LOW) ;
			double maxHigh = csv.max(i, 5, CSV.HIGH) ;
			if( maxHigh > lowClose * (1+rate / 100) ) return csv.getDate(i+1) ;
		}
		
		return null;
	}
	
	
	public static void main(String args[]) throws Exception {
		
		System.out.println("Usage SpecialChange <period> <rate>");
		
		period = Integer.parseInt(args[0]) ;
		rate = Double.parseDouble(args[1]) ;
		
		File dir = Downloader.getRecentDirectory() ;
		File[] files = dir.listFiles() ;
		Arrays.sort(files);
		
		String template = Misc.getFile("list-template.html") ;
		String header = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " - " + "Special Change" + " period:"  + period + ",rate:" + rate;
		String title = "Special Change" ;
		StringBuffer content = new StringBuffer() ;

		MarketCapExcel mc = new MarketCapExcel() ;
		for(File file : files) {
			try{
				CSV csv = new CSV(file) ;
				
				String date = check(csv);
				if(date != null){
					System.out.println("code:" + csv.getCode());
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
		
		FileOutputStream out = new FileOutputStream(new File(outputDir,  "special-change" + ".html")) ;
		IOUtils.write(template, out);
		out.flush();
		out.close() ;

	}

	
}
