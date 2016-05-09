/**
 * 
 */
package core.results;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


/**
 * @author ould
 *
 */
public class TopKDocument {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		FileWriter files = new FileWriter("fusion_query_docs_tags_top_100.res");
		for (int i = 0; i < 200; i++) {
			int count = 0;
			Scanner sn = new Scanner(new File("fusion_query_docs_tags.res"));
			while (sn.hasNextLine()) {
				String line = sn.nextLine();
				String [] vect = line.split(" ");
				if(Integer.parseInt(vect[0])== i && i!=4 && count <100){
					files.write(line +"\n");
					count++;
				}
			}
			sn.close();	

		}
		files.close();


	}
}
