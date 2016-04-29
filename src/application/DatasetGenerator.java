package application;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import model.Tweet;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import weka.core.Utils;

public class DatasetGenerator {
	
	public static void main(String[] args) throws Exception {
		retrieveTweets(":(");
	}
	
	static void retrieveTweets(String key){
	int numberOfTweets = 10000;
	Twitter twitter = new TwitterFactory().getInstance();
	List<String> resultPP = new LinkedList<String>();
	String tmp;
	Query query = new Query(key+" +exclude:retweets");
	query.setLang("en");
	long lastID = Long.MAX_VALUE;
	ArrayList<Status> tweets = new ArrayList<Status>();
	  while (tweets.size () < numberOfTweets) {
		    if (numberOfTweets - tweets.size() > 100)
		      query.setCount(100);
		    else 
		      query.setCount(numberOfTweets - tweets.size());
		    try {
		      QueryResult result = twitter.search(query);
		      tweets.addAll(result.getTweets());
		      System.out.println("Gathered " + tweets.size() + " tweets");
		      for (Status t: tweets) 
		        if(t.getId() < lastID) lastID = t.getId();

		    }   catch (TwitterException te) {
		        System.out.println("Couldn't connect: " + te);
		    }; 
		    query.setMaxId(lastID-1);
		  }

		Preprocessor pp = new Preprocessor();
 		for(Status t : tweets) {
 			System.out.println(t.getText());
 			tmp = pp.preprocessDocumentKeepSmiles(t.getText());
 			tmp = tmp.replaceAll("[\n\r]", "");
 			System.out.println("\tPreprocessed tweet: "+tmp);
 			resultPP.add("?,"+Utils.quote(tmp));
		}
 		try {
			PrintWriter writer = new PrintWriter(new FileWriter("testTweets10000neg.arff"));
			ListIterator<String> itr = resultPP.listIterator();
			writer.print("@relation toclassify\n\n");
			writer.print("@attribute classPol {0,4}\n");
			writer.print("@attribute text String\n\n");
			writer.print("@data\n");
			while (itr.hasNext()) {
                String element = (String) itr.next();
                writer.print(element + "\n");    				
            }
			System.out.println("Saved preprocessed dataset test.arff");
			writer.close();
		}
		catch (IOException e) {
			System.out.println("Problem found when writing test.arff");
		}
	}
}
