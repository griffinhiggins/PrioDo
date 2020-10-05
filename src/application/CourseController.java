package application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class CourseController {
	public CourseController() {
		
	}
	ObservableList<String> COURSELISTData = FXCollections.observableArrayList ();
	@FXML public ListView<String> COURSELIST = new ListView<String>();	
	ObservableList<PieChart.Data> PIECHARTData = FXCollections.observableArrayList();
	@FXML public PieChart PIECHART = new PieChart();
	@FXML
	protected void initialize() throws SQLException {
		populateItems();
		PIECHART.setLegendVisible(false);
		COURSELIST.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		        try {
					fill(newValue);
				} 
		        catch (SQLException e) {
				}
		    }
		});
	}
	private void populateItems() throws SQLException{
		Statement state = Model.con.createStatement();
		try{
			ResultSet rs = state.executeQuery("SELECT course_name FROM course");
			COURSELISTData.clear();
			while(rs.next()) {
				COURSELISTData.add(rs.getString("course_name"));
			}
			COURSELIST.setItems(COURSELISTData);
		}
		catch (SQLException e) {
		}
	}
	@FXML public TextField NAME = new TextField();
	@FXML public TextField ASSIGNMENT = new TextField();
	@FXML public TextField LAB = new TextField();
	@FXML public TextField FINAL = new TextField();
	@FXML public TextField MIDTERM = new TextField();
	@FXML public TextField QUIZ = new TextField();
	@FXML public TextField TERMPAPER = new TextField();
	@FXML public TextField PROJECT = new TextField();
	@FXML public TextField ATTENDANCE = new TextField();
	@FXML
	private void submit() throws SQLException {
		Statement state = Model.con.createStatement();
		try{
			ResultSet rs = state.executeQuery("SELECT * FROM course WHERE course_name = '" + NAME.getText() + "' LIMIT 1");
			String sql;
			if(!rs.next()){	
					sql = "INSERT INTO course("
						+ "course_name,"
						+ "assignment_weight,"
						+ "lab_weight,"
						+ "quiz_weight,"
						+ "midterm_weight,"
						+ "final_weight,"
						+ "paper_weight,"
						+ "project_weight,"
						+ "attendance_weight)"
						+ "VALUES("
						+ "'" + NAME.getText() + "',"
						+ "'" + Integer.parseInt(ASSIGNMENT.getText()) + "',"
						+ "'" + Integer.parseInt(LAB.getText()) + "',"
						+ "'" + Integer.parseInt(QUIZ.getText()) + "',"
						+ "'" + Integer.parseInt(MIDTERM.getText()) + "',"
						+ "'" + Integer.parseInt(FINAL.getText()) + "',"
						+ "'" + Integer.parseInt(TERMPAPER.getText()) + "',"
						+ "'" + Integer.parseInt(PROJECT.getText()) + "',"
						+ "'" + Integer.parseInt(ATTENDANCE.getText()) + "'"
						+ ")";
			}
			else{
					sql = "UPDATE course "
						+ "SET "
						+ "course_name ='"+ NAME.getText() + "',"
						+ "assignment_weight ='" + Integer.parseInt(ASSIGNMENT.getText()) + "',"
						+ "lab_weight ='" + Integer.parseInt(LAB.getText()) + "',"
						+ "quiz_weight ='" + Integer.parseInt(QUIZ.getText()) + "',"
						+ "midterm_weight ='"+ Integer.parseInt(MIDTERM.getText()) + "',"
						+ "final_weight ='"+ Integer.parseInt(FINAL.getText()) + "',"
						+ "paper_weight ='"+ Integer.parseInt(TERMPAPER.getText()) + "',"
						+ "project_weight ='"+ Integer.parseInt(PROJECT.getText()) + "',"
						+ "attendance_weight ='"+ Integer.parseInt(ATTENDANCE.getText()) + "' "
						+ "WHERE course_id =" + Integer.parseInt(rs.getString("course_id"));
			}
			state.execute(sql);
			clear();
			rs.close();
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
	@FXML
	private void clear() throws SQLException {
		NAME.setText("");
		ASSIGNMENT.setText("");
		LAB.setText("");
		FINAL.setText("");
		MIDTERM.setText("");
		QUIZ.setText("");
		TERMPAPER.setText("");
		PROJECT.setText("");
		ATTENDANCE.setText("");
		PIECHARTData.clear();
		PIECHART.setTitle("");
	}
	@FXML
	private void delete() throws SQLException {
		Statement state = Model.con.createStatement();
		Statement state1 = Model.con.createStatement();
		
		try{
			ResultSet rs = state.executeQuery("SELECT * FROM course "
					+ "INNER JOIN work ON work.course_id=course.course_id "
					+ "INNER JOIN priority ON work.work_id=priority.work_id "
					+ "WHERE course_name = '" + NAME.getText() + "' LIMIT 1");
			if(rs.next()) {
				state1.execute("DELETE FROM course WHERE course_id ='" + rs.getString("course_id") + "'");
				state1.execute("DELETE FROM work WHERE work_id ='" + rs.getString("work_id") + "')");
				state1.execute("DELETE FROM priority WHERE priority_id ='" + rs.getString("priority_id") + "'");
			}
			else {
				rs = state.executeQuery("SELECT * FROM course WHERE course_name = '" + NAME.getText() + "' LIMIT 1");
				if(rs.next()) {
					state1.execute("DELETE FROM course WHERE EXISTS (SELECT * FROM course WHERE course_id ='" + rs.getString("course_id") + "'");
				}
			}

			clear();
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
	private void fill(String query) throws SQLException{
		Statement state = Model.con.createStatement();
		try{
			ResultSet rs = state.executeQuery("SELECT * FROM course WHERE course_name = '" + query + "' LIMIT 1");
			if(rs.next()){
				NAME.setText(rs.getString("course_name"));
				ASSIGNMENT.setText(rs.getString("assignment_weight"));
				LAB.setText(rs.getString("lab_weight"));
				FINAL.setText(rs.getString("final_weight"));
				MIDTERM.setText(rs.getString("midterm_weight"));
				QUIZ.setText(rs.getString("quiz_weight"));
				TERMPAPER.setText(rs.getString("paper_weight"));
				PROJECT.setText(rs.getString("project_weight"));
				ATTENDANCE.setText(rs.getString("attendance_weight"));
				PIECHARTData.clear();
				if(Integer.parseInt(rs.getString("assignment_weight")) > 0 ) {
					PIECHARTData.add(new PieChart.Data("Assignments", Integer.parseInt(rs.getString("assignment_weight"))));
				}
				if(Integer.parseInt(rs.getString("lab_weight")) > 0 ) {
					PIECHARTData.add(new PieChart.Data("Labs", Integer.parseInt(rs.getString("lab_weight"))));
				}
				if(Integer.parseInt(rs.getString("final_weight")) > 0 ) {
					PIECHARTData.add(new PieChart.Data("Final", Integer.parseInt(rs.getString("final_weight"))));
				}
				if(Integer.parseInt(rs.getString("midterm_weight")) > 0 ) {
					PIECHARTData.add(new PieChart.Data("Midterm", Integer.parseInt(rs.getString("midterm_weight"))));
				}
				if(Integer.parseInt(rs.getString("quiz_weight")) > 0 ) {
					PIECHARTData.add(new PieChart.Data("Quizs", Integer.parseInt(rs.getString("quiz_weight"))));
				}
				if(Integer.parseInt(rs.getString("paper_weight")) > 0 ) {
					PIECHARTData.add(new PieChart.Data("Term Paper", Integer.parseInt(rs.getString("paper_weight"))));
				}
				if(Integer.parseInt(rs.getString("project_weight")) > 0 ) {
					PIECHARTData.add(new PieChart.Data("Projects", Integer.parseInt(rs.getString("project_weight"))));
				}
				if(Integer.parseInt(rs.getString("attendance_weight")) > 0 ) {
					PIECHARTData.add(new PieChart.Data("Attendance", Integer.parseInt(rs.getString("attendance_weight"))));
				}
				PIECHART.setData(PIECHARTData);
				PIECHART.setTitle(rs.getString("course_name"));
			}
		}
		catch (NumberFormatException e) {}
		catch (SQLException e) {
			System.out.println(e);
		}
		catch (NullPointerException e) {}
	}
}
