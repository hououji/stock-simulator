import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;


public class Helper {

	Map<String, Double> marketCap = null ;
	Map<String, String> names = null ;
	
	private synchronized void initMarketCap() {
		marketCap = new LinkedHashMap<String,Double>() ;
		names = new LinkedHashMap<String,String>() ;
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("output/market_cap.csv"), "utf8") );
			while(true) {
				try{
					String line = in.readLine() ;
					if(line == null) break;
					StringTokenizer st = new StringTokenizer(line, ",") ;
					String code = st.nextToken() ;
					names.put(code,st.nextToken()) ;
					Double cap = Double.parseDouble(st.nextToken()) ;
					marketCap.put(code, cap) ;
//					System.out.println("'"+code+"' " + cap);
				}catch(Exception ex){
				}
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	public boolean isMarketCapGreat(String code, int i) {
		if(marketCap == null) initMarketCap() ;
		if(marketCap.get(code) == null || marketCap.get(code) < i) return false;
		return true;
	}
	public String getName(String code) {
		if(marketCap == null) initMarketCap() ;
		return names.get(code) ;
	}

}
