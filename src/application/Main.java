package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/** 
 * Main class that starts the application, launching as first the first stage and scene
 * The first stage lets the user input a search key and create a dataset of preprocessed
 * tweets resulting from his query.
 */

public class Main extends Application {
	static Stage primaryStage;
	@Override
	public void start(Stage primaryStage) {
		try {
	        StackPane page = (StackPane) FXMLLoader.load(Main.class.getResource("test2.fxml"));
	        Scene scene = new Scene(page);
	        scene.getStylesheets().add("application/application.css");
	        primaryStage.setScene(scene);
	        primaryStage.setTitle("Sentweet");
	        primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//TODO - sistemare questione smile, editando dataset di partenza (sottocampionare set da 1 mil o togliere smile)
	
	/**
	 * This method starts the second stage. This stage shows the user the evaluated tweets
	 * according to a loaded classification model. This  stage also shows the percentage
	 * of positive and negative tweets of the user dataset
	 * @throws IOException
	 */
	public static void handleShowSecond() throws IOException {
	        // Load the fxml file and create a new stage for the scene.
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(Main.class.getResource("second.fxml"));
	        AnchorPane page = (AnchorPane) loader.load();
	        Stage dialogStage = new Stage();
	        dialogStage.setTitle("Classifier output");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);
	        secondController controller = loader.getController();
	        dialogStage.show();
	        dialogStage.setOnCloseRequest(event -> {
	            System.out.println("Stage closing, clearing tableview");
	            controller.reset();
	        });
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
