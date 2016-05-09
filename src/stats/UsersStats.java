package stats;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class UsersStats {


	public static void main(String[] args) throws IOException {


		getDocuBookStatUser();


	}


	/**
	 * cette méthode permet de retourner les stats sur l'utilisateur : 
	 * user + nombre_de_doc + nombre_de_book
	 * @throws IOException
	 */
	public static void getDocuBookStatUser() throws IOException{
		FileWriter file_stat = new FileWriter("stats_users_nbr_doc_nbr_bookmarks_delicious_2006.txt");
		BufferedReader user  = new BufferedReader(new FileReader("./files_exp_del_2006/id_users_delicious_2006.txt"));		
		String line = "";	
		while ((line = user.readLine()) != null) {	
			int count_doc = 0;
			int count_bookmark = 0;
			BufferedReader user_file = new BufferedReader(new FileReader("y:/social_network/Delicious_2006/users_files_rename/"+line+".txt"));		
			String file = "";
			while ((file = user_file.readLine()) != null) {
				String [] vect = file.split("\t");
				if(vect.length==5){
					count_bookmark++;
					count_doc++;
				}
				else {
					count_doc++;
				}
			}
			user_file.close();

			if(count_doc!=0){
				file_stat.write(line+" "+count_doc+" "+count_bookmark+"\n");
			}
		}
		user.close();
		file_stat.close();
	}

}
