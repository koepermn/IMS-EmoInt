import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;

public class CreateArff {

	static HashSet<String> Vocab = new HashSet<String> (); // task Vocab
	static boolean use_CNNLSTM_out = false;
	
	static String outputname="";
	public static void main(String[] args) throws NumberFormatException, IOException {
		
		if(args.length<3){
			System.out.println(""
					+ "1:<Input File parsed,  1 Word per Line>\n"
					+ "2:<Input File Orig, <Tweet TAB Rating>, with Header>\n"
					+ "3:<Ratings File txt or txt.gz>\n"
					+ "4:Optional[CNN-lSTM output, 4 numbers p line: anger fear  joy sadness]\n");
			
		}
	
		String inputfile=args[0];
		String clusterout="";
		String definputfile=args[1];
		String ratingfile=args[2];
		if(args.length==4){
			 clusterout=args[3];
			 use_CNNLSTM_out=true;
		}	
		outputname  = new File(inputfile).getName();

		ArrayList<String> TaggedSent = readPOS(inputfile);
		
		HashMap<Integer,String> IdtoRating = readSent(definputfile);
		ArrayList<String> Clusterout = new ArrayList<String>();
		if(use_CNNLSTM_out){		
			Clusterout= read_cnn_lstm_out(clusterout);
		}
		System.out.println("Check --  Parsed: "+TaggedSent.size()
		+"\nDefault Input:"+IdtoRating.size()
		+"\nCNN-LSTM Output  (Optional - used:"+use_CNNLSTM_out+") Size:"+Clusterout.size());
		/* WORD	 0
		 * AbsConc	 1
		 * Arousal	 2
		 * Valency	 3
		 * Dominance	 4
		 * Anger	5
		 * Anticipation 6
		 * Disgust 7
		 * Fear	 8
		 * Happiness 9
		 * Joy	10
		 * Sadness	11
		 * Surprise 12
		 * 	Trust 13
		 */

	
		HashMap<String,HashMap<String,Double>> AFFECTIVES = new HashMap<String,HashMap<String,Double>>();		
		// the following loads each rating type
		// id is used to refer to the column
		// last value represents median value of an entire column (pre-computed)
		//
		HashMap<String,Double> ratingAbsConc = loadRating(ratingfile,1,4.067);
		AFFECTIVES.put("AbsConc", ratingAbsConc);
		HashMap<String,Double> ratingArousal = loadRating(ratingfile,2,2.346);
		AFFECTIVES.put("Arousal", ratingArousal);
		HashMap<String,Double> ratingValency = loadRating(ratingfile,3,5.276);
		AFFECTIVES.put("Valency", ratingValency);
		HashMap<String,Double> ratingDominance= loadRating(ratingfile,4,5.071);
		AFFECTIVES.put("Dominance", ratingDominance);
		HashMap<String,Double> ratingAnger = loadRating(ratingfile,5,2.892);
		AFFECTIVES.put("Anger", ratingAnger);
		HashMap<String,Double> ratingAnticipation = loadRating(ratingfile,6,3.831);
		AFFECTIVES.put("Anticipation", ratingAnticipation);
		HashMap<String,Double> ratingDisgust = loadRating(ratingfile,7,	2.432);
		AFFECTIVES.put("Disgust", ratingDisgust);
		HashMap<String,Double> ratingFear = loadRating(ratingfile,8,2.785);
		AFFECTIVES.put("Fear", ratingFear);
		HashMap<String,Double> ratingHappiness = loadRating(ratingfile,9,5.29);
		AFFECTIVES.put("Happiness", ratingHappiness);
		HashMap<String,Double> ratingJoy = loadRating(ratingfile,10,2.945);
		AFFECTIVES.put("Joy", ratingJoy);
		HashMap<String,Double> ratingSadness = loadRating(ratingfile,11,2.073);
		AFFECTIVES.put("Sadness", ratingSadness);
		HashMap<String,Double> ratingSurprise = loadRating(ratingfile,12,4.448);
		AFFECTIVES.put("Surpise", ratingSurprise);
		HashMap<String,Double> ratingTrust = loadRating(ratingfile,13,3.464);
		AFFECTIVES.put("Trust", ratingTrust);
		
		DecimalFormat df = new DecimalFormat("##.###");		
		 DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
		 dfs.setDecimalSeparator('.');
		 df.setDecimalFormatSymbols(dfs); 
		
			 Writer out = new BufferedWriter(new OutputStreamWriter(
		    	  		new FileOutputStream(outputname+".arff",false), "UTF8"));
			
			 out.write("@relation arffFile \n");
			 out.write("@attribute score numeric \n");
			 out.write("@attribute id string \n");
			 out.write("@attribute tweet string \n");
			 out.write("@attribute WordCount numeric \n");
			 out.write("@attribute SCREAMER {yes,no} \n");

			for(String norm  : AFFECTIVES.keySet()){
				 out.write("@attribute "+norm+"All numeric \n");
				 out.write("@attribute "+norm+"NN numeric \n");
				 out.write("@attribute "+norm+"Verbs numeric \n");
				 out.write("@attribute "+norm+"ADJ numeric \n");
				 out.write("@attribute "+norm+"HashTags numeric \n"); 
				 out.write("@attribute "+norm+"MAX numeric \n"); 
				 out.write("@attribute "+norm+"STDV numeric \n");
			}
			 
			/*Text CL -- Romans Feat */
			if(use_CNNLSTM_out){
				
			 out.write("@attribute angerINT numeric \n");
			 out.write("@attribute fearINT numeric \n");
			 out.write("@attribute joyINT numeric \n");
			 out.write("@attribute sadnessINT numeric \n");
			}
			 
			out.write("\n@data\n");
			for(int i=0;i<TaggedSent.size();i++){
				
				String id = i+""; 
				String textCLline = "";
				if(use_CNNLSTM_out){
					
				 String cnnlstm_out = Clusterout.get(Integer.parseInt(id));
				 String[] sep = cnnlstm_out.split("\t");
				 for(int q=0;q<sep.length;q++){					
					 textCLline+=","+df.format(Double.parseDouble(sep[q]));					
					
				 }
			
						 //
				}
				String tweet = IdtoRating.get(i).split("\t")[0].replaceAll(",", "");;
				int tweetwordcount = tweet.split("\\s+").length;
				String screamer = "no";
				if(tweet.contains("!")){
					screamer="yes";
				}

			
				String rating =IdtoRating.get(i).split("\t")[1];				
				out.write(rating+",\""+id+"\",\""+tweet+"\","+tweetwordcount+","+screamer);	
				String normline="";
				for(String norm  : AFFECTIVES.keySet()){
					normline+=","+ compFeatline(AFFECTIVES.get(norm),TaggedSent.get(i));
				}
				if(use_CNNLSTM_out){
					normline+=textCLline;
				}
				out.write(normline+"\n");
				
			}
			out.flush();
			out.close();
}
	

		
		private static ArrayList<String> read_cnn_lstm_out(String path) throws NumberFormatException, IOException {
			BufferedReader br = null;
			  InputStreamReader reader = new InputStreamReader(
						new FileInputStream(path), "UTF-8");  
			   br = new BufferedReader(reader);
			   String currline="";					   
			   ArrayList<String> result = new ArrayList<String>();
	
	
			 
			   while ((currline = br.readLine()) != null) {
				   // format of this file should be 
				   // four numbers tab seperated
				   // anger fear joy sadness
				   int check = currline.split("\t").length;
				   if(check!=4){
					   System.out.println("Something wrong with file :\n"+path+
							   "\n Lenght: "+check+"!=4"+"\n Line:"+currline);
				   }
				   if(currline.isEmpty()==false){
						
						result.add(currline);
				   }
			   }
			   
			   br.close();
			return result;
		
	}

