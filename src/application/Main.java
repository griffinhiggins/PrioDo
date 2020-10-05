package application;
	
import java.sql.SQLException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Main extends Application {
	public static final IndexController CONTROLLER = new IndexController();
	public static Model db;
	public static Scene scene;
	public static BorderPane root;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			root = FXMLLoader.<BorderPane>load(getClass().getResource("indexView.fxml"));
			scene = new Scene(root,850,400);
			scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
			primaryStage.setOnCloseRequest(event -> {
			    db.closeConn();
			});
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		db = new Model();
		db.getConnection();
		launch(args);
	}
	
}
