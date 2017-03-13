package info.hououji.sim;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import info.hououji.sim.MarketCapExcel.Row;

public class ChooseByValue {

	public static void main(String args[]) throws Exception {
		File dir = Downloader.getRecentDirectory() ;
		File[] files = dir.listFiles() ;
		Arrays.sort(files);
		List<String> codes = new ArrayList<String>() ;
		List<String> results = new ArrayList<String>() ;
		
		
		code: for(File file : files) {
			String code = file.getName().substring(0,4) ;
			//System.out.println(code) ;
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
				
				
				
				codes.add(code) ;
				
				
			}catch(Exception ex) {
				
			}
		}	

		System.out.println("=== Result === : " + codes.size()) ;
		
		for(String code : codes) {
			EtnetHistIncome e = new EtnetHistIncome(code) ;
			String s = code + e.name ;
			try{
				Detail d= new Detail(code) ;
				s = s + "," + d.pe;
				s = s + "," + d.pb ;
				s = s + "," + d.div ;
			}catch(Exception ex) {
				ex.printStackTrace();
			}

			results.add(s) ;
		}
		
		System.out.println("code,PE,PB,div") ;
		for(String s : results) {
			System.out.println(s) ;
		}
	}
}
