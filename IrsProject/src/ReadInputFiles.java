
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.*;
import java.lang.Math;
public class ReadInputFiles {

	private static final String DOC_END_TAG = "</DOCNO>";
	private static final String DOC_START_TAG = "<DOCNO>";
	private static final String TEXT_END_TAG = "</TEXT>";
	private static final String TEXT_START_TAG = "<TEXT>";
	private static final String Query_Num_TAG = "<num> Number:";
	private static final String Query_Title_TAG = "<title>";
	private static final String Query_Desc_Start_TAG = "<desc> Description:";
	private static final String Query_Desc_End_TAG = "<narr> Narrative:";

	// private ReadInputDatatype readInputDatatype;
	private List<String> stopList;
	private String line;
	private int index, counter;
	private Map<String, Integer> wordDict;
	private Map<String, Integer> fileDict;
	private Map<Integer, Map<Integer, Integer>> forward_index;
	private Map<Integer, Map<Integer, Integer>> reverse_index;
	private HashMap<Integer, Integer> forward_inner;
	private Map<Integer, Integer> reverse_inner;
	private Porter porter;

	public ReadInputFiles() {
		super();
		counter = 0;
		index = 0;
		stopList = new ArrayList<>();
		line = null;
		wordDict = new TreeMap<>();
		fileDict = new HashMap<>();
		forward_index = new HashMap<Integer, Map<Integer, Integer>>();
		reverse_index = new HashMap<Integer, Map<Integer, Integer>>();
		forward_inner = new HashMap<>();
		reverse_inner = new HashMap<>();
		porter = new Porter();
	}

	// Test
	public static void main(String args[]) {
		//String query_term = "";
		//Integer term_id = null;
		System.out.println("Started at " + new Timestamp(System.currentTimeMillis()));
		ReadInputFiles readInputFiles = new ReadInputFiles();
		if (readInputFiles.stopList.isEmpty()) {
			readInputFiles.stopList = readInputFiles.loadStopList();
		}
		readInputFiles.loadData();
		readInputFiles.writeToFile(readInputFiles);
		ReadInputFiles readquery= new ReadInputFiles();
		if (readquery.stopList.isEmpty()) {
			readquery.stopList = readquery.loadStopList();
		}
		readquery.processQueryFile("topics.txt");
		/*System.out.println(readquery.wordDict);
		System.out.println(readquery.fileDict);
		System.out.println(readquery.forward_index);
		System.out.println(" this is reverse index");
		System.out.println(readquery.reverse_index);*/
		readquery.score_calculation(readInputFiles.fileDict,readquery.fileDict,readInputFiles.wordDict, readquery.wordDict, readInputFiles.reverse_index, readquery.reverse_index, readInputFiles.forward_index, readquery.forward_index);
		/*
		 * System.out.println("Size of forward index file:");
		 * System.out.println(readInputFiles.forward_index.size());
		 * System.out.println("Size of reverse index file:");
		 * System.out.println(readInputFiles.reverse_index.size());
		 */
		/*
		 * for (String string : readInputFiles.sortedTokens) { if
		 * (!string.isEmpty()) {
		 * readInputFiles.wordDict.put(++readInputFiles.index, string); } }
		 */
		// Iterator<Entry<Integer, Map<Integer, Integer>>> outer_forward_map =
		// readInputFiles.forward_index.entrySet()
		// .iterator();
		// while (outer_forward_map.hasNext()) {
		// Entry<Integer, Map<Integer, Integer>> mapEntry =
		// outer_forward_map.next();
		// Iterator<Entry<Integer, Integer>> innerIterator =
		// mapEntry.getValue().entrySet().iterator();
		// while(innerIterator.hasNext()){
		//
		// }
		// }
		/*
		 * ReadInputFiles readtestFiles = new ReadInputFiles(); if
		 * (readtestFiles.stopList.isEmpty()) { readtestFiles.stopList =
		 * readtestFiles.loadStopList(); } readtestFiles.loadTestData();
		 * System.out.println("Enter  a query term:"); Scanner input_obj = new
		 * Scanner(System.in); query_term = input_obj.nextLine(); query_term =
		 * query_term.toLowerCase().replaceAll("\\w*\\d\\w*", "").trim(); if
		 * (!query_term.isEmpty() &&
		 * !readtestFiles.stopList.contains(query_term)) {
		 * 
		 * query_term = readtestFiles.porter.stripAffixes(query_term.trim()); }
		 * 
		 * if (readtestFiles.wordDict.containsKey(query_term)) { term_id =
		 * readtestFiles.wordDict.get(query_term); Map<Integer, Integer>
		 * term_reverse_map = new HashMap<>(); term_reverse_map =
		 * readtestFiles.reverse_index.get(term_id); for (Entry<Integer,
		 * Integer> entry2 : term_reverse_map.entrySet()) { //
		 * System.out.println(entry2); int document_id = entry2.getKey(); int
		 * count = entry2.getValue(); for (Entry<String, Integer> entry3 :
		 * readtestFiles.fileDict.entrySet()) { if (entry3.getValue() ==
		 * document_id) { System.out.println("Document name: " + entry3.getKey()
		 * + " term frequency: " + count); } } } } else {
		 * System.out.println("given word is not in any of the document"); }
		 */

		/*
		 * for (Entry<Integer, Map<Integer, Integer>> entry :
		 * readInputFiles.reverse_index.entrySet()) { System.out.println(entry);
		 * // for (Entry<Integer, Integer> innerEntry : //
		 * entry.getValue().entrySet()) { // System.out.println(innerEntry); //
		 * } }
		 */

		System.out.println("Completed at " + new Timestamp(System.currentTimeMillis()));

	}

