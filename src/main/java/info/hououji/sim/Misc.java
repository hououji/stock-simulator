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
}
