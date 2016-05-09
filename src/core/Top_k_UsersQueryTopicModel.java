/**
 * 
 */
package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;


/**
 * @author ould
 *
 */
public class Top_k_UsersQueryTopicModel {

	/**
	 * Cette classe calcule P(Q|U) en utilsiant le toopic modeling
	 * 
	 * @param args
	 * 
	 */
	static org.jdom2.Document document;
	static Element racine;
	public static boolean ASC = true;
	public static boolean DESC = false;

	public static String inferencer_users_topic_file;
	public static String inferencer_query_topic_file;
	public static String train_dataset;
	
	public static FileWriter top_k_user ;

	public static void main(String[] args) throws IOException {
		inferencer_users_topic_file =""; // inferencer file for user
		inferencer_query_topic_file = ""; // inferencer file for query 
		train_dataset = ""; // train datasets : from doc or from tag or from doc+tag
		int k_topic = 100;
		top_k_user = new FileWriter(train_dataset+"_top_k_user"+k_topic+"_topic");
		ArrayList<String> liste_users = new ArrayList<String>();

		// for each query

		Scanner sc = new Scanner(new File("liste_user"));
		while (sc.hasNextLine()) {
			liste_users.add(sc.nextLine());
		}
		sc.close();

		SAXBuilder sxb = new SAXBuilder();
		try
		{
			document = sxb.build(new File("./evaluation1/topics_bibsonomy_4.xml"));
		}
		catch(Exception e){}
		racine = document.getRootElement();
		List<Element> listQuery = racine.getChildren("TOP");
		Iterator<Element> i = listQuery.iterator();
		while(i.hasNext())		
		{
			Map<String, Double> map_k_user_score_topic= new HashMap<String, Double>();
			Element courant = i.next();
			top_k_user.write(courant.getChild("NUM").getText() +" " );

			// for a current query selection top k user 
			// for each user compute P(Q|U)
			for (int i1 = 0; i1 < liste_users.size(); i1++) {
				map_k_user_score_topic.put(Integer.toString(i1),
						getScoreQueryUserTopicModel(k_topic, courant.getChild("NUM").getText(),Integer.toString(i1)));
			}
			// sort
			map_k_user_score_topic = sortByComparator(map_k_user_score_topic, DESC);

			// save file
			top_k_user.write("train_dataset"+" "+"k_topic"+" "+"query_num"+" "+"query"+" "+"user_id"+":score_user" +"\n");
			top_k_user.write(train_dataset+" "+k_topic+" "+courant.getChild("NUM").getText()+" "+courant.getChild("TITLE").getText()+" ");
			for(Entry<String, Double> entry : map_k_user_score_topic.entrySet()) {
				top_k_user.write(entry.getKey()+":"+entry.getValue()+" ");
			}
			top_k_user.write("\n");

		}	
	}


	/**
	 * 
	 * @param k_topic : le set de train
	 * @param query_id : identifiant de la requete
	 * @param user_id : identifiant de l'utisateur 
	 * @return score P(q|u) en utilisant le topc model 
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static Double getScoreQueryUserTopicModel(int k_topic, String query_id, String user_id) throws NumberFormatException, IOException{
		Double score_topic = 0.0;
		for (int i = 0; i < k_topic; i++) {			
			score_topic += getScoreUserTM(Integer.toString(k_topic),Integer.toString(i),user_id)
					      *getScoreQueryTM(Integer.toString(k_topic),Integer.toString(i),query_id);		
		}				
		return score_topic;
	}

	/**
	 * 
	 * @param k_topic : le set de training topic (10,20,30,...200) 
	 * @param topic : le topic (0....N) suivant le le set k_topic
	 * @param user_id : ide utilisateur 
	 * @return score topi pour un user suivant un topic 
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static double getScoreUserTM(String k_topic,String topic, String user_id) throws NumberFormatException, IOException{
		Double score = 0.0;
		//String path = "./files/inf-train."+k_topic;
		BufferedReader in_user  = new BufferedReader(new FileReader(inferencer_users_topic_file));
		String line = "";		
		while ((line = in_user.readLine()) != null) {	
			if(!line.startsWith("#")){
				String [] vect = line.split(" ");
				if(vect[0].equals(user_id)){
					for (int i = 2; i < vect.length; i=i+2) {
						if(vect[i].equals(topic) ){
							score = Double.parseDouble(vect[i+1]);
							break;
						}
					}					
				}
			}
		}
		in_user.close();
		return score;
		/*
		for(Entry<String, Double> entry : map_score_topic.entrySet()) {
		    String cle = entry.getKey();
		    Double valeur = entry.getValue();

		    System.out.println(cle+" "+valeur);
		}
		 */

	}


	/**
	 * 
	 * @param k_topic
	 * @param topic
	 * @param query_id
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static Double getScoreQueryTM(String k_topic,String topic, String query_id) throws NumberFormatException, IOException{		
		Double score = 0.0;
		//String path = "./files/inf-train."+k_topic;
		BufferedReader in_topic  = new BufferedReader(new FileReader(inferencer_query_topic_file));
		String line = "";		
		while ((line = in_topic.readLine()) != null) {	
			if(!line.startsWith("#")){
				String [] vect = line.split(" ");
				if(vect[0].equals(query_id)){
					for (int i = 2; i < vect.length; i=i+2) {
						if(vect[i].equals(topic) ){
							score = Double.parseDouble(vect[i+1]);
							break;
						}
					}					
				}
			}
		}
		in_topic.close();
		return score;
	}


	private static Map<String, Double> sortByComparator(Map<String, Double> unsortMap, final boolean order)
	{
		List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(unsortMap.entrySet());
		Collections.sort(list, new Comparator<Entry<String, Double>>()
		{
			public int compare(Entry<String, Double> o1,Entry<String, Double> o2)
			{if (order)
			{return o1.getValue().compareTo(o2.getValue());}
			else
			{return o2.getValue().compareTo(o1.getValue());
			}
			}
		});
		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Entry<String, Double> entry : list)
		{
			sortedMap.put(entry.getKey(), entry.getValue());
			// System.out.println(entry.getKey() + "   " +entry.getValue());
		}

		return sortedMap;
	}


}
