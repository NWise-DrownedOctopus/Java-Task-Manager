package controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import model.Task;
import model.User;
import service.TaskService;
import service.UserService;
import view.CreateUpdateTaskView;
import view.LoginView;
import view.ReadTaskListView;

/**
 * Handles all button actions on the task list screen.
 */
public class TaskListController {

    private final ReadTaskListView taskListView;
    private final User currentUser;
    private final UserService userService;
    private final TaskService taskService;

    /**
     * Creates a controller for the task list screen.
     *
     * @param taskListView task list screen managed by this controller
     * @param currentUser  user currently shown in the header
     */
    public TaskListController(ReadTaskListView taskListView, User currentUser, UserService userService,
            TaskService taskService) {
        this.taskListView = taskListView;
        this.currentUser = currentUser;
        this.userService = userService;
        this.taskService = taskService;
    }

    /**
     * Performs initial screen setup.
     */
    public void initialize() {
        taskListView.setUserDisplay(currentUser);
        // taskListView.updateTaskTable(java.util.List.of(), 0);

        loadTasks();
    }

    private void loadTasks() {
        List<Task> tasks = taskService.getTasksForUser(currentUser.getId());

        List<Object[]> rows = new ArrayList<Object[]>();
        for (Task task : tasks) {
            rows.add(task.toTableRow());
        }

        taskListView.updateTaskTable(rows, tasks.size());
    }

    /**
     * Validates the filter form and shows a placeholder response.
     */
    public void onApplyFilterButtonClick() {
        showInfo("Filter request captured. Connect this action to tb_task queries later.", "Apply Filter");
        taskListView.updateTaskTable(java.util.List.of(), 0);
    }

    /**
     * Clears filters and table selection.
     */
    public void onClearButtonClick() {
        taskListView.resetFilters();
        taskListView.clearSelection();
        taskListView.updateTaskTable(java.util.List.of(), 0);
        showInfo("Filters cleared.", "Clear Filters");
    }

    /**
     * Shows a placeholder refresh response.
     */
    public void onRefreshButtonClick() {
        taskListView.updateTaskTable(java.util.List.of(), 0);
        showInfo("Refresh clicked. Data loading from tb_task will be added later.", "Refresh");
    }

    /**
     * Opens the add-task dialog and validates the entered fields.
     */
    public void onAddButtonClick() {
        CreateUpdateTaskView dialog = new CreateUpdateTaskView(taskListView, "New Task");
        dialog.setVisible(true);

        if (!dialog.isSaved()) {
            return;
        }

        if (dialog.getTaskTitle().isBlank()) {
            showWarning("Please enter a task title.", "New Task");
            return;
        }

        if (!isValidDueDate(dialog.getDueDateText())) {
            showWarning("Due date must use YYYY-MM-DD format.", "New Task");
            return;
        }

        // showInfo("Task form is valid. Insert into tb_task will be added later.", "New
        // Task");

        // save the task into the database
        try {
            taskService.createTask(
                    currentUser.getId(),
                    dialog.getTaskTitle(),
                    dialog.getTaskDescription(),
                    dialog.getDueDateText(),
                    dialog.getSelectedPriority());

            showInfo("Task added successfully", "New Task Success");
        } catch (IllegalArgumentException ex) {
            showWarning(ex.getMessage(), "New Task Error");
        }
    }

