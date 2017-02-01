package info.hououji.sim.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

public class CachedDownload {

	public static String PREFIX_QUARTERLY = "q";
	public static String PREFIX_YEARLY = "y";
	public static String PREFIX_TEST = "test";
	
	public static File dir = new File("cache") ;
	
	public static File getFile(URL url, String prefix) throws IOException {
		String hash = DigestUtils.sha1Hex(url.toString()) ;
		String hash1 = hash.substring(0,2) ;
		String hash2 = hash.substring(2, hash.length()) ;
		File file = new File(dir, prefix + File.separator +hash1 + File.separator + hash2) ;
		if(file.exists()) return file ;
		
		URLConnection hc = url.openConnection() ;
		hc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
		InputStream in = hc.getInputStream() ;
		file.getParentFile().mkdirs() ;
		FileOutputStream out = new FileOutputStream(file) ;
		IOUtils.copy(in, out) ;
		IOUtils.closeQuietly(in);
		out.flush(); 
		IOUtils.closeQuietly(out);
		
		return file ;
	}
	
	public static String getString(URL url, String prefix) throws IOException {
		File file = getFile(url, prefix) ;
		FileInputStream in = new FileInputStream(file) ;
		String result = IOUtils.toString(in) ;
		IOUtils.closeQuietly(in);
		return result ;
	}
	
	public static void main(String args[]) throws Exception {
		CachedDownload.getFile(new URL("http://www.yahoo.com"), "test") ; 
	}
}
