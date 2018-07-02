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

import org.apache.commons.lang3.StringUtils;

public class EtnetRemainValue {
	
	
	public static void main(String args[]) throws Exception{
		
		Set<String> oldCode = new LinkedHashSet<String>() ;
		Set<String> errorCode = new LinkedHashSet<String>() ;
		BufferedReader in = new BufferedReader(new FileReader("old-code.txt")) ;
		while(true) {
			String line = in.readLine() ;
			if(line == null) break;
			if(line.length() <4) continue;
			if(line.indexOf(")") == -1) continue;
			String code = line.substring(line.indexOf(")") + 1) ;
			if(code.length() < 4) continue;
			code = code.substring(0, 4) ;
			if(!StringUtils.isNumeric(code)) continue;
			//if(line.startsWith("\"")) continue;
			System.out.println("previous code : " + code) ;
			oldCode.add(code) ;
		}
		in.close();
		
		
		File dir = Downloader.getRecentDirectory() ;
		File[] files = dir.listFiles() ;
		Arrays.sort(files);
		List<String> results = new ArrayList<String>() ;
//		MarketCapExcel excels = new MarketCapExcel() ;
		for(File file : files) {
			String code = file.getName().substring(0,4) ;
			String name = "" ;
			try{
				CSV csv = new CSV(file) ;
//				Row excel = excels.getRow(code) ;
				
				EtnetHistIncome e = new EtnetHistIncome(code) ;
				EtnetHistCommon common = new EtnetHistCommon(code) ;
				
				name = e.name ;
				
				Date marketDate = new Date() ;
				boolean isMoreThan3years = true ;
				System.out.println("code:" + code) ;
				try{
					String dateStr = common.dataset.getStr("上市日期") ;
					marketDate = new SimpleDateFormat("dd/MM/yyyy").parse(dateStr) ;
					if( (new Date().getTime() - marketDate.getTime()) / (1000*60*60*24*356) < 3  ) isMoreThan3years = false; 
				}catch(Exception ex){
					//ex.printStackTrace();
				}
				
				System.out.println(Misc.formatMoney(e.dataset.getDouble("營業額", 0))) ;
				double sale = e.dataset.getDouble("營業額", 0) ;
				if(e.lastYearIsHalf) {
					sale = sale * 2 ;
				}
				sale = sale * e.currRate ;
				
				if(sale < 800000000) continue ;
//				System.out.println("adj sales:" + Misc.formatMoney(sale)) ;
				
//				if(e.dataset.getDouble("分佔聯營公司及共同控制公司", 0) > e.dataset.getDouble("股東應佔溢利", 0) * 0.15 ) {
//					continue;
//				}
//				if(e.dataset.getDouble("投資物業公平值變動及減值", 0) > e.dataset.getDouble("股東應佔溢利", 0) * 0.15 ) {
//					continue;
//				}
//				if(e.dataset.getDouble("其他項目公平值變動及減值", 0) > e.dataset.getDouble("股東應佔溢利", 0) * 0.15 ) {
//					continue;
//				}
				
/*
				double earn = e.dataset.getDouble("股東應佔溢利", 0) * e.currRate ;
				earn = earn - e.dataset.getDouble("分佔聯營公司及共同控制公司", 0)
							- e.dataset.getDouble("投資物業公平值變動及減值", 0)
							- e.dataset.getDouble("其他項目公平值變動及減值", 0);
				if(e.lastYearIsHalf) {
					earn = earn * 2 ;
				}
	*/			
//				double shareEarn = e.dataset.getDouble("每股盈利 (仙)", 0) / 100  * e.currRate;
				long share = (long)(e.dataset.getDouble("股東應佔溢利", 0) / (e.dataset.getDouble("每股盈利 (仙)", 0) / 100) ) ;
				System.out.println("share:" + Misc.formatMoney(share)) ;
				double ps = sale / share ;
				System.out.println("ps:" + ps) ;
				
				double earnRate = getEarnRate(e, 0) ;
				System.out.println("earn ratio:" + earnRate);
				
				double remainRate = 0;
				String rrStr = "" ;
				if(isMoreThan3years) {
					// full setif
					
					double earnRate0 = getEarnRate(e, 0) ;
					double earnRate1 = getEarnRate(e, 1) ;
					double earnRate2 = getEarnRate(e, 2) ;
					double r0 = getSimpleRemainRate(earnRate0) ;
					double r1 = getSimpleRemainRate(earnRate1) ;
					double r2 = getSimpleRemainRate(earnRate2) ;
					if(r0 == 0.5) {
						remainRate = 0.5 ;
					}else if(r0 == 0.2 && r1 == 0.5 ){
						remainRate = 0.35 ;
					}else if(r0 == 0.2 && r1 == 0.2 ){
						remainRate = 0.2 ;
					}else if(r0 == 0.2 && r1 == 0.1 ){
						if(r2 == 0.1) {
							remainRate = 0.15 ;
						}else{
							remainRate = 0.2 ;
						}
					}else if(r0 == 0.1 && r1 == 0.5 ){
						remainRate = 0.2 ;
					}else if(r0 == 0.1 && r1 == 0.2 ){
						remainRate = 0.15 ;
					}else if(r0 == 0.1 && r1 == 0.1 ){
						if(r2 == 0.1) {
							remainRate = 0.1 ;
						}else{
							remainRate = 0.15 ;
						}
					}
					rrStr = "<"+r0+","+r1+","+r2+">=" + remainRate ; 
				}else {
					// simple
					if(earnRate <= 0.02) {
						remainRate = 0.1 ;
					}else if (earnRate > 0.02 && earnRate <=0.06) {
						remainRate = 0.2 ;
					}else if (earnRate > 0.06 && earnRate <=0.07) {
						remainRate = 0.35 ;
					}else if (earnRate > 0.07 ) {
						remainRate = 0.5 ;
					}
					rrStr = "" + remainRate ;
				}
				
				if(remainRate == 0) System.out.println("Remain Rate = 0, code:" + code) ; 
				
				double remain = ps * remainRate ;
				String week52 = csv.min(0, 250, CSV.ADJ_CLOSE) + " ~ " + csv.max(0, 250, CSV.ADJ_CLOSE) ;
				
				double nav = e.dataset.getDouble("每股帳面資產淨值 ($)", 0)  * e.currRate;
				double ironTri = (nav * 0.5 + remain) /2 ;  
				
				double min = csv.min(0, 1, CSV.ADJ_CLOSE) ;
				EtnetHistRatio ehr = new EtnetHistRatio(code) ;
				Detail d = new Detail(code) ;
				if(d.stockSuspend) continue;
				if( min <  Math.max(remain, ironTri) * 1.1 ){
					String s = "";
					if( ! oldCode.contains(code)) s = s + "(NEW:"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+")" ;
					s = s + "" + code + Misc.pad(e.name, 10) + " " + e.thisyear ;
					s = s +  ";" + min ; 
					s = s +  ";remain(ps:"+rrStr+"):" + Misc.trim(remain,3)+",鐵角:" + Misc.trim(ironTri,3) ;
					s = s + ";=googlefinance(\"HKG:"+code+"\",\"price\")" ;
					
					s = s + ";52周:"+ week52 + " PE:" + Misc.trim(d.pe,2) + " PB:" + Misc.trim(d.pb,2) + " 息率:" + Misc.trim(d.div,2) + " 市值:" + d.marketCap ;
					
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
				errorCode.add(code + " " + name) ;
			}
		}
		System.out.println("error count:" + errorCode.size()); 
		for(String s: errorCode) {
			System.out.println(s) ;
		}
		System.out.println("result count:" + results.size()); 
		for(String s: results) {
			System.out.println(s) ;
		}
		System.out.println("\n\nQuit Code") ;
		for(String s :oldCode) {
			System.out.println(s) ;
		}
	}

	private static double getEarnRate(EtnetHistIncome e,int idx) {
		double earn = e.dataset.getDouble("股東應佔溢利", idx) ;
		earn = earn - e.dataset.getDouble("分佔聯營公司及共同控制公司", idx)
					- e.dataset.getDouble("投資物業公平值變動及減值", idx)
					- e.dataset.getDouble("其他項目公平值變動及減值", idx);
		double earnRate = earn / e.dataset.getDouble("營業額", idx) ;

		return earnRate ; 
	}
	
	private static double getSimpleRemainRate(double earnRate) {
		double remainRate = 0 ;
		if(earnRate <= 0.02) {
			remainRate = 0.1 ;
		}else if (earnRate > 0.02 && earnRate <=0.07) {
			remainRate = 0.2 ;
//		}else if (earnRate > 0.06 && earnRate <=0.07) {
//			remainRate = 0.35 ;
		}else if (earnRate > 0.07 ) {
			remainRate = 0.5 ;
		}
		return remainRate;
	}

}
