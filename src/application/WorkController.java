package application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class WorkController {
	public WorkController() {
	}
	ObservableList<String> WORKLISTData = FXCollections.observableArrayList ();
	@FXML public ListView<String> WORKLIST = new ListView<String>();
	@FXML public TextField NAME = new TextField();
	@FXML public ChoiceBox<String> WORKTYPE = new ChoiceBox<String>();
	@FXML public DatePicker DUEDATE = new DatePicker();
	@FXML public ChoiceBox<Integer> DIFFICULTY = new ChoiceBox<Integer>();
	@FXML public ChoiceBox<String> COURSE = new ChoiceBox<String>();
	@FXML
	protected void initialize() throws SQLException {
		populateItems();
		WORKLIST.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		        try {
					fill(newValue);
				} 
		        catch (SQLException e) { System.out.println(e + "WorkController");
				}
		    }
		});
	}
	private void fill(String query) throws SQLException{
		Statement state = Model.con.createStatement();
		try{
			ResultSet rs = state.executeQuery("SELECT * FROM work "
											+ "INNER JOIN workType ON work.work_type_id=workType.work_type_id "
											+ "INNER JOIN course ON work.course_id=course.course_id "
											+ "WHERE work.work_name = '" + query + "' LIMIT 1");
			if(rs.next()){
				NAME.setText(rs.getString("work_name"));
				WORKTYPE.setValue(rs.getString("work_type_name"));
				COURSE.setValue(rs.getString("course_name"));
				DUEDATE.setValue(LocalDate.parse(rs.getString("due_date")));
				DIFFICULTY.setValue(Integer.parseInt(rs.getString("work_difficulty")));
			}
		}
		catch (NumberFormatException e) {}
		catch (SQLException e) { System.out.println(e + "WorkController");}
		catch (NullPointerException e) {}
	}
	private void populateItems() throws SQLException{
		Statement state = Model.con.createStatement();
		//COULD HARD CODE THIS IF IT GETS TO SLOW
		try{
			ResultSet rs = state.executeQuery("SELECT work_type_name FROM workType");
			WORKTYPE.getItems().clear();
			while(rs.next()) {
				WORKTYPE.getItems().add(rs.getString("work_type_name"));
			}
		}
		catch (SQLException e) { System.out.println(e + "WorkController");
		}
		
		//COULD HARD CODE THIS IF IT GETS TO SLOW
		try{
			ResultSet rs = state.executeQuery("SELECT course_name FROM course");
			COURSE.getItems().clear();
			while(rs.next()) {
				COURSE.getItems().add(rs.getString("course_name"));
			}
		}
		catch (SQLException e) { System.out.println(e + "WorkController");
		}
		DIFFICULTY.getItems().clear();
		for(int i=0; i < 10;i++) {
			DIFFICULTY.getItems().add(i);
		}
		
		//COULD HARD CODE THIS IF IT GETS TO SLOW
		try{
			ResultSet rs = state.executeQuery("SELECT * FROM work");
			WORKLISTData.clear();
			while(rs.next()) {
				WORKLISTData.add(rs.getString("work_name"));
			}
			WORKLIST.setItems(WORKLISTData);
		}
		catch (SQLException e) { System.out.println(e + "WorkController");}
		
	}
	@FXML
	private void submit() throws SQLException {
		Statement state = Model.con.createStatement();
		Statement state1 = Model.con.createStatement();
		Statement state2 = Model.con.createStatement();
		
		try{
			ResultSet rs = state.executeQuery("SELECT * FROM work WHERE work_name = '" + NAME.getText() + "' LIMIT 1");
			ResultSet rs1 = state1.executeQuery("SELECT * FROM course WHERE course_name = '" + COURSE.getValue() + "' LIMIT 1");
			ResultSet rs2 = state2.executeQuery("SELECT * FROM workType WHERE work_type_name = '" + WORKTYPE.getValue() + "' LIMIT 1");
			String sql;
			if(!rs.next()){
				rs1.next();
				rs2.next();
					sql = "INSERT INTO work("
						+ "work_type_id,"
						+ "work_name,"
						+ "course_id,"
						+ "due_date,"
						+ "work_difficulty"
						+ ")"
						+ "VALUES("
						+ "'" + rs2.getString("work_type_id") + "',"
						+ "'" + NAME.getText() + "',"
						+ "'" + rs1.getString("course_id") + "',"
						+ "'" + DUEDATE.getValue() + "',"
						+  "'" + DIFFICULTY.getValue() + "' "
						+ ")";
					
			}
			else{
				rs1.next();
				rs2.next();
					sql = "UPDATE work "
						+ "SET "
						+ "work_type_id ='" + rs2.getString("work_type_id") + "',"
						+ "work_name ='" + NAME.getText() + "',"
						+ "course_id ='" + rs1.getString("course_id") + "',"
						+ "due_date ='" + DUEDATE.getValue() + "',"
						+ "work_difficulty ='" + DIFFICULTY.getValue() + "' "
						+ "WHERE work_id =" + Integer.parseInt(rs.getString("work_id"));
			}
			state.execute(sql);
			clear();
			rs.close();
			rs1.close();
			rs2.close();
			populateItems();
		}
		catch (NumberFormatException e) {}
		catch (SQLException e) { System.out.println(e + "WorkController");}
		catch (NullPointerException e) {}
	}
	@FXML
	private void clear(){
		NAME.setText("");
		WORKTYPE.getSelectionModel().clearSelection();
		DIFFICULTY.getSelectionModel().clearSelection();
		COURSE.getSelectionModel().clearSelection();
		DUEDATE.setValue(null);
	}
	@FXML
	private void delete() throws SQLException {
		Statement state = Model.con.createStatement();
		Statement state1 = Model.con.createStatement();
		try{
			ResultSet rs = state.executeQuery("SELECT * FROM work WHERE work_name = '" + NAME.getText() + "' LIMIT 1");
			state1.execute("DELETE FROM work WHERE work_name ='" + NAME.getText() + "'");
			state1.execute("DELETE FROM priority WHERE work_id ='" + rs.getString("work_id") + "'");
			clear();
			populateItems();
		}
		catch (NumberFormatException e) {
		}
		catch (SQLException e) { 
			System.out.println(e + "WorkController");
		}
		catch (NullPointerException e) {
		}
	}
}
