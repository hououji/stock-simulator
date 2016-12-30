package info.hououji.sim;
import info.hououji.sim.MarketCapExcel.Row;

import java.util.Map;



public class MarketCapExcelTest {

	public static void main(String arg) {
		MarketCapExcel e = new MarketCapExcel() ;
		Map<String, Row> map = e.getMarketCapExcel() ;
	}
}
