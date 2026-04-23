package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import model.Task;

/**
 * Modal dialog used for both adding and editing tasks.
 *
 * <p>The dialog only collects task fields that belong on the UI form.
 * Task ID, user ID, status, and timestamps are managed elsewhere because those
 * values usually come from persistence or controller logic.</p>
 */
public class CreateUpdateTaskView extends JDialog {

    private static final long serialVersionUID = 1L;

    private final JTextField txtTitle = new JTextField(28);
    private final JTextArea txtDescription = new JTextArea(6, 28);
    private final JTextField txtDueDate = new JTextField(12);
    private final JComboBox<String> cmbPriority =
            new JComboBox<>(new String[] {Task.PRIORITY_LOW, Task.PRIORITY_MEDIUM, Task.PRIORITY_HIGH});
    private final JButton btnSave = new JButton("Save");
    private final JButton btnCancel = new JButton("Cancel");

    private boolean saved;

    /**
     * Creates the dialog for adding a new task.
     *
     * @param parent owner frame used for modality and centering
     * @param dialogTitle title shown on the dialog window
     */
    public CreateUpdateTaskView(Frame parent, String dialogTitle) {
        this(parent, dialogTitle, "", "", "", Task.PRIORITY_MEDIUM);
    }

    /**
     * Creates the dialog and pre-populates the fields for editing.
     *
     * @param parent owner frame used for modality and centering
     * @param dialogTitle title shown on the dialog window
     * @param title initial title value
     * @param description initial description value
     * @param dueDate initial due date value
     * @param priority initial priority value
     */
    public CreateUpdateTaskView(Frame parent,
                             String dialogTitle,
                             String title,
                             String description,
                             String dueDate,
                             String priority) {
        super(parent, dialogTitle, true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        buildUI();
        populate(title, description, dueDate, priority);
        addActionListeners();

        getRootPane().setDefaultButton(btnSave);
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
        SwingUtilities.invokeLater(() -> txtTitle.requestFocusInWindow());
    }

    /**
     * Builds the Swing components used by the dialog.
     */
    private void buildUI() {
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);

        Dimension labelSize = new Dimension(170, 24);

        JPanel titleRow = new JPanel(new BorderLayout(10, 0));
        JLabel lblTitle = new JLabel("Title:");
        lblTitle.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTitle.setPreferredSize(labelSize);
        titleRow.add(lblTitle, BorderLayout.WEST);
        titleRow.add(txtTitle, BorderLayout.CENTER);

        JPanel descRow = new JPanel(new BorderLayout(10, 0));
        JLabel lblDescription = new JLabel("Description:");
        lblDescription.setHorizontalAlignment(SwingConstants.RIGHT);
        lblDescription.setPreferredSize(labelSize);
        descRow.add(lblDescription, BorderLayout.WEST);
        descRow.add(new JScrollPane(txtDescription), BorderLayout.CENTER);

        JPanel bottomFields = new JPanel(new GridLayout(2, 1, 0, 10));

        JPanel dueDateRow = new JPanel(new BorderLayout(10, 0));
        JLabel lblDueDate = new JLabel("Due date (YYYY-MM-DD):");
        lblDueDate.setHorizontalAlignment(SwingConstants.RIGHT);
        lblDueDate.setPreferredSize(labelSize);
        JPanel dueWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dueWrap.add(txtDueDate);
        dueDateRow.add(lblDueDate, BorderLayout.WEST);
        dueDateRow.add(dueWrap, BorderLayout.CENTER);

        JPanel priorityRow = new JPanel(new BorderLayout(10, 0));
        JLabel lblPriority = new JLabel("Priority:");
        lblPriority.setHorizontalAlignment(SwingConstants.RIGHT);
        lblPriority.setPreferredSize(labelSize);
        JPanel priorityWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        priorityWrap.add(cmbPriority);
        priorityRow.add(lblPriority, BorderLayout.WEST);
        priorityRow.add(priorityWrap, BorderLayout.CENTER);

        bottomFields.add(dueDateRow);
        bottomFields.add(priorityRow);

        JPanel formPanel = new JPanel(new BorderLayout(0, 12));
        formPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));
        formPanel.add(titleRow, BorderLayout.NORTH);
        formPanel.add(descRow, BorderLayout.CENTER);
        formPanel.add(bottomFields, BorderLayout.SOUTH);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonRow.add(btnCancel);
        buttonRow.add(btnSave);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));
        buttonPanel.add(buttonRow, BorderLayout.EAST);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Registers the button listeners.
     */
    private void addActionListeners() {
        btnSave.addActionListener(e -> {
            saved = true;
            dispose();
        });
        btnCancel.addActionListener(e -> {
            saved = false;
            dispose();
        });
    }

    /**
     * Applies the initial field values.
     *
     * @param title initial title
     * @param description initial description
     * @param dueDate initial due date
     * @param priority initial priority
     */
    private void populate(String title, String description, String dueDate, String priority) {
        txtTitle.setText(title);
        txtDescription.setText(description);
        txtDueDate.setText(dueDate);
        cmbPriority.setSelectedItem(priority == null || priority.isBlank() ? Task.PRIORITY_MEDIUM : priority);
    }

    /**
     * @return {@code true} when the user clicked Save
     */
    public boolean isSaved() {
        return saved;
    }

    /**
     * @return trimmed task title entered by the user
     */
    public String getTaskTitle() {
        return txtTitle.getText().trim();
    }

    /**
     * @return trimmed task description entered by the user
     */
    public String getTaskDescription() {
        return txtDescription.getText().trim();
    }

    /**
     * @return trimmed due-date text entered by the user
     */
    public String getDueDateText() {
        return txtDueDate.getText().trim();
    }

    /**
     * @return currently selected priority value
     */
    public String getSelectedPriority() {
        return String.valueOf(cmbPriority.getSelectedItem());
    }
}
