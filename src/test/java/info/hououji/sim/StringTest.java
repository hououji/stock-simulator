package info.hououji.sim;

import java.util.Date;

public class StringTest {

	public static void main(String args[]) throws Exception {
		String s = "1,2,,,3,4" ;
		System.out.println(s.split(",").length) ;
		
		int count = 1;
		for(String p : s.split(",")) {
			System.out.println(count + ":"  +p) ;
			count ++  ;
		}
		
		System.out.println(new Date(1495209600L * 1000)) ;
	}
}
