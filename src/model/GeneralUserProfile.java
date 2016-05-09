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

import org.apache.commons.collections4.ListUtils;

import utils.StemmingTerm;

/**
 * @author ould
 *
 */
public class GeneralUserProfile {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		FileWriter file_xml = new FileWriter("profile_complet_users_bibsonomy.xml");
		file_xml.write(	"	<TOPS> \n");
		ArrayList<String> liste_user = new ArrayList<String>();
		BufferedReader users  = new BufferedReader(new FileReader("./files_exp/id_users"));
		String line = "";	
		while ((line = users.readLine()) != null) {	 
			liste_user.add(line);			
		}
		users.close();

		ArrayList<String> liste_terme_topic = new ArrayList<String>();
		StemmingTerm porterStemmer = new StemmingTerm();
		BufferedReader topic  = new BufferedReader(new FileReader("./files_exp/tutorial_keys_100.txt"));
		String term ="";
		while ((term = topic.readLine()) != null) {
			String[] result = term.split("\\s");
			for (int x=0; x<result.length; x++){
				for (int i = 2; i < result.length; i++) {
					liste_terme_topic.add(porterStemmer.stripAffixes(result[i]));
				}

			}
		}
		topic.close();
		System.out.println(liste_terme_topic.size());
		
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
		//	System.out.println(user_profile.size());

			List<String> user_profile_u = new ArrayList<String>();
			user_profile_u = ListUtils.intersection(liste_terme_topic, user_profile);
			//System.out.println(user_profile.size()+" "+user_profile_u.size());
			userFile(user_profile_u, liste_user.get(i), file_xml);
		}
		
		file_xml.write(	"	</TOPS> \n");
		file_xml.close();
		
	}
	
	public static void userFile(List<String> profile, String id_user, FileWriter file_xml) throws IOException{
		file_xml.write("	<TOP> \n");
		file_xml.write("		<NUM>"+id_user+"</NUM>\n");
		file_xml.write("		<USER>"+id_user+"</USER>\n");
		file_xml.write("		<TITLE> " );
		for (int j = 0; j < profile.size(); j++) {
			file_xml.write(" " +profile.get(j)+" ");
		}
		file_xml.write("  </TITLE> \n");
		file_xml.write("	</TOP>\n");
	}

}
