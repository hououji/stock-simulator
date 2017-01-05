package info.hououji.sim;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;


public class LargeWaveFinder {

	static int period = 250 ;
	
	public static boolean check(CSV csv) {
		if(csv.getLen() < 5 * 250) return false;
		
		csv.setBaseDay(0);
		double gmin = csv.max(0, 5*250, CSV.ADJ_CLOSE) ;
		double gmax = csv.max(0, 5*250, CSV.ADJ_CLOSE) ;
		
		for(int i = 5*250; i>0; i--) {
			csv.setBaseDay(i);
			double max = csv.max(0, period, CSV.ADJ_CLOSE) ;
			double min = csv.min(0, period, CSV.ADJ_CLOSE) ;
			
			if(max < min * 2) return false;
			
			if(max < gmax * 0.8) return false;
			if(min > gmin * 1.2) return false;
		}
		
		return true;
	}
	
	
	public static void main(String args[]) throws Exception {
		
		File dir = Downloader.getRecentDirectory() ;
		File[] files = dir.listFiles() ;
		Arrays.sort(files);
		List<String> codes = new ArrayList<String>() ;
		for(File file : files) {
			try{
				CSV csv = new CSV(file) ;
				if(check(csv)){
					System.out.println("code:" + csv.getCode());
					String code = Downloader.getName(file) ;
					codes.add(code) ;
				}
			}catch(Exception ex) {
			}
		}
		
		String template = Misc.getFile("list-template.html") ;
		String header = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " - " + "Large Wave"  ;
		String title = "Large Wave" ;
		StringBuffer content = new StringBuffer() ;
		
		for(int i=0; i<codes.size(); i++ ) {
			try{
				String code = codes.get(i) ;
				Detail aa = new Detail(code) ;
				content.append("<div stock='"+code+"'><div class='title'>"+code+aa.getName()+",PE:"+aa.getPe()
						+",Int:"+aa.getDiv()+",Cap:"+aa.getMarketCap()+"億</div></div>\r\n") ;
			}catch(Exception ex){}
		}
		template = template.replace("#HEADER#", header + ", period:" + period) ;
		template = template.replace("#TITLE#", title) ;
		template = template.replace("#CONTENT#", content) ;
		
		File outputDir = new File("output") ;
		outputDir.mkdirs() ;
		
		FileOutputStream out = new FileOutputStream(new File(outputDir,  "large-wave" + ".html")) ;
		IOUtils.write(template, out);
		out.flush();
		out.close() ;

	}
}
