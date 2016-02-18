package model;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Context {
    private final static Context instance = new Context();
    private static ObservableList<Tweet> tweets = FXCollections.observableArrayList();
    
    public static Context getInstance() {
        return instance;
    }
    
    public void setTweetsRaw(){
    	
    }

	public static void setTweets(ObservableList<Tweet> tweets2) {
		tweets = tweets2;
		// TODO Auto-generated method stub
		
	}

	public static String getTweetsSize() {
		// TODO Auto-generated method stub
		return String.valueOf(tweets.size());
	}

	public static ObservableList<Tweet> getTweets() {
		// TODO Auto-generated method stub
		return tweets;
	}
}
