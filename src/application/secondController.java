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

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
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

public class secondController implements Initializable {

	// Value injected by FXMLLoader
	@FXML private TableView<Tweet> tableView;
	@FXML private TableColumn<Tweet,String> tbUser;
	@FXML private TableColumn<Tweet, String> tbTweet;
	@FXML private PieChart pieChart;
	private ObservableList<Tweet> tweets;

	@Override // This method is called by the FXMLLoader when initialization is complete
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
	    assert tableView != null : "fx:id=\"tableView\" was not injected.";
	    assert pieChart != null : "fx:id=\"pieChart\" was not injected.";
	    tweets = Context.getTweets();
	    tbUser.setCellValueFactory(new PropertyValueFactory<Tweet, String>("user"));
	    tbTweet.setCellValueFactory(new PropertyValueFactory<Tweet, String>("text"));
	    tableView.setItems(tweets);
	    //TODO - replace with actual data
	    ObservableList<PieChart.Data> pieChartData =
	            FXCollections.observableArrayList(
	            new PieChart.Data("Positive", 60),
	            new PieChart.Data("Negative", 45));
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
	}
	
}