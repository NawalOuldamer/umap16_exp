package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ProfileFiltring {

	public static void main(String[] args) throws FileNotFoundException {
		
		
		ArrayList<String> liste_users = new ArrayList<String>();

		Scanner sc = new Scanner(new File("liste_user"));
		while (sc.hasNextLine()) {
			liste_users.add(sc.nextLine());
		}
		sc.close();
		
		/*
		 * for each user we compute the profile filtred by topic
		 *  
		 */
		
		for (int i = 0; i < liste_users.size(); i++) {
			Scanner s_q = new Scanner(new File("topic"));
			while (s_q.hasNextLine()) {
				
			}
			s_q.close();
		}
		
		

	}
	
	
	
	
	
	
	
	
	public static void userProfile(){
		
		
		
	}
	
	
	
	
	

}
