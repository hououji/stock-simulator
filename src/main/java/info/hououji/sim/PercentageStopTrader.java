package info.hououji.sim;
import java.io.File;


public class PercentageStopTrader extends Trader{

	private boolean debug = false;
	
	private double stopPercentage = 5 ;
	
	public PercentageStopTrader(double _stopPercentage) {
		stopPercentage = _stopPercentage;
	}
	
	@Override
	public Result trade(CSV csv, int backDays) {

		try{
			csv.setBaseDay(0);
			
			Result r = new Result() ;
			r.inValue = csv.get(backDays, CSV.ADJ_CLOSE) ;
			r.startBackDays = backDays ;
			double stopPrice = r.inValue * (1 - stopPercentage / 100) ; 
			for(int i=backDays ; i>0; i--) {
				double curr = csv.get(i, CSV.ADJ_CLOSE) ;
				if(debug) System.out.println("\t\tcurr,stop:" + i+" " + csv.getDate(i) + " "+curr + "," + stopPrice) ;
				if(curr < stopPrice) {
					// trigger
					r.outValue = curr ;
					r.finalBackDays = i ;
					r.finalDate = CSV.sdf.parse(csv.getDate(i)) ;
					return r;
				}
				double newStopPrice = curr * (1 - stopPercentage / 100) ;
				if(newStopPrice > stopPrice) stopPrice = newStopPrice ;  
			}
		}catch(Exception ex){
			ex.printStackTrace(); 
		}

		return null;
	}

}
