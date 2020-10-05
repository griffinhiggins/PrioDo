package application;

import java.io.IOException;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class WorkViewLoader {
	
	public WorkViewLoader(BorderPane root) throws SQLException {
		
		root.setCenter(null);
		Pane temp = null;
		try {
			temp = FXMLLoader.<Pane>load(getClass().getResource("workView.fxml"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
        root.setCenter(temp);
        
	}
	
}