		// Extracts POS Specific Features
		private static String compFeatline(HashMap<String, Double> rat, String feat) {			
			ArrayList<Double> memory = new ArrayList<Double>();	 // <- used for avg across all words
			
			double allNN=0; // all nouns
			double allNNdivid=0;
			
			double allV=0; /// all verbs
			double allVdivid=0;
			
			double allADJ=0; // all adj
			double allADJdivid=0;
			
			double allhash =0; // all hashtags
			double hashdivid=0;
			
			double max=0;
			
			String[] line = feat.split(" ");
			for(int j=0;j<line.length;j++){
				String w=line[j].split("\t")[0].trim();
				String pos = line[j].split("\t")[1];
				
				if(rat.containsKey(w)==false){ // in case we dont find word, check if we find word lowercased
					w=w.toLowerCase();
				}
				
				if(rat.containsKey(w)==false){					
					continue;
				}
				double val = rat.get(w);				
				
				memory.add(val);
				max = Math.max(val, max);				
			
				if(pos.equals("N")){
					allNN+= val;
					allNNdivid++;
				}
				if(pos.equals("V")){
					allV+= val;
					allVdivid++;
				}
				if(pos.equals("A")){
					allADJ+= val;
					allADJdivid++;
				}
				if(w.charAt(0)=='#'){
					allhash+= val;
					hashdivid++;
				}
				
				
			}
		
			if(allNNdivid!=0.0){
				allNN = allNN/allNNdivid;
			}
			else {
				allNN=rat.get("MedianVallue"); // <- Median as fall back strategy
			}
			if(allVdivid!=0.0){
				allV = allV/allVdivid;
			}
			else {
				allV=rat.get("MedianVallue");
			}
			if(allADJdivid!=0.0){
				allADJ = allADJ/allADJdivid;
			}
			else {
				allADJ=rat.get("MedianVallue");
			}
			if(hashdivid!=0.0){
				allhash = allhash/hashdivid;
			}
			else {
				allhash=rat.get("MedianVallue");
			}
			
			double[] data = new double[memory.size()];
			for(int i=0;i<memory.size();i++){
				data[i] = memory.get(i);
			}
			mystat tst = new mystat();
			double[] avg = new double[1];
			double[] std = new double[1];
			double[] skw = new double[1];
			double[] sk2 = new double[1];
			double[] kur = new double[1];int ncnt;
			ncnt = data.length;
			tst.mystat(ncnt,data,avg,std,skw,kur);

			double mean = avg[0]; // average all words
			double stdv = std[0];
			
		return mean+","+allNN
				+","+allV
				+","+allADJ
				+","+allhash
				+","+max
				+","+stdv;
	
	}
		


