import java.io.InputStream;

import org.apache.commons.io.IOUtils;


public class Misc {

	public static String getFile(String name) {
		try{
			InputStream is = Misc.class.getClassLoader().getResourceAsStream(name) ;
			return IOUtils.toString(is) ;
		}catch(Exception ex) {
			throw new RuntimeException(ex) ;
		}
	}
	
	public static void main(String args[]){
		System.out.println(getFile("list-template.html")) ;
	}
}
