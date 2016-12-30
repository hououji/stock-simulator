package info.hououji.sim;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.tools.weka.WekaClusterer;
import weka.clusterers.XMeans;
import weka.core.ManhattanDistance;

public class Clustering {

	private int minCap = 500 ;
	private int dayBack = 250 ;
	
	public void run() throws Exception {
		File dir = Downloader.getRecentDirectory() ;
		Helper h = new Helper() ;
//		run(new File(dir,"0015.csv")) ;
//		VolumeSpecialDetector d = new VolumeSpecialDetector() ;
		
		File[] files = dir.listFiles() ;
		Arrays.sort(files);
		Dataset data = new DefaultDataset();
		List<String> classId = new ArrayList<String>() ;
		for(File file : files) {
			try{
				CSV csv = new CSV(file) ;
				
				if(h.isMarketCapGreat(csv.getCode(), minCap) == false) continue;
				if(csv.getLen() <  251)  continue;
				if(csv.max(0, 10, CSV.VOL) < 0.1) continue;
	
//				int len = 250 ;
				double v[] = new double[dayBack] ;
				String code = csv.getCode() ;
				for(int i=0; i<dayBack; i++) {
					// 1
//					v[i] = (csv.get(i, CSV.ADJ_CLOSE) - csv.get(i+1, CSV.ADJ_CLOSE)) /  csv.get(i+1, CSV.ADJ_CLOSE)  * 100;
					
					// 2 MACD
					double a5 = csv.avg(i, 5, CSV.ADJ_CLOSE) ;
					double a20 = csv.avg(i, 20, CSV.ADJ_CLOSE) ;
					v[i] = (a5 - a20) / a20 * 100;
					
					// 3 Linear normalize
//					double min = csv.min(0, 250, CSV.ADJ_CLOSE) ;
//					double max = csv.max(0, 250, CSV.ADJ_CLOSE) ;
//					v[i] = csv.get(i, CSV.ADJ_CLOSE) ;
				}
				
				DenseInstance i = new DenseInstance(v,code) ;
				data.add(i) ;
				classId.add(code) ;
				System.out.println(code);
				
			}catch(Exception ex) {
				ex.printStackTrace(); 
			}
		}
		
		XMeans xm = new XMeans();
		xm.setMinNumClusters(10);
		xm.setMaxNumClusters(20);
		xm.setDistanceF(new ManhattanDistance());
		
		Clusterer jmlxm = new WekaClusterer(xm);
		Dataset[] clusters = jmlxm.cluster(data);
		
//		Clusterer km = new KMeans(5);
//		Dataset[] clusters = km.cluster(data);

		
		System.out.println("#cluster : " + clusters.length) ;
		int count = 1;
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream("output/clustering.html"), "utf8")) ;
		out.println("<html>\n<meta charset='utf8'>\n<style>td { font-size: 25px;}</style>\n") ; 
		out.println("min cap:" + this.minCap + ", day back:" + this.dayBack) ;
		out.println("<table>");
		for(Dataset ds : clusters) {
			System.out.println("cluster : " + count);
			out.println("<tr><td colspan='3' style='background-color:#1a1aff;color:#cccccc;'>Cluster "+count+"</td></tr>") ;
			count ++ ;
			for(int i=0; i<ds.size(); i++) {
				int id = ds.get(i).getID() ;
				String code = classId.get(ds.get(i).getID()) ;
				String name = h.getName(code) ;
				out.println("<tr><td>"+code+"</td><td>"+name+"</td>"
						+ "<td><img src='http://charts.aastocks.com/servlet/Charts?scheme=3&com=100&chartwidth=438&chartheight=250&stockid=00"+code+".HK&period=9&type=5&Indicator=1&indpara1=10&indpara2=20&indpara3=50&indpara4=100&indpara5=150&subChart1=1&ref1para1=0&ref1para2=0&ref1para3=0&fontsize=12&15MinDelay=T&lang=1&titlestyle=1&logoStyle=1&'/></td>"
						+ "</tr>") ;
				System.out.println("id:" + ds.get(i).getID() + "," + code + " - " + name);
			}
		}
		out.println("</table>");
		out.println("</html");
		out.flush(); 
		out.close();

	}
	
	public static void main(String args[]) throws Exception {
		new Clustering().run() ;
	}
}
