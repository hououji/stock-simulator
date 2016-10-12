import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;


public abstract class Detector {
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
			content.append("<div stock='"+code+"'></div>\r\n") ;
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
}
