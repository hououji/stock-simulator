import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;


public abstract class Detector {
	
	Map<String, Double> marketCap = null ;
	
	public abstract boolean detect(File file) ;
	
	public abstract String  getName() ;
	
	public abstract String getDesc() ;
	
	public void makeHtml() throws IOException{
		File dir = Downloader.getRecentDirectory() ;
		
//		run(new File(dir,"0015.csv")) ;
//		VolumeSpecialDetector d = new VolumeSpecialDetector() ;
		
		File[] files = dir.listFiles() ;
		Arrays.sort(files);
		List<String> codes = new ArrayList<String>() ;
		for(File file : files) {
			if(detect(file)){
				String code = Downloader.getName(file) ;
				codes.add(code) ;
			}
		}
		
		// make html
		String template = Misc.getFile("list-template.html") ;
		String header = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " - " + this.getName()  ;
		String title = this.getName() ;
		StringBuffer content = new StringBuffer() ;
		for(String code : codes) {
			Aastock aa = new Aastock(code) ;
			content.append("<div stock='"+code+"'><div class='title'>"+code+aa.getName()+",PE:"+aa.getPe()
					+",Int:"+aa.getInt()+",Cap:"+aa.getMarketCap()+"億</div></div>\r\n") ;
		}
		template = template.replace("#HEADER#", header + this.getDesc()) ;
		template = template.replace("#TITLE#", title) ;
		template = template.replace("#CONTENT#", content) ;
		
		File outputDir = new File("output") ;
		outputDir.mkdirs() ;
		
		FileOutputStream out = new FileOutputStream(new File(outputDir,  this.getName() + ".html")) ;
		IOUtils.write(template, out);
		out.flush();
		out.close() ;
	}
	
	private synchronized void initMarketCap() {
		marketCap = new HashMap<String,Double>() ;
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("output/market_cap.csv"), "utf8") );
			while(true) {
				try{
					String line = in.readLine() ;
					if(line == null) break;
					StringTokenizer st = new StringTokenizer(line, ",") ;
					String code = st.nextToken() ;
					st.nextToken() ;
					Double cap = Double.parseDouble(st.nextToken()) ;
					marketCap.put(code, cap) ;
				}catch(Exception ex){
				}
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	protected boolean isMarketCapGreat(String code, int i) {
		if(marketCap == null) initMarketCap() ;
		if(marketCap.get(code) == null || marketCap.get(code) < i) return false;
		return true;
	}
	protected double sd(List<Double> list) {
		StandardDeviation sd = new StandardDeviation();
		double d[] = new double[list.size()] ;
		for(int i=0; i<list.size();i++) {
			d[i] = list.get(i) ;
		}
		return sd.evaluate(d) ;
	}
}
