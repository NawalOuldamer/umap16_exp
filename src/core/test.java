/**
 * 
 */
package core;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @author ould
 *
 */
public class test {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
	
		FileWriter f = new FileWriter(args[0]);
		f.write("aaaaa"+ " " +args[0]);
		f.close();

	}

}