		private static HashMap<String, Double> loadRating(String path, int id,double Median) throws NumberFormatException, IOException {
						
			
			BufferedReader br = null;
			if(path.endsWith(".gz")){
				GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(path));
		     br = new BufferedReader(new InputStreamReader(gzip, "UTF-8"));
			}
			else {
				  InputStreamReader reader = new InputStreamReader(
							new FileInputStream(path), "UTF-8"); 
				 br = new BufferedReader(reader);
			}

		   
		    
		    String currline;			   
			
	
			   HashMap<String, Double> data = new HashMap<String, Double>();
			
			   boolean header=true;
			   while ((currline = br.readLine()) != null) {
				if(header){
					header=false;
					continue;
				}
				
				String word = currline.split("\t")[0].trim(); // TODO adjust this code for tune, test data
			
			
				if(Vocab.contains(word)==false){ // SKIP WORDS THAT ARE NOT IN THE TASK 
					continue;
				}
				double val = Double.parseDouble(currline.split("\t")[id]);	
				data.put(word, val);
				   
			   }
			   br.close();		
			   data.put("MedianVallue", Median); // this token is used as backup strategy
			   // in case we dont match a word we insert the median
			   // the typo is intended. 
			  
			return data;
	}

// This function reads the parsed file
/* Input format should look like this:
So	R
nervous	A
I	O
could	V
puke	V

Just	R
died	V
from	P
laughter	N
after	P
seeing	V
that😂😭😂😭	N*/		
private static ArrayList<String> readPOS(String path) throws IOException {
			BufferedReader br = null;
			if(path.endsWith(".gz")){
				GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(path));
		     br = new BufferedReader(new InputStreamReader(gzip, "UTF-8"));
			}
			else {
				  InputStreamReader reader = new InputStreamReader(
							new FileInputStream(path), "UTF-8"); 
				 br = new BufferedReader(reader);
			}
			String currline="";		
			 ArrayList<String> data = new ArrayList<String>();
			 String neuezeile="";
			   while ((currline = br.readLine()) != null) {				   
				   if(currline.isEmpty()){
					   if(neuezeile.isEmpty()==false){
						   String[] line = neuezeile.split(" ");
							for(int j=0;j<line.length;j++){
								String w=line[j].split("\t")[0];
								   Vocab.add(w.trim()); // Save task vocab
								   Vocab.add(w.toLowerCase().trim());
							}
						   data.add(neuezeile.trim());
						   //System.out.println(neuezeile);
						   neuezeile="";
						   continue;
					   }
				   }				 
				   neuezeile+=currline+" ";
				   
			   }
			   // also add last line
			   br.close();
			   String[] line = neuezeile.split(" ");
				for(int j=0;j<line.length;j++){
					String w=line[j].split("\t")[0];
					   Vocab.add(w.trim()); // Save task vocab
					   Vocab.add(w.toLowerCase().trim());
				}
				   if(neuezeile.isEmpty()==false){
					   data.add(neuezeile.trim());					 
				   }
			
			   
			   br.close();
			return data;
		}
// This function reads default input file, with Gold Rating info
/* Input format should look like this:
Tweet	Rating
So nervous I could puke	0.0
Just died from laughter after seeing that😂😭😂😭	0.0
...
 */
private static HashMap<Integer,String> readSent(String path) throws IOException {
	BufferedReader br = null;
	if(path.endsWith(".gz")){
		GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(path));
     br = new BufferedReader(new InputStreamReader(gzip, "UTF-8"));
	}
	else {
		  InputStreamReader reader = new InputStreamReader(
					new FileInputStream(path), "UTF-8"); 
		 br = new BufferedReader(reader);
	}
	String currline="";		
	HashMap<Integer,String> data = new HashMap<Integer,String>();
	 String neuezeile="";
	 boolean header = true;
	   while ((currline = br.readLine()) != null) {	
		   if(header) { // ignore first line
			   header=false;
			   continue;
		   }
		   data.put(data.size(), currline);
		   
	   }
	   // also add last line
	   br.close();
	
	return data;
}
}
