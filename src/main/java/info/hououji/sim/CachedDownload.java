package info.hououji.sim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

public class CachedDownload {

	public static File dir = new File("cached") ;
	
	public static File getFile(URL url) throws IOException {
		String sha1 = DigestUtils.sha512Hex(url.toString()) ;
		String sha11 = sha1.substring(0, 2) ;
		String sha12 = sha1.substring(2, sha1.length()) ;
		File outfile = new File(dir, sha11 + File.separator + sha12) ;
		if(outfile.exists()) return outfile ;
		
		URLConnection hc = url.openConnection() ;
		hc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
		String html = IOUtils.toString(hc.getInputStream()) ;

		outfile.getParentFile().mkdirs() ;
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outfile), "utf-8")) ;
		out.println(html);
		out.flush();
		out.close();

		return outfile ;
	}
	
	public static String getString(URL url) throws IOException {
		File file = getFile(url) ;
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8")) ;
		String html = IOUtils.toString(in) ;
		return html; 
	}
}
