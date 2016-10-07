import info.hououji.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;


public class KeepUpDetector {

	static boolean debug = false;

	
	public static boolean run(File file) {
		try{
			CSV csv = new CSV(file) ;
			
			// ignore if too small
			if(csv.get(0, CSV.VOL_PRICE) < 100000000) return false;
			if(csv.getLen() < 250) return false;

			if(debug) Log.log("file:" + file.getAbsolutePath());

			for(int i = 40; i>=0; i--){
				double avg10 = csv.avg(i, 10, CSV.ADJ_CLOSE) ;
				double avg10Yesterday = csv.avg(i + 1, 10, CSV.ADJ_CLOSE) ;
				
				if(avg10Yesterday > avg10) {
					return false; // fail
				}
			}
			
			// pass
			Log.log(csv.getName() + ", adf cls:" + csv.to2dp(csv.get(0, CSV.ADJ_CLOSE)) +", ex:" + csv.to2dp(csv.get(0, CSV.VOL_PRICE)));
			
			return true;
		}catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
		
	}
	
	public static void main(String args[]) throws Exception {
	
		File dir = Downloader.getRecentDirectory() ;
		
//		run(new File(dir,"0015.csv")) ;
		File[] files = dir.listFiles() ;
		Arrays.sort(files);
		List<String> codes = new ArrayList<String>() ;
		for(File file : files) {
			if(run(file)){
				String code = Downloader.getName(file) ;
				codes.add(code) ;
			}
		}
		
		// make html
		String template = Misc.getFile("list-template.html") ;
		String header = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " - Keep Up Detector"  ;
		String title = "Keep Up Detector" ;
		StringBuffer content = new StringBuffer() ;
		for(String code : codes) {
			content.append("<div stock='"+code+"'></div>\r\n") ;
		}
		template = template.replace("#HEADER#", header) ;
		template = template.replace("#TITLE#", title) ;
		template = template.replace("#CONTENT#", content) ;
		
		File outputDir = new File("output") ;
		outputDir.mkdirs() ;
		
		FileOutputStream out = new FileOutputStream(new File(outputDir, "keep-up-detector.html")) ;
		IOUtils.write(template, out);
		out.flush();
		out.close() ;
	}
}
