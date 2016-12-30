package info.hououji.sim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.tools.weka.WekaClusterer;
import weka.clusterers.XMeans;

public class ClusterTest {

	
	public static void main(String args[]) throws Exception {
//		Dataset data =  FileHandler.loadDataset(new File("data/clustering.csv"), 0, ",");
		
		
		Dataset data = new DefaultDataset();
		BufferedReader in = new BufferedReader(new FileReader(new File("data/clustering.csv"))) ;
		List<String> classId = new ArrayList<String>() ;
		while(true) {
			String line = in.readLine() ;
			System.out.println(line) ;
			if(line == null) break ;
			
			StringTokenizer t = new StringTokenizer(line, ",") ;
			String c = t.nextToken() ;
			List<Double> v = new ArrayList<Double>() ;
			while(t.hasMoreElements()) {
				v.add(Double.parseDouble(t.nextToken())) ;
			}
			
			double[] da = new double[v.size()] ;
			for(int i=0; i<v.size(); i++) {
				da[i] = v.get(i) ;
			}
			
			DenseInstance i = new DenseInstance(da, c) ;
			data.add(i) ;
			classId.add(c) ;
		}
		
//		Clusterer km = new KMeans(3);
//		Dataset[] clusters = km.cluster(data);
		
		XMeans xm = new XMeans();
		Clusterer jmlxm = new WekaClusterer(xm);
		Dataset[] clusters = jmlxm.cluster(data);
		
		System.out.println("#cluster : " + clusters.length) ;
		for(Dataset ds : clusters) {
			System.out.println("cluster : ");
			for(int i=0; i<ds.size(); i++) {
				System.out.println("id:" + ds.get(i).getID() + "," +classId.get(ds.get(i).getID()));
			}
		}
	}
}
