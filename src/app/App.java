package app;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import dao.TaskDao;
import dao.UserDao;
import model.Task;
import service.TaskService;
import service.UserService;
import util.DbConfig;
import util.DbConnection;
import util.PasswordHasher;
import view.LoginView;

public class App {

	/**
	 * Application entry point.
	 */
	public static void main(String[] args) {
		try {
			DbConfig config = new DbConfig();
			DbConnection connection = new DbConnection(config);

			UserDao userDao = new UserDao(connection);
			TaskDao taskDao = new TaskDao(connection);

			TaskService taskService = new TaskService(taskDao);

			PasswordHasher passwordHasher = new PasswordHasher();
			UserService userService = new UserService(userDao, passwordHasher);

			new LoginView(userService, taskService).setVisible(true);

			// Task newTask = new Task(1, 18, "Update titel", "Update description", LocalDate.now(), "OPEN", "MEDIUM", LocalDateTime.now());
			// taskDao.updateTask(newTask);

			// taskService.createTask(10, "testTask", "Test Descritption", "2020-01-01", "MEDIUM");
			// List<Task> tasks = taskDao.readByUser(10);
			// for (Task task : tasks) {
			// 	System.out.println(task);
			// }
		} catch (RuntimeException ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Application Startup Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
