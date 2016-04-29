package model;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Context {
    private final static Context instance = new Context();
    private static ObservableList<Tweet> tweets = FXCollections.observableArrayList();
    private static double[] PolarityCount = new double[2];
    
    public static Context getInstance() {
        return instance;
    }
    
    public void setTweetsRaw(){
    	
    }
    
	public static void setTweets(ObservableList<Tweet> tweets2) {
		tweets = tweets2;		
	}

	public static String getTweetsSize() {
		return String.valueOf(tweets.size());
	}

	public static ObservableList<Tweet> getTweets() {
		return tweets;
	}

	public double[] getPolarityCount() {
		return PolarityCount;
	}

	public static void setPolarityCount(double[] polarityCount) {
		PolarityCount = polarityCount;
	}
}
