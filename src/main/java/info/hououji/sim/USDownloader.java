package info.hououji.sim;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class USDownloader implements Runnable {

	String code ; 
	File dir ;
	String crumb ; 
	
	public USDownloader(String code, File dir, String crumb) {
		this.code = code ;
		this.dir = dir ;
		this.crumb = crumb ;
	}
	
	public static File getRecentDirectory() {
		File root = new File("./data-us") ;
		File target = null ;
		for(File dir : root.listFiles()) {
			if( ! dir.isDirectory()) continue;
			if(target == null) {
				target = dir;
				continue;
			}
			if(dir.getName().compareTo(target.getName()) > 0) {
				target = dir ;
			}
		}
		return target;
	}
	
	public static String getName(File file) {
		String filename = file.getName() ;
		return filename.substring(0, 4) ;
	}
	
	public static String getCrumb() throws Exception {
		// Get crumb
		CookieManager manager = new CookieManager();
		manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(manager);
		CookieStore cookieJar =  manager.getCookieStore();

		String urlstr = "https://hk.finance.yahoo.com/quote/0016.HK/history?period1=0&period2=1495382400&interval=1d&filter=history&frequency=1d" ;

		String crumb = null ;
		while(true) {
			String html = CachedDownload.getString(new URL(urlstr),false) ;
			crumb = getYahooCrumb(html) ;
			System.out.println(crumb) ;
			if(crumb.indexOf("002F")== -1) break; // retry if crumb include 002F
			System.out.println("get crumb fail, wait for retry") ;
			Thread.sleep(5000);
	//		System.out.println(html);
	//		PrintStream out = new PrintStream(new FileOutputStream("out.html")) ;
	//		out.println(html) ;
	//		out.flush(); 
	//		out.close();
		}

		// Setup the cookies store
		
		CookieManager manager2 = new CookieManager();
		CookieHandler.setDefault(manager2);
		CookieStore cookieJar2 =  manager2.getCookieStore();
		List <HttpCookie> cookies = cookieJar.getCookies();
		for (HttpCookie cookie: cookies) {
			cookieJar2.add(new URL("https://query1.finance.yahoo.com/").toURI(), cookie);
		}
		return crumb;
	}
	
	public static void main(String args[]) throws Exception{

		SSLTool.disableCertificateValidation();
		
		String crumb = getCrumb() ;
		System.out.println("crumb : " + crumb) ;
		
		// Go
		File dir = new File("./data-us/" + new SimpleDateFormat("yyyyMMdd").format(new Date())) ;
		dir.mkdirs() ;
		
		ExecutorService executor = Executors.newFixedThreadPool(5);
		
//		// SP500
//		String codes[] = new String[]{
//				"MMM","ABT","ABBV","ACN","ATVI","AYI","ADBE","AAP","AES","AET","AMG","AFL","A","APD","AKAM","ALK","ALB","AGN","LNT","ALXN","ALLE","ADS","ALL","GOOGL","GOOG","MO","AMZN","AEE","AAL","AEP","AXP","AIG","AMT","AWK","AMP","ABC","AME","AMGN","AMSG","APH","APC","ADI","ANTM","AON","APA","AIV","AAPL","AMAT","ADM","ARNC","AJG","AIZ","T","ADSK","ADP","AN","AZO","AVB","AVY","BHI","BLL","BAC","BK","BCR","BAX","BBT","BDX","BBBY","BRK-B","BBY","BIIB","BLK","HRB","BA","BWA","BXP","BSX","BMY","AVGO","BF-B","CHRW","CA","COG","CPB","COF","CAH","HSIC","KMX","CCL","CAT","CBG","CBS","CELG","CNC","CNP","CTL","CERN","CF","SCHW","CHTR","CHK","CVX","CMG","CB","CHD","CI","XEC","CINF","CTAS","CSCO","C","CFG","CTXS","CLX","CME","CMS","COH","KO","CTSH","CL","CMCSA","CMA","CAG","CXO","COP","ED","STZ","GLW","COST","COTY","CCI","CSRA","CSX","CMI","CVS","DHI","DHR","DRI","DVA","DE","DLPH","DAL","XRAY","DVN","DLR","DFS","DISCA","DISCK","DG","DLTR","D","DOV","DOW","DPS","DTE","DD","DUK","DNB","ETFC","EMN","ETN","EBAY","ECL","EIX","EW","EA","EMR","ENDP","ETR","EOG","EQT","EFX","EQIX","EQR","ESS","EL","ES","EXC","EXPE","EXPD","ESRX","EXR","XOM","FFIV","FB","FAST","FRT","FDX","FIS","FITB","FSLR","FE","FISV","FLIR","FLS","FLR","FMC","FTI","FL","F","FTV","FBHS","BEN","FCX","FTR","GPS","GRMN","GD","GE","GGP","GIS","GM","GPC","GILD","GPN","GS","GT","GWW","HAL","HBI","HOG","HAR","HRS","HIG","HAS","HCA","HCP","HP","HES","HPE","HOLX","HD","HON","HRL","HST","HPQ","HUM","HBAN","ITW","ILMN","IR","INTC","ICE","IBM","IP","IPG","IFF","INTU","ISRG","IVZ","IRM","JEC","JBHT","SJM","JNJ","JCI","JPM","JNPR","KSU","K","KEY","KMB","KIM","KMI","KLAC","KSS","KHC","KR","LB","LLL","LH","LRCX","LEG","LEN","LVLT","LUK","LLY","LNC","LLTC","LKQ","LMT","L","LOW","LYB","MAA","MTB","MAC","M","MNK","MRO","MPC","MAR","MMC","MLM","MAS","MA","MAT","MKC","MCD","MCK","MJN","MDT","MRK","MET","MTD","KORS","MCHP","MU","MSFT","MHK","TAP","MDLZ","MON","MNST","MCO","MS","MOS","MSI","MUR","MYL","NDAQ","NOV","NAVI","NTAP","NFLX","NWL","NFX","NEM","NWSA","NWS","NEE","NLSN","NKE","NI","NBL","JWN","NSC","NTRS","NOC","NRG","NUE","NVDA","ORLY","OXY","OMC","OKE","ORCL","PCAR","PH","PDCO","PAYX","PYPL","PNR","PBCT","PEP","PKI","PRGO","PFE","PCG","PM","PSX","PNW","PXD","PBI","PNC","RL","PPG","PPL","PX","PCLN","PFG","PG","PGR","PLD","PRU","PEG","PSA","PHM","PVH","QRVO","PWR","QCOM","DGX","RRC","RTN","O","RHT","REGN","RF","RSG","RAI","RHI","ROK","COL","ROP","ROST","RCL","R","CRM","SCG","SLB","SNI","STX","SEE","SRE","SHW","SIG","SPG","SWKS","SLG","SNA","SO","LUV","SWN","SE","SPGI","STJ","SWK","SPLS","SBUX","STT","SRCL","SYK","STI","SYMC","SYF","SYY","TROW","TGT","TEL","TGNA","TDC","TSO","TXN","TXT","COO","HSY","TRV","TMO","TIF","TWX","TJX","TMK","TSS","TSCO","TDG","RIG","TRIP","FOXA","FOX","TSN","UDR","ULTA","USB","UA","UA.C","UNP","UAL","UNH","UPS","URI","UTX","UHS","UNM","URBN","VFC","VLO","VAR","VTR","VRSN","VRSK","VZ","VRTX","VIAB","V","VNO","VMC","WMT","WBA","DIS","WM","WAT","WFC","HCN","WDC","WU","WRK","WY","WHR","WFM","WMB","WLTW","WEC","WYN","WYNN","XEL","XRX","XLNX","XL","XYL","YHOO","YUM","ZBH","ZION","ZTS"	
//		};

		// ROE > 15%, Sale > 15%(5 years), Earing > 20% (5 years), Cap > 2b
//		String codes[] = new String[]{
//				"AB","ABMD","AEL","AL","ALGN","AM","AMCX","AMN","AMZN","ANET","APPF","ASGN","BIIB","BKNG","CBRE","CELG","CHTR","COHR","COR","CQP","DFS","DHI","DXC","EQGP","EVR","EXEL","EXP","EXR","FB","FIVE","FLT","FND","FOXF","GLPI","GWR","HALO","HQY","ILMN","IPGP","KHC","KW","LGND","LRCX","MKSI","MKTX","MOH","MPLX","MPWR","MU","NCLH","NFLX","NKTR","NRZ","NVDA","OLLI","PAYC","PCTY","PKG","PSXP","RH","RHT","SFM","STMP","STZ","SWKS","TAL","THO","TNET","TPL","TREE","TWTR","UBNT","ULTA","VEEV","VLP","WAL","WGP"	
//		};

		//test
		String codes[] = new String[]{
				"WAL"	
		};
		
		
		for(int i=0; i<codes.length ; i++) {
			System.out.println("list "+i+"/" + codes.length)  ;
			String code  = codes[i] ;
			new USDownloader(code, dir, crumb).run();
			Thread.sleep(200);
		}
		executor.shutdown();
	}
	
	public static String getYahooCrumb(String html) {
		html = html.replaceAll("\\{", "\n").replaceAll("\\}", "\n") ;
		String crumb = "" ;
		for(String line : html.split("\n")) {
			if(line.indexOf("\"firstName\"") != -1 && line.indexOf("\"crumb\"") != -1) {
				crumb = line.split("\\\"")[3] ;
			}
		}
//		crumb = crumb.replaceAll("\\u002F", "\\") ;
		return crumb;
	}
	
	
	public void run()  {
		try {
			Log.log("download start : " + code) ;
			File file = new File(dir, code + ".csv") ;
			URL url = new URL("https://query1.finance.yahoo.com/v7/finance/download/"+code+"?period1=0&period2="+new Date().getTime()+"&interval=1d&events=history&crumb=" + crumb) ;
			String csv = CachedDownload.getString(url,false) ;// since the crumb will change , cache is no use 
			String line[] = csv.split("\n") ;
			StringBuffer sb = new StringBuffer() ;
			sb.append(line[0].trim() + "\n") ;
			for(int i=line.length-1; i>0 ;i--) {
				if( "".equals( line[i].trim()) ) continue ;
				if(line[i].indexOf("null" )!= -1 ) continue;
				sb.append(line[i].trim() + "\n") ;
			}
			
			PrintStream out2 = new PrintStream(new FileOutputStream(file)) ;
			out2.println(sb.toString()) ;
			out2.flush();
			out2.close();
			Log.log("download complete : " + code) ;
		}catch(FileNotFoundException fnfe) {
		// no such stock, just skip
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
//		try {
//			Date now = new Date() ;
//			//URL url = new URL("http://real-chart.finance.yahoo.com/table.csv?s="+code+".HK&d=8&e=13&f=2016&g=d&a=0&b=4&c=2000&ignore=.csv") ;
//			URL url = new URL("http://real-chart.finance.yahoo.com/table.csv?s="+code+".HK&d="+now.getMonth()+"&e="+now.getDate()+"&f="+(now.getYear() + 1900)+"&g=d&a=0&b=4&c=2000&ignore=.csv") ;
//			FileUtils.copyURLToFile(url, file) ;
//			Log.log("download complete : " + code) ;
//		}catch(FileNotFoundException fnfe) {
//			// no such stock, just skip
//		}catch(Exception ex) {
//			ex.printStackTrace();
//		}
	}
}