    /**
     * Requires a selected row before an edit action can continue.
     */
    // AFTER
    public void onEditButtonClick() {
        if (!hasSelectedTask()) {
            showWarning("Select a task to edit.", "Edit Task");
            return;
        }

        // Fetch the task FIRST so we can pre-populate the dialog
        Task selectedTask = taskService.getTaskForUser(currentUser.getId(), getSelectedTaskId());

        CreateUpdateTaskView dialog = new CreateUpdateTaskView(
                taskListView,
                "Edit Task",
                selectedTask.getTitle(),
                selectedTask.getDescription(),
                selectedTask.getDueDateDisplay(),
                selectedTask.getPriority());
        dialog.setVisible(true);

        if (!dialog.isSaved())
            return;

        if (dialog.getTaskTitle().isBlank()) {
            showWarning("Please enter a task title.", "Edit Task");
            return;
        }

        if (!isValidDueDate(dialog.getDueDateText())) {
            showWarning("Due date must use YYYY-MM-DD format.", "Edit Task");
            return;
        }

        try {
            taskService.updateTaskDetails(
                    currentUser.getId(),
                    selectedTask.getId(),
                    dialog.getTaskTitle(),
                    dialog.getTaskDescription(),
                    dialog.getDueDateText(),
                    dialog.getSelectedPriority());
            showInfo("Task updated successfully.", "Edit Task");
            loadTasks();
        } catch (IllegalArgumentException ex) {
            showWarning(ex.getMessage(), "Edit Task Error");
        }
    }

    /**
     * Requires a selected row before a status update can continue.
     */
    public void onMarkCompletedButtonClick() {
        if (!hasSelectedTask()) {
            showWarning("Select a task first.", "Mark Completed");
            return;
        }
        showInfo("Mark Completed clicked. Update to tb_task.status will be added later.", "Mark Completed");
    }

    /**
     * Requires a selected row before a status update can continue.
     */
    public void onMarkInProgressButtonClick() {
        if (!hasSelectedTask()) {
            showWarning("Select a task first.", "Mark In Progress");
            return;
        }
        showInfo("Mark In Progress clicked. Update to tb_task.status will be added later.", "Mark In Progress");
    }

    /**
     * Requires a selected row before delete can continue.
     */
    public void onDeleteButtonClick() {
        if (!hasSelectedTask()) {
            showWarning("Select a task to delete.", "Delete Task");
            return;
        }
        Task selectedTask = taskService.getTaskForUser(currentUser.getId(), getSelectedTaskId());

        try {
            taskService.deleteTask(selectedTask.getUserId(), selectedTask.getId());
            showInfo("Deleted sucessful", "Delete Task");
            loadTasks();
        } catch (Exception e) {
            showWarning("Task delete error", "Error");
        }
    }

    /**
     * Returns the user to the login screen after confirmation.
     */
    public void onLogoutButtonClick() {
        int choice = JOptionPane.showConfirmDialog(taskListView,
                "Log out now?",
                "Logout",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            LoginView loginView = new LoginView(userService, taskService);
            loginView.setVisible(true);
            taskListView.dispose();
        }
    }

    /**
     * Performs ISO date check for the due-date text box.
     *
     * @param dueDateText raw due-date text
     * @return {@code true} when blank or formatted as YYYY-MM-DD
     */
    private boolean isValidDueDate(String dueDateText) {
        if (dueDateText == null || dueDateText.isBlank()) {
            return true;
        }

        try {
            LocalDate.parse(dueDateText.trim(), Task.DUE_DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    /**
     * Determines whether a table row is currently selected.
     *
     * @return {@code true} when the user has selected a row in the task table
     */
    private boolean hasSelectedTask() {
        return taskListView.getSelectedTaskId() >= 0;
    }

    /**
     * Shows a standard information dialog.
     *
     * @param message message to display
     * @param title   dialog title
     */
    private void showInfo(String message, String title) {
        JOptionPane.showMessageDialog(taskListView, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private int getSelectedTaskId() {
        int selectedTaskId = taskListView.getSelectedTaskId();
        if (selectedTaskId < 0) {
            showWarning("No Task Selected", "Select Task");
            return -1;
        }
        return selectedTaskId;
    }

    /**
     * Shows a standard warning dialog.
     *
     * @param message message to display
     * @param title   dialog title
     */
    private void showWarning(String message, String title) {
        JOptionPane.showMessageDialog(taskListView, message, title, JOptionPane.WARNING_MESSAGE);
    }
}
