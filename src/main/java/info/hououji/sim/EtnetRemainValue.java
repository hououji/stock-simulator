package info.hououji.sim;

import info.hououji.sim.util.CachedDownload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class EtnetRemainValue {
	
	
	public static void main(String args[]) {
		
		File dir = Downloader.getRecentDirectory() ;
		File[] files = dir.listFiles() ;
		Arrays.sort(files);
		List<String> codes = new ArrayList<String>() ;
		for(File file : files) {
			String code = file.getName().substring(0,4) ;
			try{
				
				EtnetHistIncome e = new EtnetHistIncome(code) ;
				
				System.out.println(Misc.formatMoney(e.dataset.getDouble("營業額", 0))) ;
				double sale = e.dataset.getDouble("營業額", 0) ;
				if(e.lastYearIsHalf) {
					sale = sale * 2 ;
				}
				sale = sale * e.currRate ;
				
				if(sale < 800000000) continue ;
//				System.out.println("adj sales:" + Misc.formatMoney(sale)) ;
				
				double earn = e.dataset.getDouble("股東應佔溢利", 0) * e.currRate ;
				double shareEarn = e.dataset.getDouble("每股盈利 (仙)", 0) / 100  * e.currRate;
				int share = (int)(earn / shareEarn) ;
//				System.out.println("share:" + Misc.formatMoney(share)) ;
				
				double earnRate = earn / sale ;
//				System.out.println("earn ratio:" + earnRate);
				
				double ps = sale / share ;
				
				double remainRate = 0;
				if(earnRate <= 0.02) {
					remainRate = 0.1 ;
				}else if (earnRate > 0.02 && earnRate <=0.06) {
					remainRate = 0.2 ;
				}else if (earnRate > 0.06 && earnRate <=0.07) {
					remainRate = 0.35 ;
				}else if (earnRate > 0.07 && earnRate <=0.1) {
					remainRate = 0.5 ;
				}
				
				double remain = ps * remainRate ;
				
//				System.out.println("PS " + remainRate + ",remain:" + remain) ;
				
			}catch(Exception ex) {
				System.out.println("exception code : " + code) ;
				ex.printStackTrace();
			}
		}
	}

}
