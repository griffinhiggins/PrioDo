package application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SettingsController {
	public SettingsController() {
		
	}
	
	@FXML
	protected void initialize() throws SQLException {
		populateItems();
	}
	@FXML public TextField UPPER = new TextField();
	@FXML public TextField LOWER = new TextField();
	private void populateItems() throws SQLException{
		Statement state = Model.con.createStatement();
		try{
			ResultSet rs = state.executeQuery("SELECT * FROM prioritySettings");
			while(rs.next()) {
				UPPER.setText(rs.getString("threshold_1"));
				LOWER.setText(rs.getString("threshold_2"));
			}
		}
		catch (SQLException e) {
		}
	}
	@FXML
	private void submit() throws SQLException {
		Statement state = Model.con.createStatement();
		try{
			state.execute( "UPDATE prioritySettings "
						 + "SET "
						 + "threshold_1 ='"+ Integer.parseInt(UPPER.getText()) + "',"
						 + "threshold_2 ='" + Integer.parseInt(LOWER.getText()) + "'"
			
			);
			populateItems();
		}
		catch (NumberFormatException e) {
		}
		catch (SQLException e) {
			System.out.println(e);
		}
		catch (NullPointerException e) {
			
		}
	}
}
