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
		String codes[] = new String[]{
				"A","AAPL","ABC","ABT","ADBE","ADI","ADM","ADP","ADSK","AEE","AEP","AES","AFL","AGN","AIG","AIV","AIZ","AKAM","AKS","ALL","ALTR","AMAT","AMCC","AMD","AMGN","AMP","AMT","AMZN","AN","ANF","AON","APA","APC","APD","APH","APOL","ATI","AVB","AVY","AXP","AZO","BA","BAX","BBBY","BBY","BDX","BEN","BF.B","BIG","BIIB","BK","BLL","BMY","BRCM","BRK.B","BSX","BTU","BXP","C","CA","CAG","CAH","CAM","CAT","CB","CBG","CCE","CCL","CERN","CF","CHK","CHRW","CI","CIEN","CINF","CL","CLF","CLX","CMA","CMCSA","CME","CMI","CMS","CNP","CNX","COF","COG","COP","COST","CPB","CPWR","CRM","CSC","CSCO","CSX","CTAS","CTL","CTSH","CTX","CTXS","CVS","CVX","D","DD","DE","DELL","DF","DFS","DGX","DHI","DHR","DIS","DISCA","DNR","DO","DOV","DOW","DRI","DTE","DUK","DV","DVA","DVN","EBAY","ECL","ED","EFX","EIX","EL","EMN","EMR","EOG","EQR","EQT","ETFC","ETN","ETR","EXC","EXPD","EXPE","F","FAST","FCX","FDX","FE","FHN","FII","FIS","FISV","FITB","FLIR","FLR","FLS","FMC","FPL","FSLR","FTI","GAS","GCI","GD","GE","GILD","GIS","GLW","GM","GME","GNW","GOOG","GPC","GPS","GS","GT","GWW","HAL","HBAN","HCBK","HD","HES","HIG","HOG","HON","HP","HRB","HRL","HRS","HST","HSY","HUM","IBM","ICE","IFF","IGT","INTC","INTU","IP","IPG","IR","IRM","ISRG","ITT","ITW","IVV","IVZ","JBL","JCI","JCP","JNJ","JNPR","JNS","JPM","JWN","K","KEY","KIM","KLAC","KMB","KMX","KO","KR","KSS","L","LEG","LEN","LH","LLTC","LLY","LM","LMT","LNC","LOW","LSI","LUK","LUV","LXK","M","MA","MAR","MAS","MAT","MCD","MCHP","MCK","MCO","MDP","MDT","MET","MIL","MKC","MMC","MMM","MO","MRK","MRO","MS","MSFT","MTB","MU","MUR","MWW","MYL","NBL","NBR","NDAQ","NEM","NI","NKE","NOC","NOV","NRG","NSC","NTAP","NTRS","NUE","NVDA","NVLS","NWL","NYT","ODP","OI","OKE","OMC","ORCL","ORLY","OXY","PAYX","PBCT","PBI","PCAR","PCG","PCL","PD","PDCO","PEG","PEP","PFE","PFG","PG","PGN","PGR","PH","PHM","PKI","PLD","PLL","PM","PMCS","PMO","PNC","PNW","POM","PPG","PPL","PRU","PSA","PWR","PXD","Q","QCOM","QEP","QLGC","R","RDC","RF","RHI","RHT","RL","ROK","ROP","ROST","RRC","RRD","RSG","RTN","S","SBUX","SCG","SCHW","SEE","SHLD","SHW","SJM","SLB","SNA","SNDK","SO","SPG","SPY","SRCL","SRE","STT","STZ","SUN","SVU","SWK","SWN","SYK","SYMC","SYY","T","TAP","TDC","TE","TER","TGT","THC","TIF","TJX","TMK","TMO","TROW","TRV","TSN","TSS","TXN","TXT","UNH","UNM","UNP","UPS","URBN","USB","UTX","V","VAR","VFC","VLO","VMC","VNO","VRSN","VTR","VZ","WAT","WEC","WFC","WHR","WIN","WM","WMB","WMT","WU","WY","WYNN","X","XEL","XL","XLNX","XOM","XRAY","XRX","YHOO","YUM","ZION"	
		};

		// ROE > 15%, Sale > 15%(5 years), Earing > 20% (5 years), Cap > 2b
//		String codes[] = new String[]{
//				"AB","ABMD","AEL","AL","ALGN","AM","AMCX","AMN","AMZN","ANET","APPF","ASGN","BIIB","BKNG","CBRE","CELG","CHTR","COHR","COR","CQP","DFS","DHI","DXC","EQGP","EVR","EXEL","EXP","EXR","FB","FIVE","FLT","FND","FOXF","GLPI","GWR","HALO","HQY","ILMN","IPGP","KHC","KW","LGND","LRCX","MKSI","MKTX","MOH","MPLX","MPWR","MU","NCLH","NFLX","NKTR","NRZ","NVDA","OLLI","PAYC","PCTY","PKG","PSXP","RH","RHT","SFM","STMP","STZ","SWKS","TAL","THO","TNET","TPL","TREE","TWTR","UBNT","ULTA","VEEV","VLP","WAL","WGP"	
//		};

		// Dividend > 6%, +Mid, ROE > 10%
//		String codes[] = new String[]{
//				"AB","APAM","GLPI","IRM","M","MFA","MO","PMT"
//		};
		
		//test
//		String codes[] = new String[]{
//				"WAL"	
//		};
		
		
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
