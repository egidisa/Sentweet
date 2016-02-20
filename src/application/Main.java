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


public class Main extends Application {
	static Stage primaryStage;
	@Override
	public void start(Stage primaryStage) {
		try {
	        StackPane page = (StackPane) FXMLLoader.load(Main.class.getResource("test2.fxml"));
	        //AnchorPane page2 = (AnchorPane) FXMLLoader.load(Main.class.getResource("second.fxml"));
	        Scene scene = new Scene(page);
	        scene.getStylesheets().add("application/application.css");
	        primaryStage.setScene(scene);
	        primaryStage.setTitle("Sentweet");
	        primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void handleShowSecond() throws IOException {
	        // Load the fxml file and create a new stage for the popup.
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(Main.class.getResource("second.fxml"));
	        AnchorPane page = (AnchorPane) loader.load();
	        Stage dialogStage = new Stage();
	        dialogStage.setTitle("Classifier output");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);

	        // Set the persons into the controller.
	        secondController controller = loader.getController();
	        dialogStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
