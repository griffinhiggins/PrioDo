package application;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Model {
	public static Connection con;
	public Model() {
	}
	public void getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		con = DriverManager.getConnection("jdbc:sqlite:store.db");
		initStore();
	}
	public void closeConn() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void initStore() throws SQLException {
		Statement state = con.createStatement();
		state.execute(
				"CREATE TABLE IF NOT EXISTS course (" + 
				"		    course_id         INTEGER PRIMARY KEY AUTOINCREMENT," + 
				"		    course_name       TEXT NOT NULL," + 
				"		    assignment_weight INTEGER DEFAULT 0," + 
				"		    lab_weight        INTEGER DEFAULT 0," + 
				"		    quiz_weight       INTEGER DEFAULT 0," +
				"		    midterm_weight    INTEGER DEFAULT 0," +
				"		    final_weight      INTEGER DEFAULT 0," + 
				"		    paper_weight      INTEGER DEFAULT 0," + 
				"		    project_weight    INTEGER DEFAULT 0," + 
				"		    attendance_weight INTEGER DEFAULT 0" +
				")"
		);
		state.execute(
				"CREATE TABLE IF NOT EXISTS workType (" + 
				"		    work_type_id      INTEGER PRIMARY KEY AUTOINCREMENT," +
				"		    work_type_name    TEXT DEFAULT ''" +
				")"
		);
		
		ResultSet rs = state.executeQuery("SELECT * FROM workType LIMIT 1");
		if(!rs.next()){
			state.execute("INSERT INTO workType(work_type_name)VALUES('Assignments')");
			state.execute("INSERT INTO workType(work_type_name)VALUES('Labs')");
			state.execute("INSERT INTO workType(work_type_name)VALUES('Quizs')");
			state.execute("INSERT INTO workType(work_type_name)VALUES('Midterm')");
			state.execute("INSERT INTO workType(work_type_name)VALUES('Final')");
			state.execute("INSERT INTO workType(work_type_name)VALUES('Paper')");
			state.execute("INSERT INTO workType(work_type_name)VALUES('Project')");
			state.execute("INSERT INTO workType(work_type_name)VALUES('Attendance')");
			
		}
		
		state.execute(
				"CREATE TABLE IF NOT EXISTS work (" + 
				"		    work_id           INTEGER PRIMARY KEY AUTOINCREMENT," +
				"		    work_type_id      INTEGER NOT NULL," + 
				"		    work_name         TEXT NOT NULL," +
				"		    work_difficulty   INTEGER DEFAULT 0," +
				"		    course_id         INTEGER NOT NULL," +
				"		    due_date          DATE NOT NULL,"  +
				"			FOREIGN KEY(course_id) REFERENCES course(course_id)," +
				"			FOREIGN KEY(work_type_id) REFERENCES workType(work_type_id)" +
				")"
		);
		
		state.execute(
				"CREATE TABLE IF NOT EXISTS priority (" + 
				"		    priority_id           INTEGER PRIMARY KEY AUTOINCREMENT," +
				"		    priority_group        INTEGER DEFAULT 3," + 
				"			priority_score	      REAL DEFAULT 0," +
				"			priority_days	      INTEGER DEFAULT 0," +
				"			work_id	      		  INTEGER NOT NULL," +
				"			FOREIGN KEY(work_id) REFERENCES work(work_id)" +
				")"
		);
		state.execute(
				"CREATE TABLE IF NOT EXISTS priorityViewSettings (" + 
				"		    priorityViewSettings_id   INTEGER PRIMARY KEY AUTOINCREMENT," +
				"		    score        		  INTEGER DEFAULT 0," + 
				"			days	              INTEGER DEFAULT 0," +
				"			course	      		  INTEGER DEFAULT 0," +
				"			weight	      		  INTEGER DEFAULT 0," +
				"			difficulty	      	  INTEGER DEFAULT 0" +
				")"
		);
		rs = state.executeQuery("SELECT * FROM priorityViewSettings LIMIT 1");
		if(!rs.next()){
			state.execute("INSERT INTO priorityViewSettings(score,days,course,weight,difficulty)VALUES(0,0,0,0,0)");
		}
		state.execute(
				"CREATE TABLE IF NOT EXISTS prioritySettings (" + 
				"		    prioritySettings_id   INTEGER PRIMARY KEY AUTOINCREMENT," +
				"		    threshold_1   INTEGER DEFAULT 0," + 
				"		    threshold_2   INTEGER DEFAULT 0" +
				")"
		);
		rs = state.executeQuery("SELECT * FROM prioritySettings LIMIT 1");
		if(!rs.next()){
			state.execute("INSERT INTO prioritySettings(threshold_1,threshold_2)VALUES(200,100)");
		}
	}
}
