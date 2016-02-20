package application;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import model.Context;
import model.Tweet;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import weka.classifiers.*;
import weka.core.Instances;
import weka.core.Utils;

public class simpleController implements Initializable {

	@FXML private Button myButton; // Value injected by FXMLLoader
	@FXML private Button btnClassify;
	@FXML private TextField txtField; 
	@FXML private Label label1;
	@FXML private AnchorPane content;
	String key;
    private ObservableList<Tweet> tweets =  FXCollections.observableArrayList();
	@FXML private void HandleShowSecond() throws IOException {
		try {
		Main.handleShowSecond();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}}
	
	@Override // This method is called by the FXMLLoader when initialization is complete
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
	    assert myButton != null : "fx:id=\"myButton\" was not injected: check your FXML file.";
	    assert btnClassify != null : "fx:id=\"btnClassify\" was not injected: check your FXML file.";
	    assert txtField != null : "fx:id=\"txtField\" was not injected: check your FXML file.";
	    //TODO - classify button that loads second view
	    //first view button
	    myButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
		    	if(txtField.getText().length()==0){
					label1.setTextFill(Color.RED);
				   	label1.setText("Insert a search key");
				}
			   	else {	
			   		label1.setTextFill(Color.BLACK);
			    	btnClassify.setDisable(true);
			    	key = txtField.getText();
			    	System.out.println("Search key: "+key);
			    	//myButton.setDisable(true);
			    	try {
			    		label1.setText("Retrieving tweets...");
						retrieveTweets(key);
					} catch (TwitterException e) {
						e.printStackTrace();
					}
			    	label1.setText("Preprocessed tweets saved to file testTweets.arff");
				    
			    	Context.setTweets(tweets);
			    	btnClassify.setDisable(false);
		    	}
		    }
	    });
	}

	private double[] IterateList(ArrayList<double[]> labeled) {
		double polarity[] = new double[2];
		int pos=0;
		int neg=0;

		for(double[] i : labeled){
			if (i[0] > 0.5f) neg++;
			else pos++;			
		}
		polarity[0] = pos;
		polarity[1] = neg;
		System.out.println("Pos: "+pos+" Neg: "+neg);
		return polarity;
	}
	
	void retrieveTweets(String key) throws TwitterException{ 
		String tmp;
		int i = 0;
		Twitter twitter = new TwitterFactory().getInstance();
		List<String> result = new LinkedList<String>();
		//for (int page = 1; page <= 10; page++) {
			Query query = new Query(key+" +exclude:retweets");
			query.setCount(100); // set tweets per page to 100
			query.setLang("en");
			QueryResult qr = twitter.search(query);
			List<Status> qrTweets = qr.getTweets();
			Preprocessor pp = new Preprocessor();
		//	if(qrTweets.size() == 0) break;
		 		for(Status t : qrTweets) {
		 			System.out.println(t.getText());
		 			tmp = pp.preprocessDocument(t.getText());
		 			tmp = tmp.replaceAll("[\n\r]", "");
		 			System.out.println("\tPreprocessed tweet: "+tmp);
		 			result.add("?,"+Utils.quote(tmp));
		 			label1.setText("Preprocessing tweet # "+i);
		 			Tweet tw = new Tweet(t.getUser().getName(),t.getText(),0);
		 			tweets.add(tw);
		 			i++;
		//		}
			}
	 		try {
				PrintWriter writer = new PrintWriter(new FileWriter("testTweets.arff"));
				ListIterator<String> itr = result.listIterator();
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
	public void preprocessRaw(String path, String outputPath){
		Preprocessor pre = new Preprocessor();
		String strLine = null,str,pol;
		List<String> result = new LinkedList<String>();
		try{
            
            FileInputStream fstream1 = new FileInputStream(path);
            DataInputStream in = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));;
       
            while ((strLine = br.readLine()) != null) {
            	String[] items = strLine.split(";;");
            	str = items[5].toLowerCase();
	    		pol = items[0];
                result.add(pol+","+Utils.quote(pre.preprocessDocument(str)));
            }
            in.close();
        }catch (Exception e){
            System.err.println("Error while parsing the input raw file: " + e.getMessage());
        }
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(outputPath));
			ListIterator itr = result.listIterator();
			String temp;
			writer.print("@relation prova\n\n");
			writer.print("@attribute classPol {0,4}\n");
			writer.print("@attribute text String\n\n");
			writer.print("@data\n");
			while (itr.hasNext()) {
                String element = (String) itr.next();
                temp = element.replaceAll("USERNAME", "");
                writer.print(temp + "\n");    				
            }
			System.out.println("Saved preprocessed dataset "+ outputPath);
			writer.close();
		}
		catch (IOException e) {
			System.out.println("Problem found when writing " + outputPath);
		}
		System.out.println(Arrays.toString(result.toArray()));
			
		System.out.println("Preprocessing completed");
		//TODO - load second scene here
	}
	public void onEnter(){
		if(txtField.getText().length()==0){
			label1.setTextFill(Color.RED);
		   	label1.setText("Insert a search key");
		}
	   	else {	
	   		label1.setTextFill(Color.BLACK);
	    	btnClassify.setDisable(true);
	    	key = txtField.getText();
	    	System.out.println("Search key: "+key);
	    	//myButton.setDisable(true);
	    	try {
	    		label1.setText("Retrieving tweets...");
				retrieveTweets(key);
			} catch (TwitterException e) {
				e.printStackTrace();
			}
	    	label1.setText("Preprocessed tweets saved to file testTweets.arff");
	    	Context.setTweets(tweets);
	    	btnClassify.setDisable(false);
		}

	}
}