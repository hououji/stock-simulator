import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;


public class CSV {

	public static int VOL = 4 ;
	public static int ADJ_CLOSE = 5 ;
	public static int VOL_PRICE = 100 ;
	
	List<String> dates = new ArrayList<String>() ;
	List<double[]> dataList = new ArrayList<double[]>() ;
	File file ;
	String name = "" ;
	String code = "" ;
	int baseDay = 0 ;
	
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd") ;
	
	public static  double to2dp(double d) {
		long l = (long)(d * 100) ;
		return l / 100.0;
	}
	
	public void setBaseDay(int bd) {
		baseDay = bd;
	}
	
	public double avg(int start, int len, int type) {
		double sum = 0 ;
		
		for(int i=start; i<start+len;i ++) {
			sum += get(i,type) ;
		}
		
		return to2dp(sum / len) ;
	}

	public double max(int start, int len, int type) {
		double max = 0 ;
		
		for(int i=start; i<start+len;i ++) {
			double d = get(i,type) ;
			if(d > max) max = d ;
		}
		
		return to2dp(max) ;
	}
	public double min(int start, int len, int type) {
		double min = Double.MAX_VALUE ;
		
		for(int i=start; i<start+len;i ++) {
			double d = get(i,type) ;
			if(d < min) min = d ;
		}
		
		return to2dp(min) ;
	}

	
	public String getName() {
		return name ;
	}
	public String getCode() {
		return code;
	}
	
	public int getLen() {
		return dates.size() ;
	}
	public String getDate(int num) {
		return dates.get(num + baseDay) ;
	}
	public double get(int num, int type) {
		if(type < 100) {
			return  dataList.get(num + baseDay)[type] ;
		}
		if(type == VOL_PRICE) {
			return get(num,VOL) * get(num,ADJ_CLOSE) ;
		}
		throw new RuntimeException("NO such CSV field type : " + type) ;
	}
	public File getFile() {
		return this.file ;
	}
	public CSV(File _file) 
	{
		try{
			file = _file;
			name = file.getName() ;
			code = name.substring(0, 4) ;
			BufferedReader r = new BufferedReader(new FileReader(file)) ;
			r.readLine() ; // skip this first line, it is a lable
			while(true) {
				String line = r.readLine() ;
				if(line == null) break; 
				double [] data = new double[6] ;
				int idx = 0 ;
				StringTokenizer stok = new StringTokenizer(line,",") ;
				dates.add(stok.nextToken()) ;
				while(stok.hasMoreElements()) {
					String token = stok.nextToken() ;
					data[idx] = Double.parseDouble(token) ;
					idx ++ ;
				}
				dataList.add(data); 
			}
		}catch(Exception ex){
			throw new RuntimeException(ex) ;
		}
	}
	
	public static void main(String args[]) throws Exception {
		CSV csv = new CSV( new File(Downloader.getRecentDirectory(), "0002.csv")) ;
		System.out.println(csv.getFile().getAbsolutePath());
		for(int i=0; i<10; i++) {
			System.out.println("5 avg ("+i+") : " + csv.avg(i, 5, VOL)) ;
		}
		for(int i=0; i<10; i++) {
			System.out.println(csv.getDate(i) + " " + csv.get(i, VOL_PRICE)+ " " + csv.get(i, VOL)+ " " + to2dp(csv.get(i,ADJ_CLOSE)) ) ;
		}
		
		for(int i=0; i<5; i++){
			csv.setBaseDay(i);
			for(int j=0; j<5; j++) {
				System.out.println( "baseDay"+i+",num:"+j+":\t" + csv.get(j, CSV.ADJ_CLOSE) ) ;
			}
		}

		for(int i=0; i<5; i++){
			csv.setBaseDay(i);
			for(int j=0; j<5; j++) {
				System.out.println( "[AVG ]baseDay"+i+",num:"+j+":\t" + csv.avg(j, 5, CSV.ADJ_CLOSE) ) ;
			}
		}

		
	}
	
}
