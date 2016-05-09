/**
 * 
 */
package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utils.StemmingTerm;


import org.apache.commons.collections4.ListUtils;
/**
 * @author ould
 *
 */
public class ProfileFiltering {

	/**
	 * @param args
	 * @throws IOException 
	 */

	public static void main(String[] args) throws IOException {
		int nbr_topic = Integer.parseInt(args[0]);
		int d = Integer.parseInt(args[1]);
		int f = Integer.parseInt(args[2]);
		ArrayList<String> liste_user = new ArrayList<String>();
		BufferedReader users  = new BufferedReader(new FileReader("./files_exp/id_users"));
		String line = "";	
		while ((line = users.readLine()) != null) {	 
			liste_user.add(line);			
		}
		users.close();
		for (int i = 0; i < nbr_topic; i++) {
			for (int i1 = d; i1 < f; i1++) {
				if(i1!=49){ //i1!=4 pour bibsonomy and 49 pour delic
					FileWriter file = new FileWriter("./filtred_users_profiles_v2/user_profile_filtered_number_of_topic_"+i+"_query_"+i1+".txt", true);
					FileWriter file_xml = new FileWriter("./filtred_users_profiles_v2_xml/user_profile_filtered_number_of_topic_"+i+"_query_"+i1+".xml", true);
					file_xml.write("<TOPS> \n");
					getFiltredUserProfile(i,String.valueOf(i1),liste_user,file, file_xml);
					file.close();
					file_xml.write("</TOPS> \n");
					file_xml.close();
				}

			}

		}
	}

	public static void getFiltredUserProfile(int number_of_topic, String id_query, ArrayList<String> liste_user, FileWriter file, FileWriter file_xml) throws IOException{
		StemmingTerm porterStemmer = new StemmingTerm();
		BufferedReader topic_query  = new BufferedReader(new FileReader("./files_exp/topic_query.txt"));
		String line = "";	
		ArrayList<String> liste_topic = new ArrayList<String>();
		while ((line = topic_query.readLine()) != null) {	
			String [] vect = line.split(" ");				
			if(vect[0].equals(id_query)){
				for (int k = 0; k <= number_of_topic; k = k+2) {
					liste_topic.add(vect[2+k]);
				}
				break;
			}
		}
		topic_query.close();


		ArrayList<String> liste_terme_topic = new ArrayList<String>();

		BufferedReader topic  = new BufferedReader(new FileReader("./files_exp/tutorial_keys_100.txt"));
		String term ="";
		while ((term = topic.readLine()) != null) {
			String[] result = term.split("\\s");
			for (int x=0; x<result.length; x++){
				if(liste_topic.contains(result[x])){
					for (int i = 2; i < result.length; i++) {
						liste_terme_topic.add(porterStemmer.stripAffixes(result[i]));
					}
				}
			}
		}
		topic.close();
		for (int i = 0; i < liste_user.size(); i++) {
			ArrayList<String> user_profile = new ArrayList<String>();
			BufferedReader topic_user  = new BufferedReader(new FileReader("./files_exp/indexUsersBibsonomyTF-IDF.txt"));
			String line_user = "";	
			while ((line_user = topic_user.readLine()) != null) {
				String [] vect = line_user.split(" ");
				if(vect[0].equals(liste_user.get(i))){
					for (int j = 2; j < vect.length; j++) {
						String []v = vect[j].split(":");
						user_profile.add(porterStemmer.stripAffixes(v[0])); // requete);
					}					
				}

			}
			topic_user.close();	
			System.out.println(user_profile.size());
			// profile filtre
			List<String> user_profile_filtered = new ArrayList<String>();
			user_profile_filtered = ListUtils.intersection(liste_terme_topic, user_profile);

			if (!user_profile_filtered.isEmpty()) {
				file.write(id_query+" "+number_of_topic+" "+liste_user.get(i)+" ");
				for (int i1 = 0; i1 < user_profile_filtered.size(); i1++) {
					file.write(user_profile_filtered.get(i1)+" ");
					file.flush();
				}
				System.err.println(user_profile_filtered.size());
				file.write("\n");	
				userFile(user_profile_filtered, liste_user.get(i), file_xml, id_query);
			}
		}





	}

	public static void userFile(List<String> profile_filtred, String id_user, FileWriter file_xml, String id_query) throws IOException{
		file_xml.write("	<TOP> \n");
		file_xml.write("		<NUM>"+id_user+"</NUM>\n");
		file_xml.write("		<USER>"+id_user+"</USER>\n");
		file_xml.write("		<TITLE> " );
		for (int j = 0; j < profile_filtred.size(); j++) {
			file_xml.write(" " +profile_filtred.get(j)+" ");
		}
		file_xml.write("  </TITLE> \n");
		file_xml.write("	</TOP>\n");
	}
}
