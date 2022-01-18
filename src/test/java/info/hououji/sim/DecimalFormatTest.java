package info.hououji.sim;

import java.text.DecimalFormat;

public class DecimalFormatTest {

	public static void main(String args[]) {
		DecimalFormat df = new DecimalFormat("0.##E0");
//		df.setMaximumFractionDigits(1);
		double d = 120000000.1234 ;
		System.out.println(df.format(d)) ;
		
	}
}
