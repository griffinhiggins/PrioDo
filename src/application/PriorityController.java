package application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;

public class PriorityController {
	public PriorityController() {
		
	}
	ObservableList<String> PRIORITY1LISTData = FXCollections.observableArrayList ();
	@FXML public ListView<String> PRIORITY1LIST = new ListView<String>();	
	ObservableList<String> PRIORITY2LISTData = FXCollections.observableArrayList ();
	@FXML public ListView<String> PRIORITY2LIST = new ListView<String>();
	ObservableList<String> PRIORITY3LISTData = FXCollections.observableArrayList ();
	@FXML public ListView<String> PRIORITY3LIST = new ListView<String>();	
	@FXML
	protected void initialize() throws SQLException {
		initSettings();
		prioritize();
		populateItems(1,PRIORITY1LIST,PRIORITY1LISTData);
		populateItems(2,PRIORITY2LIST,PRIORITY2LISTData);
		populateItems(3,PRIORITY3LIST,PRIORITY3LISTData);
	}
	private void initSettings() throws SQLException {
		Statement state = Model.con.createStatement();
		try{
			ResultSet rs = state.executeQuery("SELECT * FROM priorityViewSettings LIMIT 1");
			rs.next();
			if(Integer.parseInt(rs.getString("score")) == 1) {
				SCORE.setSelected(true);
			}
			if(Integer.parseInt(rs.getString("days")) == 1) {
				DAYS.setSelected(true);
			}
			if(Integer.parseInt(rs.getString("course")) == 1) {
				COURSE.setSelected(true);
			}
			if(Integer.parseInt(rs.getString("weight")) == 1) {
				WEIGHT.setSelected(true);
			}
			if(Integer.parseInt(rs.getString("difficulty")) == 1) {
				DIFFICULTY.setSelected(true);
			}
		}
		catch (SQLException e) {System.out.println(e + "LMAOGG");}
	}
	private void prioritize() throws SQLException{
		Statement state = Model.con.createStatement();
		Statement state2 = Model.con.createStatement();
		Statement state3 = Model.con.createStatement();
		Statement state4 = Model.con.createStatement();
		try{
			
			ResultSet rs = state.executeQuery("SELECT * "
											+ ",CASE "
											+ "WHEN work.work_type_id = 1 THEN course.assignment_weight "
											+ "WHEN work.work_type_id = 2 THEN course.lab_weight "
											+ "WHEN work.work_type_id = 3 THEN course.quiz_weight "
											+ "WHEN work.work_type_id = 4 THEN course.midterm_weight "
											+ "WHEN work.work_type_id = 5 THEN course.final_weight "
											+ "WHEN work.work_type_id = 6 THEN course.paper_weight "
											+ "WHEN work.work_type_id = 7 THEN course.project_weight "
											+ "WHEN work.work_type_id = 8 THEN course.attendance_weight "
											+ "END weight " 
											+ "FROM work "
											+ "INNER JOIN workType ON workType.work_type_id=work.work_type_id "
											+ "INNER JOIN course ON course.course_id=work.course_id ");
			ResultSet rs4 = state4.executeQuery("SELECT * FROM prioritySettings LIMIT 1");
			String sql;
			int upper = Integer.parseInt(rs4.getString("threshold_1"));
			int lower = Integer.parseInt(rs4.getString("threshold_2"));
			while(rs.next()) {
				
				int weight = Integer.parseInt(rs.getString("weight"));
				Long range = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(rs.getString("due_date")));
				int priority_score = (int) ((Math.floor(Integer.parseInt(rs.getString("work_difficulty"))*100)/Math.floor(range)))*weight;
				int group;
				if(priority_score >= upper) {
					group = 1;
				}
				else if(priority_score < upper && priority_score > lower) {
					group = 2;
				}
				else {
					group = 3;
				}
				ResultSet rs2 = state2.executeQuery("SELECT * FROM priority WHERE work_id = '" + rs.getString("work_id") + "' LIMIT 1");
				if(!rs2.next()) {
					sql = "INSERT INTO priority("
							+ "work_id,"
							+ "priority_score,"
							+ "priority_group,"
							+ "priority_days"
							+ ")"
							+ "VALUES("
							+ "'" + rs.getString("work_id") + "',"
							+ "'" + priority_score + "',"
							+ "'" + group + "',"
							+ "'" + range + "'"
							+ ")";
				}
				else {
					sql = "UPDATE priority "
						+ "SET "
						+ "work_id ='" + rs.getString("work_id") +  "',"
						+ "priority_score ='" + priority_score + "',"
						+ "priority_group ='" + group + "',"
						+ "priority_days ='" + range + "' "
						+ "WHERE priority_id =" + Integer.parseInt(rs2.getString("priority_id"));
				}
				state3.execute(sql);
			}
		}
		catch (SQLException e) {System.out.println(e + "LMAO");}
	}
	private void populateItems(int priority, ListView<String> LIST, ObservableList<String> DATA) throws SQLException{
		Statement state = Model.con.createStatement();
		Statement state2 = Model.con.createStatement();
		try{
			ResultSet rs = state.executeQuery("SELECT * FROM priorityViewSettings");
			ResultSet rs2 = state2.executeQuery("SELECT *"
											+ ",CASE "
											+ "WHEN work.work_type_id = 1 THEN course.assignment_weight "
											+ "WHEN work.work_type_id = 2 THEN course.lab_weight "
											+ "WHEN work.work_type_id = 3 THEN course.quiz_weight "
											+ "WHEN work.work_type_id = 4 THEN course.midterm_weight "
											+ "WHEN work.work_type_id = 5 THEN course.final_weight "
											+ "WHEN work.work_type_id = 6 THEN course.paper_weight "
											+ "WHEN work.work_type_id = 7 THEN course.project_weight "
											+ "WHEN work.work_type_id = 8 THEN course.attendance_weight "
											+ "END weight "
											+ "FROM priority "
											+ "INNER JOIN work ON work.work_id=priority.work_id "
											+ "INNER JOIN workType ON workType.work_type_id=work.work_type_id "
											+ "INNER JOIN course ON course.course_id=work.course_id "
											+ "WHERE priority_group='" + priority + "' ORDER BY priority_score DESC");
			DATA.clear();
			int i = 1;
			rs.next();
			while(rs2.next()) {
				String str = i + ".";
				if(Integer.parseInt(rs.getString("course")) == 1) {
					str += rs2.getString("course_name") + " - ";
				}
				
				str += rs2.getString("work_name");
				
				if(Integer.parseInt(rs.getString("score")) == 1) {
					str += " [score: " + rs2.getString("priority_score") + "]";
				}
				if(Integer.parseInt(rs.getString("days")) == 1) {
					str += " [days: " + rs2.getString("priority_days") + "]";
				}
				if(Integer.parseInt(rs.getString("difficulty")) == 1) {
					str += " [difficulty: " + rs2.getString("work_difficulty") + "]";
				}
				if(Integer.parseInt(rs.getString("weight")) == 1) {
					str += " [weight: " + rs2.getString("weight") + "]";
				}
				DATA.add(str);
				i++;
			}
			LIST.setItems(DATA);
			rs.close();
			rs2.close();
		}
		catch (SQLException e) {System.out.println(e + " DSFAS");}
	}
	@FXML CheckBox SCORE = new CheckBox();
	@FXML CheckBox DAYS = new CheckBox();
	@FXML CheckBox COURSE = new CheckBox();
	@FXML CheckBox WEIGHT = new CheckBox();
	@FXML CheckBox DIFFICULTY = new CheckBox();
	@FXML
	private void submitSettings() {
		int score = 0;
		int days = 0;
		int course = 0;
		int weight = 0;
		int difficulty = 0;
		if(SCORE.isSelected()) {
			score = 1;
		}
		if(DAYS.isSelected()) {
			days = 1;
		}
		if(COURSE.isSelected()) {
			course = 1;
		}
		if(WEIGHT.isSelected()) {
			weight = 1;
		}
		if(DIFFICULTY.isSelected()) {
			difficulty = 1;
		}
		try{
			Statement state = Model.con.createStatement();
			String sql = "UPDATE priorityViewSettings "
					+ "SET "
					+ "score ='" + score +  "',"
					+ "days ='" + days + "',"
					+ "course ='" + course + "',"
					+ "weight ='" + weight + "',"
					+ "difficulty ='" + difficulty + "'";
			state.execute(sql);
			initialize();
		}
		catch (SQLException e) {System.out.println(e + "LMAO");}
	}
}
