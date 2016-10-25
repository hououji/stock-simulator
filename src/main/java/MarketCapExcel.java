import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MarketCapExcel {
	public static void main(String args[]) throws Exception {
//		Aastock aa = new Aastock("0002") ;
//		System.out.println(aa.toString());
		
		// Real
		File dir = Downloader.getRecentDirectory() ;
		System.out.println(dir.getAbsolutePath()) ;
		File[] files = dir.listFiles() ;
		Arrays.sort(files);
		List<String> codes = new ArrayList<String>() ;
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("output/market_cap.csv"),"utf8") ;
		PrintWriter out = new PrintWriter(osw); 
		out.print("\uFEFF"); // BOM, make Excel auto use UTF8
		for(File file : files) {
			try{
				String code = Downloader.getName(file) ;
				Aastock a = new Aastock(code) ;
				out.println(a.toString()) ;
				System.out.println(a.toString());
				out.flush(); 
				Thread.sleep(1000);
			}catch(Exception ex){
				ex.printStackTrace(); 
			}
		}
		out.flush(); 
		out.close();
		osw.close();
	

	}

}
