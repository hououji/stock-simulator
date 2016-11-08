
import java.io.File;
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
				
				if(h.isMarketCapGreat(csv.getCode(), 200) == false) continue;
				if(csv.getLen() <  251)  continue;
				if(csv.max(0, 10, CSV.VOL) < 0.1) continue;
	
				int len = 250 ;
				double v[] = new double[len] ;
				String code = csv.getCode() ;
				for(int i=0; i<len; i++) {
//					v[i] = (csv.get(i, CSV.ADJ_CLOSE) - csv.get(i+1, CSV.ADJ_CLOSE)) /  csv.get(i+1, CSV.ADJ_CLOSE)  * 100;
					double a5 = csv.avg(i, 5, CSV.ADJ_CLOSE) ;
					double a20 = csv.avg(i, 20, CSV.ADJ_CLOSE) ;
					v[i] = (a5 - a20) / a20 * 100;
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
		for(Dataset ds : clusters) {
			System.out.println("cluster : " + count);
			count ++ ;
			for(int i=0; i<ds.size(); i++) {
				int id = ds.get(i).getID() ;
				String code = classId.get(ds.get(i).getID()) ;
				String name = h.getName(code) ;
				System.out.println("id:" + ds.get(i).getID() + "," + code + " - " + name);
			}
		}

	}
	
	public static void main(String args[]) throws Exception {
		new Clustering().run() ;
	}
}
