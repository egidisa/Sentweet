package application;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import util.Options;
import weka.core.Instances;

/**
 * class that preprocess a tweet
 */
public class Preprocessor {
	
	private Options _opt;
	
	/**
	 * 
	 * @param opt the preprocessing options
	 */
	public Preprocessor(Options opt) {
		this._opt = opt;
	}
	/**
	 * Default constructor
	 */
	public Preprocessor() {
		this._opt = new Options();
	}
	
	/**
	 * 
	 * @param item the tweet to modify
	 * @return the tweet with "USERNAME" in place of terms that start with "@" 
	 */
	public String removeUsername(String item) {
		String result = "";
		if(item.startsWith("@"))
			result =  "USERNAME";
		else
			result = item;
		return result;
	}
	
	/**
	 * 
	 * @param item the tweet to modify
	 * @return the tweet with "URL" in place of the terms that start with "http://" or "www."
	 */
	public String removeUrl(String item) {
		String result = "";
		if(item.startsWith("http://") || item.startsWith("www.")||item.startsWith("https://"))
			result = "URL";
		else
			result = item;
		return result;
	}
	
	/**
	 * 
	 * @param item the tweet to modify
	 * @return the tweet with "_SMILEHAPPY_" in place of happy emoticons and "_SMILESAD_" in place of sad emticons
	 */
	public String replaceEmoticons(String item) {
		String result = item;
		result = result.replaceAll(":[)]", "_SMILEHAPPY_");
		result = result.replaceAll(";[)]", "_SMILEHAPPY_");
		result = result.replaceAll(":-[)]", "_SMILEHAPPY_");
		result = result.replaceAll(";-[)]", "_SMILEHAPPY_");
		result = result.replaceAll(":d", "_SMILEHAPPY_");
		result = result.replaceAll(";d", "_SMILEHAPPY_");
		result = result.replaceAll("=[)]", "_SMILEHAPPY_");
		result = result.replaceAll("\\^_\\^", "_SMILEHAPPY_");
		result = result.replaceAll(":[(]", "_SMILESAD_");
		result = result.replaceAll(":-[(]", "_SMILESAD_");
		return result;
	}
	
	/**
	 * 
	 * @param item the tweet to modify
	 * @return the tweet without any emoticon
	 */
	public String removeEmoticons(String item) {
		String result = item;
		result = result.replaceAll(":[)]", "");
		result = result.replaceAll(";[)]", "");
		result = result.replaceAll(":-[)]", "");
		result = result.replaceAll(";-[)]", "");
		result = result.replaceAll(":d", "");
		result = result.replaceAll(";d", "");
		result = result.replaceAll("=[)]", "");
		result = result.replaceAll("\\^_\\^", "");
		result = result.replaceAll(":[(]", "");
		result = result.replaceAll(":-[(]", "");
		return result;
	}
	
	
	/**
	 * 
	 * @param item the tweet to modify
	 * @return the tweet without characters that are repeated more than three times
	 */
	public String removeRepeatedCharacters(String item) {
		String result = item;
		result = item.replaceAll("(.)\\1{2,100}", "$1$1");
		return result;
	}
	
	/**
	 * 
	 * @param item the tweet to modify
	 * @return the tweet with symbols in place of their codes
	 */
	public String removeSymbols(String item) {
		String result = item;
		result = result.replaceAll("&quot;", "\"");
		result = result.replaceAll("&amp;", "&"); 
		result = result.replaceAll("&lt;", "<");
		result = result.replaceAll("&gt;", ">");
		//test symbols 
		//result = result.replaceAll("[|]", "");
		//result = result.replaceAll("[+]", "");
		//result = result.replaceAll("[--]","");
		return result;
	}
	
	public String removeNumbers(String item) {
		String result = item;
		result = result.replaceAll("[0-9]", "");
		return result;
	}
	
	/**
	 * 
	 * @param item the tweet to modify
	 * @return the tweet with "STRLAUGH" in place of strings that represent a laugh
	 */
	public String recognizeLaugh(String item) {
		String result = item;
		if(result.contains("haha"))
			result = "STRLAUGH";
		else if(result.contains("ahah")) 
			result = "STRLAUGH";
		return result;
	}
	
	/**
	 * 
	 * @param item the tweet to preprocess
	 * @return the tweet after pre-processing
	 */
	public String preprocessDocument(String item) {
		String result_fin = "";
		String result = item;
		StringTokenizer st1 = new StringTokenizer(result, " ,?![]");
		while (st1.hasMoreTokens()) {
			String str = st1.nextToken();
			result = removeUrl(str);
			if(!_opt.isRemoveEmoticons())
				result = replaceEmoticons(result);
			else
				result = removeEmoticons(result);
			StringTokenizer st2 = new StringTokenizer(result, ".:#)(_");
			String tmp = "";
			String tmp2 = "";
			while (st2.hasMoreTokens()) {
				tmp = st2.nextToken();
				tmp = recognizeLaugh(tmp);
				tmp2 = tmp2 + " " + removeUsername(tmp); 
			}
			result = tmp2;
			result = result.replaceAll("lu+v+", "love");
			result = removeRepeatedCharacters(result);
			result = removeSymbols(result);
			result = removeNumbers(result);
			result_fin = result_fin + result;
		}
		//System.out.println("Proprocessed: "+result);
		return result_fin;
	}
	
	public String preprocessDocumentKeepSmiles(String item) {
		String result_fin = "";
		String result = item;
		StringTokenizer st1 = new StringTokenizer(result, " ,?![]");
		while (st1.hasMoreTokens()) {
			String str = st1.nextToken();
			result = removeUrl(str);
			result = replaceEmoticons(result);
			StringTokenizer st2 = new StringTokenizer(result, ".:#)(_");
			String tmp = "";
			String tmp2 = "";
			while (st2.hasMoreTokens()) {
				tmp = st2.nextToken();
				tmp = recognizeLaugh(tmp);
				tmp2 = tmp2 + " " + removeUsername(tmp); 
			}
			result = tmp2;
			result = result.replaceAll("lu+v+", "love");
			result = removeRepeatedCharacters(result);
			result = removeSymbols(result);
			result_fin = result_fin + result;
		}
		return result_fin;
	}
	
	public void saveARFF(String fileName, Instances outputInstances) {
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(fileName));
			writer.print(outputInstances);
			System.out.println("Saved dataset: " + fileName );
			writer.close();
		}
		catch (IOException e) {
			System.out.println("Problem found when writing: " + fileName);
		}
	}
}


