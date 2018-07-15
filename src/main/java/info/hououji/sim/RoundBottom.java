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

public class RoundBottom {

	static int period = 10 ;
	
	public static String check(CSV csv) {
		if(csv.getLen() < 2 * 250) return null;
//		MarketCapExcel excel = new MarketCapExcel() ;
		
		csv.setBaseDay(0);
		
		for(int i=period; i>=0; i--) {
			
			double last6month = csv.max(i+1, 120, CSV.ADJ_CLOSE) ;
			
			if(csv.get(i, csv.ADJ_CLOSE) > last6month)
			{
				boolean volTest = false;
				for(int j= Math.max(0, i-5); j<=i+5; j++) {
					if(csv.avg(j, 3, CSV.VOL) > csv.avg(j, 20, CSV.VOL) * 3) {
						volTest = true ;
						break;	
					}
				}
				
				if( ! volTest) {
					continue;
				}
				
				// 6 month max current
				// then check if 250ma > 100ma in previous 6 months, to check it is down trend before
				boolean maTestPass = false;
				for(int j=0; j<=120; j++) {
					//double ma250 = csv.avg(j+1, 250, CSV.ADJ_CLOSE) ;
					double ma100 = csv.avg(j+1, 100, CSV.ADJ_CLOSE) ;
					double ma50 = csv.avg(j+1, 50, CSV.ADJ_CLOSE) ;
					if(ma100 > ma50) {
						maTestPass = true; 
						break ;
					}
				}
				if( ! maTestPass) continue;
				
				System.out.println("bingo : " + csv.getCode() + ",i=" + i + ",date:" + csv.getDate(i) + ",last6month:" + last6month + ",curr:" +  csv.get(i, csv.ADJ_CLOSE));

				Detail d = new Detail(csv.getCode()) ;
				if(d.marketCap < 10 && false) { // no checking first
					System.out.println("market cap : " + d.marketCap + ", skip") ;
					return null ;
				}

				return csv.getDate(i) ;
			}
		}
		
		return null;
	}
	
	
	public static void main(String args[]) throws Exception {
		
		System.out.println("Usage RoundBottom <period> ");
		
//		period = Integer.parseInt(args[0]) ;
		
		System.out.println("period:" + period );
		
		File dir = Downloader.getRecentDirectory() ;
		File[] files = dir.listFiles() ;
		Arrays.sort(files);
		
		String template = Misc.getFile("list-template.html") ;
		String header = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " - " + "Round Bottom (6 months)";
		String title = "Round Bottom" ;
		StringBuffer content = new StringBuffer() ;

//		MarketCapExcel mc = new MarketCapExcel() ;
		for(File file : files) {
			try{
				CSV csv = new CSV(file) ;
//				if( ! csv.getCode().equals("0204")) continue;
				
				String date = check(csv);
				if(date != null){
//					System.out.println("code:" + csv.getCode());
					String code = Downloader.getName(file) ;

//					Row r = mc.getRow(code) ;
//					Detail aa = new Detail(code) ;
					Detail d = new Detail(csv.getCode()) ;
					content.append("<div stock='"+code+"'><div class='title'>"+code+" "+d.name
							+"," + date
							+",PE:"+d.pe
							+",PB:"+d.pb
							+",Int:"+d.div+",Cap:"+d.marketCap+"億</div></div>\r\n") ;
					System.out.println("<div stock='"+code+"'><div class='title'>"+code+" "+d.name
							+"," + date
							+",PE:"+d.pe
							+",PB:"+d.pb
							+",Int:"+d.div+",Cap:"+d.marketCap+"億</div></div>\r\n") ;
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
		
		FileOutputStream out = new FileOutputStream(new File(outputDir,  "round-bottom" + ".html")) ;
		IOUtils.write(template, out);
		out.flush();
		out.close() ;

	}

	
}
