package info.hououji.sim;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HsiDL {
	public static void main(String args[]) throws Exception {
		CSV csv = new CSV(new File("HSI.csv")) ;
		PrintStream ps = new PrintStream( new FileOutputStream("hsi.json")) ;
		ObjectMapper mapper = new ObjectMapper();
		int interval = 10 ;
		double rate = 0.05 ; // inside <interval> days, change <rate> %
		int count = 0 ;
		int hit = 0 ;
		for(int i=interval; i<1000; i++) {
//			System.out.println(csv.getDate(i) + " "+csv.get(i, CSV.ADJ_CLOSE)) ;
//			if(1==1) continue;
			count ++ ;
			double min = csv.min(i-interval, interval, CSV.ADJ_CLOSE) ;
			double max = csv.max(i-interval, interval, CSV.ADJ_CLOSE) ;
			double today = csv.get(i, CSV.ADJ_CLOSE) ;
			
			int down = 0 ;
			int up = 0 ;
			if( (today - min) / today > rate ) down = 1;
			if( (max - today) / today > rate ) up = 1;
			if(down ==1 || up == 1) hit ++ ;
			List<Integer> result = new ArrayList<Integer>() ;
			result.add(down) ;
			result.add(up) ;
			
			List<Double> prices = new ArrayList<Double>() ;
			for(int j=0; j<25; j++) {
				prices.add( csv.get(i+j, CSV.ADJ_CLOSE)) ;
			}
			
			List record = new ArrayList() ;
			record.add(prices) ;
			record.add(result) ;
			String out = mapper.writeValueAsString(record) ;
			
//			System.out.println(csv.getDate(i) + ",min:" + min + ",max:" + max + " " + out);
			System.out.println(out);
			ps.println(out) ;
			
		}
		ps.flush();
		ps.close();
		System.out.println("count:" + count + ",hit:" + hit + ",ratio:" + (double)hit / count);
	}
}
