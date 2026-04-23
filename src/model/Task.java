package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Task representation.
 */
public class Task {

    /** Generic filter option used by the task list screen. */
    public static final String FILTER_ANY = "Any";

    /** Status value stored for a task not yet started. */
    public static final String STATUS_TODO = "TODO";

    /** Status value stored for a task currently being worked on. */
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";

    /** Status value stored for a completed task. */
    public static final String STATUS_COMPLETED = "COMPLETED";

    /** Low-priority value. */
    public static final String PRIORITY_LOW = "LOW";

    /** Medium-priority value. */
    public static final String PRIORITY_MEDIUM = "MEDIUM";

    /** High-priority value. */
    public static final String PRIORITY_HIGH = "HIGH";

    /** Shared formatter for task due dates. */
    public static final DateTimeFormatter DUE_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private int id;
    private int userId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private String status;
    private String priority;
    private LocalDateTime createdAt;

    /**
     * Creates an empty task model.
     */
    public Task() {
        // Default constructor intentionally left empty.
    }

    /**
     * Creates a fully populated task model.
     *
     * @param id task primary key
     * @param userId owning user's ID from {@code tb_task.user_id}
     * @param title task title
     * @param description optional task description
     * @param dueDate optional task due date
     * @param status task status
     * @param priority task priority
     * @param createdAt task creation timestamp
     */
    public Task(int id, int userId, String title, String description,
                LocalDate dueDate, String status, String priority,
                LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
    }

    /** @return task ID */
    public int getId() { return id; }

    /** @param id task ID */
    public void setId(int id) { this.id = id; }

    /** @return owning user ID */
    public int getUserId() { return userId; }

    /** @param userId owning user ID */
    public void setUserId(int userId) { this.userId = userId; }

    /** @return task title */
    public String getTitle() { return title; }

    /** @param title task title */
    public void setTitle(String title) { this.title = title; }

    /** @return task description */
    public String getDescription() { return description; }

    /** @param description task description */
    public void setDescription(String description) { this.description = description; }

    /** @return task due date */
    public LocalDate getDueDate() { return dueDate; }

    /** @param dueDate task due date */
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    /** @return task status */
    public String getStatus() { return status; }

    /** @param status task status */
    public void setStatus(String status) { this.status = status; }

    /** @return task priority */
    public String getPriority() { return priority; }

    /** @param priority task priority */
    public void setPriority(String priority) { this.priority = priority; }

    /** @return creation timestamp */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /** @param createdAt creation timestamp */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * Formats the due date for display in a table or form.
     *
     * @return ISO date string, or an empty string when no due date exists
     */
    public String getDueDateDisplay() {
        return dueDate == null ? "" : DUE_DATE_FORMATTER.format(dueDate);
    }

    /**
     * Converts the task into the row order expected by the task list table.
     *
     * @return table row values in display order
     */
    public Object[] toTableRow() {
        return new Object[] {id, title, status, priority, getDueDateDisplay()};
    }
}