	private void writeToFile(ReadInputFiles readInputFiles) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("./parser_output.txt"));

			readInputFiles.writeContent(readInputFiles.wordDict, writer);
			readInputFiles.writeContent(readInputFiles.fileDict, writer);

			writer.flush();
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			writer = new BufferedWriter(new FileWriter("./forwardindex_output.txt"));

			readInputFiles.writeIndexToFile(readInputFiles.forward_index, writer);
			// readInputFiles.writeContent(readInputFiles.fileDict, writer);

			writer.flush();
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			writer = new BufferedWriter(new FileWriter("./Reverseindex_output.txt"));

			readInputFiles.writeIndexToFile(readInputFiles.reverse_index, writer);
			// readInputFiles.writeContent(readInputFiles.fileDict, writer);

			writer.flush();
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to write the content to the output file.
	 * 
	 * @param mapToPrint
	 *            hash map write onto a file.
	 * @param writer
	 *            buffered writer object.
	 */
	private void writeIndexToFile(Map<Integer, Map<Integer, Integer>> indexmap, BufferedWriter writer) {
		Map<Integer, Integer> innermap = new HashMap<>();
		try {
			writer.write("-------------------------------------------");
			writer.newLine();
			for (Entry<Integer, Map<Integer, Integer>> entry : indexmap.entrySet()) {
				writer.write("-------------------------------------------");
				writer.newLine();
				writer.write("Outer Index Id: " + entry.getKey());
				// writer.write(entry.getKey());
				writer.newLine();
				writer.write("-------------------------------------------");
				writer.newLine();
				// writer.write("Inner Index, frequency vale");
				// writer.newLine();
				innermap = entry.getValue();
				for (Entry<Integer, Integer> entry2 : innermap.entrySet()) {
					writer.write("Inner Index Id: " + entry2.getKey() + " Frequency value: " + entry2.getValue());
					writer.newLine();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeContent(Map<String, Integer> mapToPrint, BufferedWriter writer) {

		try {
			writer.write("-------------------------------------------");
			writer.newLine();
			for (Entry<String, Integer> entry : mapToPrint.entrySet()) {
				writer.write(entry.getValue() + " " + entry.getKey());
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Method call to load all the files
	 */
	public void loadData() {
		// System.out.println("enter the documents folder path to parse: ");
		// Scanner sc=new Scanner(System.in);
		// String path=sc.nextLine();
		List<String> filesList = loadFiles("./src/ft911");

		for (String fileName : filesList) {
			processFileContent(fileName);
		}
	}

	public void loadTestData() {
		System.out.println("enter the documents folder path to parse for testing purpose: ");
		Scanner sc = new Scanner(System.in);
		String path = sc.nextLine();
		List<String> filesList = loadFiles(path);

		for (String fileName : filesList) {
			processFileContent(fileName);
		}
	}

	/**
	 * Method to load the files present in a folder.
	 * 
	 * @param path
	 *            to the folder containing the files.
	 * @return list of files to be read.
	 */
	private List<String> loadFiles(final String path) {
		List<String> filesToLoad = new ArrayList<>();
		File folder = new File(path);
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isFile()) {
				filesToLoad.add(fileEntry.getAbsolutePath());
			}
		}
		return filesToLoad;

	}

	/**
	 * 
	 * @return list containing the stop words
	 */
	private List<String> loadStopList() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("./src/stopwordlist.txt"));
			while ((line = reader.readLine()) != null) {
				stopList.add(line.trim());
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stopList;
	}

	/**
	 * Method to process the file content.
	 * 
	 * @param fileToRead
	 * @return list containing the data.
	 */
	private void processFileContent(String fileToRead) {
		BufferedReader bufferedReader = null;
		String docNumber = null;
		String textLine = null;
		// String wordnum=null;
		int doc_id = 0;
		int word_id;
		// List<ReadInputDatatype> readinputDataList = new ArrayList<>();

		try {
			bufferedReader = new BufferedReader(new FileReader(fileToRead));
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains(DOC_START_TAG)) {
					docNumber = line.substring(line.indexOf(DOC_START_TAG) + DOC_START_TAG.length(),
							line.indexOf(DOC_END_TAG));
					fileDict.put(docNumber, ++counter);
					doc_id = counter;

				} else if (line.contains(TEXT_START_TAG)) {
					forward_inner = new HashMap<>();
					while (!(textLine = bufferedReader.readLine()).contains(TEXT_END_TAG)) {
						for (String token : textLine.toLowerCase().replaceAll("\\w*\\d\\w*", "").trim()
								.split("\\s*[^a-z]\\s*")) {
							if (!token.isEmpty() && !stopList.contains(token)) {
								// TODO update code here to check for assignment
								// 2
								token = porter.stripAffixes(token.trim());
								if (!wordDict.containsKey(token)) {
									wordDict.put(token, ++index);

									// System.out.println("token
									// inserted"+token);

								}
								reverse_inner = new HashMap<>();
								word_id = wordDict.get(token);
								if (forward_inner.containsKey(word_id)) {
									forward_inner.put(word_id, forward_inner.get(word_id) + 1);
								} else {
									forward_inner.put(word_id, 1);
								}
								if (!reverse_index.containsKey(word_id)) {
									reverse_inner.put(doc_id, 1);
									reverse_index.put(word_id, reverse_inner);
									// reverse_inner=new HashMap<>();
									// reverse_inner.remove(doc_id);
								} else {
									if (!reverse_index.get(word_id).containsKey(doc_id)) {
										reverse_inner = reverse_index.get(word_id);
										reverse_inner.put(doc_id, 1);
										reverse_index.put(word_id, reverse_inner);
										// reverse_inner=new HashMap<>();
										// reverse_inner.remove(doc_id);
									} else {
										reverse_inner = reverse_index.get(word_id);
										reverse_inner.put(doc_id, reverse_inner.get(doc_id) + 1);
										reverse_index.put(word_id, reverse_inner);
										// reverse_inner=new HashMap<>();
										// reverse_inner.remove(doc_id);
									}
								}
							}
						}
					}
					if (!forward_index.containsKey(doc_id)) {
						forward_index.put(doc_id, forward_inner);
					}
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void processQueryFile(String fileToRead) {
		BufferedReader bufferedReader = null;
		String docNumber = null;
		String textLine = null;
		// String wordnum=null;
		int doc_id = 0;
		int word_id;
		// List<ReadInputDatatype> readinputDataList = new ArrayList<>();

		try {
			bufferedReader = new BufferedReader(new FileReader(fileToRead));
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains(Query_Num_TAG)) {
					docNumber = line.substring(line.indexOf(Query_Num_TAG) + Query_Num_TAG.length(), line.length())
							.trim();
					fileDict.put(docNumber, ++counter);
					doc_id = counter;

				} else if (line.contains(Query_Title_TAG)) {
					forward_inner = new HashMap<>();
					textLine = line.substring(line.indexOf(Query_Title_TAG) + Query_Title_TAG.length(), line.length())
							.trim();
					
					// while (!(textLine =
					// bufferedReader.readLine()).contains(TEXT_END_TAG)) {
					for (String token : textLine.toLowerCase().replaceAll("\\w*\\d\\w*", "").trim()
							.split("\\s*[^a-z]\\s*")) {
						if (!token.isEmpty() && !stopList.contains(token)) {
							// TODO update code here to check for assignment
							// 2
							token = porter.stripAffixes(token.trim());
							if (!wordDict.containsKey(token)) {
								wordDict.put(token, ++index);

								// System.out.println("token
								// inserted"+token);

							}
							reverse_inner = new HashMap<>();
							word_id = wordDict.get(token);
							if (forward_inner.containsKey(word_id)) {
								forward_inner.put(word_id, forward_inner.get(word_id) + 1);
							} else {
								forward_inner.put(word_id, 1);
							}
							if (!reverse_index.containsKey(word_id)) {
								reverse_inner.put(doc_id, 1);
								reverse_index.put(word_id, reverse_inner);
								// reverse_inner=new HashMap<>();
								// reverse_inner.remove(doc_id);
							} else {
								if (!reverse_index.get(word_id).containsKey(doc_id)) {
									reverse_inner = reverse_index.get(word_id);
									reverse_inner.put(doc_id, 1);
									reverse_index.put(word_id, reverse_inner);
									// reverse_inner=new HashMap<>();
									// reverse_inner.remove(doc_id);
								} else {
									reverse_inner = reverse_index.get(word_id);
									reverse_inner.put(doc_id, reverse_inner.get(doc_id) + 1);
									reverse_index.put(word_id, reverse_inner);
									// reverse_inner=new HashMap<>();
									// reverse_inner.remove(doc_id);
								}
							}
						}
					}
					// }
					if (!forward_index.containsKey(doc_id)) {
						forward_index.put(doc_id, forward_inner);
					}
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private int term_frequency(int word_id, int file_id,
			Map<Integer, Map<Integer, Integer>> reverse_index) {
		int term_frequency;
		
		term_frequency = reverse_index.get(word_id).get(file_id);
		
		return term_frequency;
	}
	
	private int doc_frequency(int word_id, int file_id,
			Map<Integer, Map<Integer, Integer>> reverse_index) {
		int doc_frequency;
		doc_frequency = reverse_index.get(word_id).size();
		return doc_frequency;
	}

	private double weight_value(int term_frequency, int doc_frequency, int length) {
		double weight;
		weight = term_frequency * Math.log10(length / doc_frequency);
		return weight;
	}
	static <K,V extends Comparable<? super V>> 
    List<Entry<K, V>> entriesSortedByValues(Map<K,V> map) {

		List<Entry<K,V>> sortedEntries = new ArrayList<Entry<K,V>>(map.entrySet());

	Collections.sort(sortedEntries, 
	    new Comparator<Entry<K,V>>() {
	        @Override
	        public int compare(Entry<K,V> e1, Entry<K,V> e2) {
	            return e2.getValue().compareTo(e1.getValue());
	        }
	    }
	);

	return sortedEntries;
	}

	private void score_calculation(Map<String, Integer> db_fileDict,Map<String, Integer> query_fileDict,Map<String, Integer> db_wordDict, Map<String, Integer> query_wordDict,
			Map<Integer, Map<Integer, Integer>> reverse_doc, Map<Integer, Map<Integer, Integer>> reverse_query,
			Map<Integer, Map<Integer, Integer>> forward_doc, Map<Integer, Map<Integer, Integer>> forward_query) {
		int db_length = forward_doc.size();
		int querydb_length = forward_query.size();
		double scores[] = new double[db_length];
		int querydoc_id;
		int queryterm_id;
		double weight_query;
		double weight_db;
		int temp_value;
		String queryterm=null;
		String query_num=null;
		int db_term_id;
		int db_doc_id;
		double euc_tf_value;
		Map<Integer, Integer> doc_postlists;
		Map<Integer, Integer> euclidean_doc_postlists;
		String db_doc_name=null;
		int rank=0;
		int query_term_frequency;
		int doc_term_frequency;
		int doc_frequency;
		List<Entry<Integer,Double>> sortedscores;
		double euc_query=0;
		try{
		BufferedWriter writer = new BufferedWriter(new FileWriter("./cosine_output.txt"));
		for (Entry<Integer, Map<Integer, Integer>> entry : forward_query.entrySet()) {
			Map<Integer,Double> score_fileid= new HashMap<>();
			querydoc_id = entry.getKey();
			euc_query=0;
			rank=0;
			//Map<Integer,Integer> query_term_entries= new HashMap<>();
			//query_term_entries= forward_query.get(querydoc_id);
			for (Entry<Integer, Integer> entry2 : entry.getValue().entrySet()) {
				queryterm_id = entry2.getKey();
				query_term_frequency=term_frequency(queryterm_id, querydoc_id, reverse_query);
				//weight_query = weight_value(queryterm_id, querydoc_id, querydb_length, reverse_query);
				for (Entry<String, Integer> entry3 : query_wordDict.entrySet()) {
					temp_value = entry3.getValue();
					if (temp_value == queryterm_id) {
						queryterm = entry3.getKey();
					}

				}
				if(!queryterm.isEmpty() && db_wordDict.containsKey(queryterm))
				{
				db_term_id = db_wordDict.get(queryterm);
				//System.out.println(db_term_id);
				if (!reverse_doc.containsKey(db_term_id)) {
					weight_db = 0;
				} else {
					doc_postlists = reverse_doc.get(db_term_id);
					for (Entry<Integer, Integer> doc : doc_postlists.entrySet()) {
						db_doc_id = doc.getKey();
						doc_term_frequency= term_frequency(db_term_id, db_doc_id, reverse_doc);
						doc_frequency=doc_frequency(db_term_id, db_doc_id, reverse_doc);
						weight_query=weight_value(query_term_frequency, doc_frequency, db_length);
						euc_query+= weight_query * weight_query;
						weight_db=weight_value(doc_term_frequency, doc_frequency, db_length);
						//weight_db = weight_value(db_term_id, db_doc_id, db_length, reverse_doc);
						scores[db_doc_id - 1] += weight_db * weight_query;
					}
				}
				
				
				}
			}
			for (int scores_index = 0; scores_index < db_length; scores_index++) {
				euclidean_doc_postlists= forward_doc.get(scores_index + 1);
				euc_tf_value=0;
				for(Entry<Integer,Integer> docpost: euclidean_doc_postlists.entrySet())
				{
					doc_term_frequency= term_frequency(docpost.getKey(), scores_index+1, reverse_doc);
					doc_frequency=doc_frequency(docpost.getKey(),scores_index+1,reverse_doc);
					weight_db= weight_value(doc_term_frequency,doc_frequency,db_length);
					euc_tf_value+= weight_db * weight_db;
					//euc_tf_value+= docpost.getValue() * docpost.getValue();
				}
				euc_tf_value = Math.sqrt(euc_tf_value);
				euc_query= Math.sqrt(euc_query);
				scores[scores_index] = scores[scores_index] / (euc_tf_value * euc_query *10
						);
				if(scores[scores_index] != 0)
				{
					score_fileid.put(scores_index+1,scores[scores_index]);
				}
			}
			for(Entry<String,Integer> querydoc : query_fileDict.entrySet())
			{
				if(querydoc.getValue().equals(querydoc_id))
				{
					query_num = querydoc.getKey(); 
				}
			}
			//System.out.println("quer number: "+ query_num);
			//writer.write("quer number: "+ query_num);
			//writer.newLine();
			//Map<Double,Integer> score_fileid_desc= score_fileid.descendingMap(); 
			sortedscores= entriesSortedByValues(score_fileid);
			for(Entry<Integer,Double> score_entry: sortedscores)
			{
				for(Entry<String,Integer> dbdoc: db_fileDict.entrySet())
				{
					if(dbdoc.getValue().equals(score_entry.getKey()))
					{
						db_doc_name=dbdoc.getKey();
					}
				}
				rank+=1;
				writer.write(query_num + "		"+db_doc_name +"		"+ rank + "		"+ score_entry.getValue());
				writer.newLine();
			}
			/*for(Entry<Double,Integer> nonzero_score: sortedscores.iterator())
			{
				for(Entry<String,Integer> dbdoc: db_fileDict.entrySet())
				{
					
					if(dbdoc.getValue().equals(nonzero_score.getValue()))
					{
						db_doc_name= dbdoc.getKey();
					}
				}
				rank+=1;
				writer.write(query_num + "		"+db_doc_name +"		"+ rank + "		"+ nonzero_score.getKey());
				writer.newLine();
				//System.out.println(db_doc_name +"	"+ nonzero_score.getKey());
			}*/
			
			//System.out.println(scores[0] + " " + scores[1] + " "+ scores[2]);
		}
		writer.flush();
		writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
