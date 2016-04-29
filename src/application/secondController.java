package application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import javafx.util.Duration;
import model.Context;
import model.Tweet;
import weka.core.Instances;

public class secondController implements Initializable {

	// Values injected by FXMLLoader
	@FXML private TableView<Tweet> tableView;
	@FXML private TableColumn<Tweet,String> tbUser;
	@FXML private TableColumn<Tweet, String> tbTweet;
	@FXML private TableColumn<Tweet, String> tbPol;
	@FXML private PieChart pieChart;
	@FXML private TextField lblPos,lblNeg,lblModel;
	
	//@FXML private ImageView imgLoad;
	private ObservableList<Tweet> tweets;
	ArrayList<double[]> labeled;
	boolean negative;
	double[] CntPolarity;
	//TODO - exit deletes dataset
	
	@Override // This method is called by the FXMLLoader when initialisation is complete
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
	    assert tableView != null : "fx:id=\"tableView\" was not injected.";
	    assert pieChart != null : "fx:id=\"pieChart\" was not injected.";
	    tweets = Context.getTweets();
	    tbUser.setCellValueFactory(new PropertyValueFactory<Tweet, String>("user"));
	    tbTweet.setCellValueFactory(new PropertyValueFactory<Tweet, String>("text"));
	    tbPol.setCellValueFactory(new PropertyValueFactory<Tweet, String>("polarity"));

	    CntPolarity = new double[2];
	    FilteredClassifierBuiler fcb = new FilteredClassifierBuiler();
	    try {
			labeled = fcb.classifyInstances();
			CntPolarity = IterateList(labeled);
			
			lblPos.setText((int)CntPolarity[0]+" / "+ tweets.size());
			lblNeg.setText((int)CntPolarity[1]+" / "+tweets.size());
			lblModel.setText("NaiveBayesianMultinomial");
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
	    //"dirty" cycle to insert labels of the tweets in the observablelist containing the raw ones
	    int i =0;
	    if (!tweets.isEmpty()){ //&&tweets.size() == 100
				try {
					Instances label = new Instances( new BufferedReader( new FileReader("labeled.arff")));	    	
					for(Tweet t:tweets){
						if (label.instance(i).value(0)==1.0) t.setPolarity(4);
						else if (label.instance(i).value(0) == 0.0) t.setPolarity(0);
						i++;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    }
	    
	    tableView.setItems(tweets);
	    ObservableList<PieChart.Data> pieChartData =
	            FXCollections.observableArrayList(
	            new PieChart.Data("Positive", CntPolarity[0]),
	            new PieChart.Data("Negative", CntPolarity[1]));
	    pieChart.setData(pieChartData);
	    for(PieChart.Data d : pieChartData){
	    	d.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
	            Bounds b1 = d.getNode().getBoundsInLocal();
	            double newX = (b1.getWidth()) / 2 + b1.getMinX();
	            double newY = (b1.getHeight()) / 2 + b1.getMinY();
	            // Make sure pie wedge location is reset
	            d.getNode().setTranslateX(0);
	            d.getNode().setTranslateY(0);
	            TranslateTransition tt = new TranslateTransition(
	                    Duration.millis(700), d.getNode());
	            tt.setByX(newX);
	            tt.setByY(newY);
	            tt.setAutoReverse(true);
	            tt.setCycleCount(2);
	            tt.play();
	        });
	    }
	    //imgLoad.setVisible(false);
	}
	
	private int roundPolarity(double[] ds) {
		int polarity;
		if (ds[0]>=0.5) polarity = 0;
		else polarity = 4;
		return polarity;
	}

	public void reset(){
		tweets.clear();
		Context.setTweets(null);
		this.CntPolarity = null;
		this.CntPolarity = new double[2];
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
	
}