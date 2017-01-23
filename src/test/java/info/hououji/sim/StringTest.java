package info.hououji.sim;

public class StringTest {

	public static void main(String args[]) throws Exception {
		String s = "1,2,,,3,4" ;
		System.out.println(s.split(",").length) ;
		
		int count = 1;
		for(String p : s.split(",")) {
			System.out.println(count + ":"  +p) ;
			count ++  ;
		}
	}
}
