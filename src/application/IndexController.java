package application;

import java.sql.SQLException;

public class IndexController {
	public IndexController() {
	}
	public void classView() throws SQLException {
		new CourseViewLoader(Main.root);
	}
	public void entryView() throws SQLException {
		new WorkViewLoader(Main.root);
	}
	public void priorityView() throws SQLException {
		new PriorityViewLoader(Main.root);
	}
	public void settingsView() throws SQLException {
		new SettingsViewLoader(Main.root);
	}
}
