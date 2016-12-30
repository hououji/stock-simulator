package info.hououji.sim;
import info.hououji.sim.MarketCapExcel.Row;

import java.util.Map;



public class MarketCapExcelTest {

	public static void main(String arg[]) {
		MarketCapExcel e = new MarketCapExcel() ;
		Map<String, Row> map = e.getMarketCapExcel() ;
		
		for(String code : map.keySet()) {
			System.out.println(map.get(code));
			System.out.println(e.isMarketCapGreat("0005", 12509)) ;
			System.out.println(e.getRow("0005").nav) ;
		}
	}
}
