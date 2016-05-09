/**
 * 
 */
package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author ould
 *
 */
public class FriendsRelevance {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		
		
		
		
		ArrayList<String> liste_users = new ArrayList<String>();
		Scanner sc = new Scanner(new File("liste_user"));
		while (sc.hasNextLine()) {
			liste_users.add(sc.nextLine());
		}
		sc.close();
		
		
		for (int i = 0; i < args.length; i++) {
			
		}
		
		

	}

}
