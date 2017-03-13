package info.hououji.sim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import info.hououji.sim.MarketCapExcel.Row;

public class EtnetRemainValue {
	
	
	public static void main(String args[]) throws Exception{
		
		Set<String> oldCode = new LinkedHashSet<String>() ;
		BufferedReader in = new BufferedReader(new FileReader("old-code.txt")) ;
		while(true) {
			String line = in.readLine() ;
			if(line == null) break;
			oldCode.add(line.substring(0, 4)) ;
		}
		in.close();
		
		File dir = Downloader.getRecentDirectory() ;
		File[] files = dir.listFiles() ;
		Arrays.sort(files);
		List<String> results = new ArrayList<String>() ;
		MarketCapExcel excels = new MarketCapExcel() ;
		for(File file : files) {
			String code = file.getName().substring(0,4) ;
			try{
				CSV csv = new CSV(file) ;
				Row excel = excels.getRow(code) ;
				
				EtnetHistIncome e = new EtnetHistIncome(code) ;
				
				System.out.println(Misc.formatMoney(e.dataset.getDouble("營業額", 0))) ;
				double sale = e.dataset.getDouble("營業額", 0) ;
				if(e.lastYearIsHalf) {
					sale = sale * 2 ;
				}
				sale = sale * e.currRate ;
				
				if(sale < 800000000) continue ;
//				System.out.println("adj sales:" + Misc.formatMoney(sale)) ;
				
				if(e.dataset.getDouble("分佔聯營公司及共同控制公司 業績", 0) > e.dataset.getDouble("股東應佔溢利", 0) * 0.15 ) continue;
				if(e.dataset.getDouble("投資物業公平值變動及減值", 0) > e.dataset.getDouble("股東應佔溢利", 0) * 0.15 ) continue;
				if(e.dataset.getDouble("其他項目公平值變動及減值", 0) > e.dataset.getDouble("股東應佔溢利", 0) * 0.15 ) continue;
				
				double earn = e.dataset.getDouble("股東應佔溢利", 0) * e.currRate ;
				double shareEarn = e.dataset.getDouble("每股盈利 (仙)", 0) / 100  * e.currRate;
				long share = (long)(earn / shareEarn) ;
//				System.out.println("share:" + Misc.formatMoney(share)) ;
				
				double earnRate = e.dataset.getDouble("股東應佔溢利", 0) / e.dataset.getDouble("營業額", 0) ;
//				System.out.println("earn ratio:" + earnRate);
				
				double ps = sale / share ;
				
				double remainRate = 0;
				if(earnRate <= 0.02) {
					remainRate = 0.1 ;
				}else if (earnRate > 0.02 && earnRate <=0.06) {
					remainRate = 0.2 ;
				}else if (earnRate > 0.06 && earnRate <=0.07) {
					remainRate = 0.35 ;
				}else if (earnRate > 0.07 ) {
					remainRate = 0.5 ;
				}
				
				double remain = ps * remainRate ;
				String week52 = csv.min(0, 250, CSV.ADJ_CLOSE) + " ~ " + csv.max(0, 250, CSV.ADJ_CLOSE) ;
				
				double min = csv.min(0, 1, CSV.ADJ_CLOSE) ;
				EtnetHistRatio ehr = new EtnetHistRatio(code) ;
				if( min < remain * 1.1 ){
					String s = "";
					if( ! oldCode.contains(code)) s = s + "(NEW:"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+")" ;
					s = s + "" + code + Misc.pad(e.name, 10) ;
					s = s +  ";" + min ; 
					s = s +  ";remain(ps:"+remainRate+"):" + Misc.trim(remain,3) ;
					s = s + ";=googlefinance(\"HKG:"+code+"\",\"price\")" ;
					
					s = s + ";52周:"+ week52 + " PE:" + excel.pe + " PB:" + excel.pb + " 息率:" + excel.div + " 市值:" + excel.cap ;
					
					s = s + ";ps("+((long)(sale/1000000))+"M/"+((long)(share/1000000))+"M):" + Misc.trim(ps,3);
					s = s + ";income rate("+(long)(e.dataset.getDouble("股東應佔溢利", 0)/1000000) + "M/"+(long)(e.dataset.getDouble("營業額", 0)/1000000)+"M) :" + Misc.trim(earnRate * 100, 1) + "%" ;
					s = s + ";" +ehr.dataset.getRow("純利率 (%)").toString() ;
					s = s + ";" +ehr.dataset.getRow("股東資金回報率 (%)").toString();
					
					results.add(s) ;
					oldCode.remove(code) ;
				}
				
//				System.out.println("PS " + remainRate + ",remain:" + remain) ;
				
			}catch(Exception ex) {
				System.out.println("exception code : " + code) ;
				ex.printStackTrace();
			}
		}
		for(String s: results) {
			System.out.println(s) ;
		}
		System.out.println("\n\nQuit Code") ;
		for(String s :oldCode) {
			System.out.println(s) ;
		}
	}

}
