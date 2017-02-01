package info.hououji.sim;
import java.io.InputStream;
import java.text.DecimalFormat;

import org.apache.commons.io.IOUtils;


public class Misc {

	public static String getFile(String name) {
		try{
			InputStream is = Misc.class.getClassLoader().getResourceAsStream(name) ;
			return IOUtils.toString(is) ;
		}catch(Exception ex) {
			throw new RuntimeException(ex) ;
		}
	}
	
	public static double parseDouble(String str) {
		try{
			str = str.trim().replaceAll(",", "") ;
			int sign = 1 ;
			if(str.startsWith("(") && str.endsWith(")")) {
				sign = -1 ;
				str = str.substring(1, str.length() - 1) ;
			}
			double m = 1;
			if(str.endsWith("%")) {
				str = str.substring(0, str.length() -1) ;
				m = 0.01 ;
			}
			return Double.parseDouble(str) * sign * m;
		}catch(Exception ex) {
			System.out.println("parseDouble exception:" + str) ;
			ex.printStackTrace();
		}
		return 0;
	}
	
	public static String formatMoney(double d) {
      DecimalFormat myFormatter = new DecimalFormat("###,###,###");
      String output = myFormatter.format(d);
      return output ;
	}
	
	public static DecimalFormat df3 = new DecimalFormat("#.###") ; 
	
	public static double trim(double d, int sf) {
		for(int i=0; i<sf; i++) {
			d = d * 10 ;
		}
		d = Math.round(d) ;
		for(int i=0; i<sf; i++) {
			d = d / 10 ;
		}
		return d;
	}
	
	public static double trim(double d) {
		if(d > 100) return Math.round(d) ;
		return Math.round(d * 100) / 100d ;
	}

	public static void main(String args[]){
		System.out.println(getFile("list-template.html")) ;
	}
	public static int len(String s) {
		if(s == null) return 0 ;
		int len = 0 ; 
		for(char c : s.toCharArray()) {
			int i = (int) 0xffff&c ;
			if(c > 128) {
				len += 2 ;
			}else{
				len ++ ;
			}
		}
		return len ;
	}
	public static String pad(String s,int w) {
		while(len(s) < w) {
			s = s + ' ' ;
		}
		return s; 
	}
}
