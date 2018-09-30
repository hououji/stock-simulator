package info.hououji.sim;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class ChooseByPBToHtml {

	public static void main(String args[]) throws Exception {
		List <String > codes = exec() ;
		//List<String> codes = new ArrayList<String>() ;
//		codes.add("3360") ;
		
		String template = Misc.getFile("list-template.html") ;
		String header = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " - " + "Choose by PB" ;
		String title = "Choose by PB" ;
		StringBuffer content = new StringBuffer() ;
		StringBuffer text = new StringBuffer() ;
		
		text.append("<table border='1'><tr><th>Code</th><th>Name</th><th>Price</th><th>curr. PB</th>") ;
		text.append("<th>min PB/L</th><th>2nd min PB/L</th><th>min PB/H</th><th>2nd PB/H</th></tr>") ;
		
		double currPB =10000;
		code : for(String code  : codes) {
			String msg = "" ;
			try{
				EtnetHistIncome h = new EtnetHistIncome(code) ;
				Detail d = new Detail(code);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd") ;
				CSV csv = new CSV(code) ;
//				double minLowPs = 10000 ; 
//				double minHighPs = 10000 ;
				ArrayList<Double> lowPsList = new ArrayList<Double>() ;
				ArrayList<Double> highPsList = new ArrayList<Double>() ;
//				ArrayList<Double> lowPriceList = new ArrayList<Double>() ;
//				ArrayList<Double> highPriceList = new ArrayList<Double>() ;
				double currBookValue = 10000;
				for(int i=0; i<=4;i++) {
					
					int start = csv.getItemNumFromDate(h.getStartDate(i)) ;
					int end = csv.getItemNumFromDate(h.getEndDate(i)) ;
					
					if(start == -1 || end == -1) continue code ;
					
					double high = csv.max(end, (start-end+1), CSV.HIGH) ;
					double low = csv.min(end, (start-end+1), CSV.LOW) ;
					
					double bookValue = h.dataset.getDouble("每股帳面資產淨值", i) ;
					bookValue = bookValue * h.currRate ;
					if(i==0)currBookValue = bookValue;
					
					double earn = h.dataset.getDouble("股東應佔溢利", i) * h.currRate ;
					double shareEarn = h.dataset.getDouble("每股盈利 (仙)", i) / 100  * h.currRate;
					long share = (long)(earn / shareEarn) ;
					
					lowPsList.add(low/bookValue) ;
					highPsList.add(high/bookValue) ;
					
//					msg = msg + h.dataset.getDouble("營業額", i) + " " + sale + " " + h.currRate + " ";
					
					msg = msg + "<tr>" + "<td>" + sdf.format(h.getStartDate(i)) + "</td>" 
					+ "<td>" + sdf.format(h.getEndDate(i))+ "</td>"
					 + "<td>" + Misc.lpad(share+"", 13) + "</td>"
					 + "<td>" + Misc.formatPrice(bookValue,8)+ "</td>"
					 + "<td>" +Misc.formatPrice(low,8) + "</td>"
					 + "<td>" + Misc.formatPrice(high, 8)+ "</td>"
					 + "<td>" + Misc.formatPrice(low/bookValue,8)+ "</td>"
					 + "<td>" + Misc.formatPrice(high/bookValue,8) + "</td>"
					+ "</tr>" ;
				}
				currPB = csv.get(0, CSV.ADJ_CLOSE) / currBookValue ;
				Collections.sort(lowPsList);
				Collections.sort(highPsList);
				double secondMinLowPs = lowPsList.get(1) ;
				double secondMinHighPs = highPsList.get(1) ;
				if(currPB < secondMinLowPs * 1.1 || currPB < secondMinHighPs * 0.6 ) {
//					System.out.println("code:" + code + ",curr price:" + Misc.trim(csv.get(0, CSV.ADJ_CLOSE)) + ",curr PS:" + Misc.trim(currPs) +",2nd min Low Ps:" + Misc.trim(minLowPs) + ",2nd min High Ps:" + Misc.trim(minHighPs)) ;
//					System.out.println("                         # of Share     PS=1.0   PRICE/L  PRICE/H  PS/L     PS/H") ;
//					System.out.println(msg) ;
					content.append("<div stock='"+code+"'><div class='title'>"+code+" "+d.name
							+",PE:"+d.pe
//							+",PB:"+d.pb
							+",Int:"+d.div+",Cap:"+d.marketCap+"億"
							+ "<br>curr price:" + Misc.trim(csv.get(0, CSV.ADJ_CLOSE)) + ",curr PB:" + Misc.trim(currPB) 
							+"<table border='1'>"
							+ "<tr><th>price</th><th>Book Value</th><th>min PB(L)</th><th>2nd PB(L)</th><th>min PB/H</th><th>2nd PB(H)</th>"
							+ "<tr>" 
							+ "<td>" + Misc.trim(csv.get(0, CSV.ADJ_CLOSE)) + "</td>"
							+ "<td>" + Misc.trim(currBookValue) + "</td>"
							+ "<td>" + Misc.trim(lowPsList.get(0) * currBookValue) +"</td>"
							+ "<td>" + Misc.trim(lowPsList.get(1) * currBookValue) + "</td>"
							+ "<td>" + Misc.trim(highPsList.get(0) * currBookValue) + "</td>"
							+ "<td>" + Misc.trim(highPsList.get(1) * currBookValue) + "</td>"
							+ "</tr>"
							+ "<tr>" 
							+ "<td>" +  "</td>"
							+ "<td>" + "</td>"
							+ "<td>" + Misc.trim((currPB-lowPsList.get(0))/lowPsList.get(0) *100,2)  +"%" + "</td>"
							+ "<td>" + Misc.trim((currPB-secondMinLowPs)/secondMinLowPs * 100,2) +"%" + "</td>"
							+ "<td>" + Misc.trim((currPB - highPsList.get(0) )/highPsList.get(0) * 100,2) +"%" + "</td>"
							+ "<td>" + Misc.trim((currPB - secondMinHighPs )/secondMinHighPs* 100,2)  +"%" + "</td>"
							+ "</tr>"
							+ "</table>"
							+"</div></div>\r\n") ;
					content.append("<table border='1'>") ;
					content.append("<tr><th>Start</th><th>End</th><th># of share</th><th>Book Value</th><th>PRICE/L</th><th>PRICE/H</th><th>PB/L</th><th>PB/H</th></tr>") ;
					content.append(msg) ;
					content.append("</table><br><br>") ;
					text.append("<tr><td>" + code + "</td><td>" + h.name + "</td><td>" + Misc.trim(csv.get(0, CSV.ADJ_CLOSE)) 
							+ "</td><td>" + Misc.trim(currPB) 
							+"</td>"
							+ "<td>" + Misc.trim((currPB-lowPsList.get(0))/lowPsList.get(0) *100,2)  +"%" + "</td>"
							+ "<td>" + Misc.trim((currPB-secondMinLowPs)/secondMinLowPs * 100,2) +"%" + "</td>"
							+ "<td>" + Misc.trim((currPB - highPsList.get(0) )/highPsList.get(0) * 100,2) +"%" + "</td>"
							+ "<td>" + Misc.trim((currPB - secondMinHighPs )/secondMinHighPs* 100,2)  +"%" + "</td>"
							+ "</tr>")  ;
				}
			}catch(Exception ex) {
				ex.printStackTrace(); 
			}
		}
		template = template.replace("#HEADER#", header ) ;
		template = template.replace("#TITLE#", title) ;
		template = template.replace("#CONTENT#", content) ;
		
		text.append("</table>") ;
		
		File outputDir = new File("output") ;
		outputDir.mkdirs() ;
		
		FileOutputStream out = new FileOutputStream(new File(outputDir,  "choose-by-bp" + ".html")) ;
		IOUtils.write(template, out);
		out.flush();
		out.close() ;

		FileOutputStream textout = new FileOutputStream(new File(outputDir,  "choose-by-bp" + ".txt")) ;
		IOUtils.write(text, textout);
		textout.flush();
		textout.close() ;

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

				if(e.dataset.getDouble("每股帳面資產淨值", 0) < e.dataset.getDouble("每股帳面資產淨值", 4) * 1.2) {
					// it need to has at least slight increase within 5 year
					continue;
				}
				
//				double earn = e.dataset.getDouble("股東應佔溢利", 0) - e.dataset.getDouble("投資物業公平值變動及減值", 0) - e.dataset.getDouble("其他項目公平值變動及減值", 0);
//				if(e.lastYearIsHalf) {
//					earn = earn * 2 ;
//				}
//				earn = earn * e.currRate ;
//				if(earn < 800000000) continue code;

				
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
