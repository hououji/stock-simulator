package info.hououji.sim;
import info.hououji.sim.MarketCapExcel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;


public abstract class Detector {
	
	Helper h = new Helper(); 
	MarketCapExcel excel = new MarketCapExcel() ; 
	
	public abstract boolean detect(CSV csv, int backDays) ;
	
	public abstract String  getName() ;
	
	public abstract String getDesc() ;
	
	public int makeHtml() throws IOException{
		File dir = Downloader.getRecentDirectory() ;
		
//		run(new File(dir,"0015.csv")) ;
//		VolumeSpecialDetector d = new VolumeSpecialDetector() ;
		
		File[] files = dir.listFiles() ;
		Arrays.sort(files);
		List<String> codes = new ArrayList<String>() ;
		List<Integer> keepDays = new ArrayList<Integer>();
		for(File file : files) {
			CSV csv = new CSV(file) ;
			if(detect(csv,0)){
				String code = Downloader.getName(file) ;
				codes.add(code) ;
				
				// get the keep day
				int i;
				for(i=1; i<100; i++) {
					if(detect(csv,i)) break;
				}
				keepDays.add(i) ;
			}
		}
		
		// make html
		String template = Misc.getFile("list-template.html") ;
		String header = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " - " + this.getName()  ;
		String title = this.getName() ;
		StringBuffer content = new StringBuffer() ;
		for(int i=0; i<codes.size(); i++ ) {
			try{
				String code = codes.get(i) ;
				int keepDay = keepDays.get(i) ;
				Detail aa = new Detail(code) ;
				content.append("<div stock='"+code+"'><div class='title'>"+code+aa.getName()
						+",PE:"+aa.getPe() +",PB:"+aa.getPb()
						+",Int:"+aa.getDiv()+",Cap:"+aa.getMarketCap()+"億, "+keepDay+" days</div></div>\r\n") ;
			}catch(Exception ex){
				Log.log(ex.toString());
			}
		}
		template = template.replace("#HEADER#", header + this.getDesc()) ;
		template = template.replace("#TITLE#", title) ;
		template = template.replace("#CONTENT#", content) ;
		
		File outputDir = new File("output") ;
		outputDir.mkdirs() ;
		
		File outFile = new File(outputDir,  this.getName() + ".html") ;
		FileOutputStream out = new FileOutputStream(outFile) ;
		IOUtils.write(template, out);
		out.flush();
		out.close() ;
		
		System.out.println("output:" + outFile.getAbsolutePath()) ;
		
		return codes.size() ;
	}
	
	protected double sd(List<Double> list) {
		StandardDeviation sd = new StandardDeviation();
		double d[] = new double[list.size()] ;
		for(int i=0; i<list.size();i++) {
			d[i] = list.get(i) ;
		}
		return sd.evaluate(d) ;
	}
	
	protected boolean isMarketCapGreat(String code, int i) {
		return h.isMarketCapGreat(code,i) ;
	}
	
	protected Row getRow(String code) {
		return excel.getRow(code) ;
	}
}
