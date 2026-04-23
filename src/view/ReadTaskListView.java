package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import controller.TaskListController;
import model.Task;
import model.User;
import service.TaskService;
import service.UserService;

/**
 * Main task list window.
 */
public class ReadTaskListView extends JFrame {

    private static final long serialVersionUID = 1L;

    private final TaskListController controller;
    private final TaskService taskService;

    private final JLabel userLabel = new JLabel("Tasks for user   [userId=0]");
    private final JComboBox<String> cmbStatus =
            new JComboBox<>(new String[] {Task.FILTER_ANY, Task.STATUS_TODO, Task.STATUS_IN_PROGRESS, Task.STATUS_COMPLETED});
    private final JComboBox<String> cmbPriority =
            new JComboBox<>(new String[] {Task.FILTER_ANY, Task.PRIORITY_LOW, Task.PRIORITY_MEDIUM, Task.PRIORITY_HIGH});
    private final JTextField txtKeywordSearch = new JTextField(18);
    private final JButton btnApplyFilter = new JButton("Apply Filter");
    private final JButton btnClear = new JButton("Clear");

    private final JTable tblTasks;
    private final JLabel footerLabel = new JLabel("Showing 0 of 0 task(s).");

    private final JButton btnRefresh = new JButton("Refresh");
    private final JButton btnNewTask = new JButton("New Task");
    private final JButton btnEditTask = new JButton("Edit Task");
    private final JButton btnMarkComplete = new JButton("Mark Completed");
    private final JButton btnMarkInProgress = new JButton("Mark In Progress");
    private final JButton btnDelete = new JButton("Delete Task");
    private final JButton btnLogout = new JButton("Logout");

    /**
     * Creates the main task list window.
     *
     * @param currentUser user currently signed in
     */
    public ReadTaskListView(User currentUser, UserService userService, TaskService taskService) {
        this.taskService = taskService;
        controller = new TaskListController(this, currentUser, userService, taskService);

        setTitle("Task Manager - Tasks");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[] {"ID", "Title", "Status", "Priority", "Due Date"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblTasks = new JTable(tableModel);
        tblTasks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblTasks.setRowHeight(24);
        tblTasks.setFillsViewportHeight(true);

        buildUI();
        addActionListeners();
        applyInitialState();

        controller.initialize();
    }

    /**
     * Builds the Swing components used by the screen.
     */
    private void buildUI() {
        setLayout(new BorderLayout(0, 10));

        JPanel topPanel = new JPanel(new BorderLayout(0, 6));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.add(userLabel);
        topPanel.add(labelPanel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(cmbStatus);
        filterPanel.add(Box.createHorizontalStrut(8));

        filterPanel.add(new JLabel("Priority:"));
        filterPanel.add(cmbPriority);
        filterPanel.add(Box.createHorizontalStrut(8));

        filterPanel.add(new JLabel("Keyword:"));
        filterPanel.add(txtKeywordSearch);
        filterPanel.add(Box.createHorizontalStrut(8));

        filterPanel.add(btnApplyFilter);
        filterPanel.add(btnClear);

        topPanel.add(filterPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(tblTasks);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        scrollPane.setPreferredSize(new Dimension(940, 340));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 6));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        footerPanel.add(footerLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnNewTask);
        buttonPanel.add(btnEditTask);
        buttonPanel.add(btnMarkComplete);
        buttonPanel.add(btnMarkInProgress);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnLogout);

        bottomPanel.add(footerPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Registers listeners that delegate button clicks to the controller.
     */
    private void addActionListeners() {
        btnApplyFilter.addActionListener(e -> controller.onApplyFilterButtonClick());
        btnClear.addActionListener(e -> controller.onClearButtonClick());
        btnRefresh.addActionListener(e -> controller.onRefreshButtonClick());
        btnNewTask.addActionListener(e -> controller.onAddButtonClick());
        btnEditTask.addActionListener(e -> controller.onEditButtonClick());
        btnMarkComplete.addActionListener(e -> controller.onMarkCompletedButtonClick());
        btnMarkInProgress.addActionListener(e -> controller.onMarkInProgressButtonClick());
        btnDelete.addActionListener(e -> controller.onDeleteButtonClick());
        btnLogout.addActionListener(e -> controller.onLogoutButtonClick());
    }

    /**
     * Applies common startup configuration after the components exist.
     */
    private void applyInitialState() {
        pack();
        setMinimumSize(new Dimension(980, 520));
        setLocationRelativeTo(null);
        SwingUtilities.invokeLater(() -> txtKeywordSearch.requestFocusInWindow());
    }

    /**
     * Updates the header label with the current user.
     *
     * @param user current user shown in the header
     */
    public void setUserDisplay(User user) {
        String displayName = user.getUsername();
        if (user.getFullName() != null && !user.getFullName().isBlank()) {
            displayName = user.getFullName();
        }
        userLabel.setText("Tasks for " + displayName + "   [userId=" + user.getId() + "]");
    }

    /**
     * Rebuilds the table model with the provided rows.
     *
     * @param rows table rows to display
     * @param totalTasks total number of tasks before filtering
     */
    public void updateTaskTable(List<Object[]> rows, int totalTasks) {
        DefaultTableModel model = (DefaultTableModel) tblTasks.getModel();
        model.setRowCount(0);
        for (Object[] row : rows) {
            model.addRow(row);
        }
        footerLabel.setText("Showing " + rows.size() + " of " + totalTasks + " task(s).");
    }

    /**
     * Resets the filter controls to their default values.
     */
    public void resetFilters() {
        cmbStatus.setSelectedItem(Task.FILTER_ANY);
        cmbPriority.setSelectedItem(Task.FILTER_ANY);
        txtKeywordSearch.setText("");
    }

    /**
     * @return selected status filter value
     */
    public String getSelectedStatus() {
        return String.valueOf(cmbStatus.getSelectedItem());
    }

    /**
     * @return selected priority filter value
     */
    public String getSelectedPriority() {
        return String.valueOf(cmbPriority.getSelectedItem());
    }

    /**
     * @return trimmed keyword filter text
     */
    public String getKeyword() {
        return txtKeywordSearch.getText().trim();
    }

    /**
     * Returns the ID of the currently selected task row.
     *
     * @return selected task ID, or -1 when nothing is selected
     */
    public int getSelectedTaskId() {
        int selectedRow = tblTasks.getSelectedRow();
        if (selectedRow < 0) {
            return -1;
        }
        Object idValue = tblTasks.getValueAt(selectedRow, 0);
        return Integer.parseInt(String.valueOf(idValue));
    }

    /**
     * Clears the current table selection.
     */
    public void clearSelection() {
        tblTasks.clearSelection();
    }
}
