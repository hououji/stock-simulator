package info.hououji.sim;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ChooseByValue {

	public static void main(String args[]) throws Exception {
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
				for(int i=0; i<roe.length; i++) {
					roe[i] = r.dataset.getDouble("股東資金回報率", i) ;
					if(roe[i] < 15) continue code ;
				}
				double sale = e.dataset.getDouble("營業額", 0) ;
				if(e.lastYearIsHalf) {
					sale = sale * 2 ;
				}
				sale = sale * e.currRate ;
				if(sale < 800000000) continue code;

				double earn = e.dataset.getDouble("股東應佔溢利", 0) ;
				if(e.lastYearIsHalf) {
					earn = earn * 2 ;
				}
				earn = earn * e.currRate ;
				if(earn < 800000000) continue code;

				
				if(e.dataset.getDouble("投資物業公平值變動及減值", 0) > e.dataset.getDouble("股東應佔溢利", 0) * 0.15 ) continue;
				if(e.dataset.getDouble("其他項目公平值變動及減值", 0) > e.dataset.getDouble("股東應佔溢利", 0) * 0.15 ) continue;

				
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
				
			}
		}	

		System.out.println("=== Result === : " + codes.size()) ;
		
		for(String s : results) {
			System.out.println(s) ;
		}
	}
}
