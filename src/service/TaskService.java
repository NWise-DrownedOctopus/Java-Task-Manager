package service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import dao.TaskDao;
import model.Task;

public class TaskService {
    
    private final TaskDao taskDao;

    public TaskService(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public Task createTask(int userId, String title, String desciprtion, String dueDateText, String priority) {
        // Buisiness: all newly ccreated tasks will have status = 'TODO'
        // Buisiness: all tasks must be created by a legitimate user

        if(userId <= 0) {
            throw new IllegalArgumentException("A valid user is required");
        }

        Task newTask = new Task();

        newTask.setUserId(userId);
        newTask.setTitle(title);
        newTask.setDescription(desciprtion);
        newTask.setDueDate(convertDueDate(dueDateText));
        newTask.setPriority(priority);
        newTask.setStatus(Task.STATUS_TODO);

        int id = taskDao.createTask(newTask);
        newTask.setId(id);

        return newTask;
    }

    private LocalDate convertDueDate(String dueDateText) {
        if(dueDateText == null || dueDateText.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(dueDateText.trim(), Task.DUE_DATE_FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Due date must use YYYY-MM-DD format");
        }
    }

    public Task updateTaskDetails(int userId, int taskId, String title, String description, String DueDateText, String priority) {
        
        Task existingTask = getTaskForUser(userId, taskId);

        existingTask.setTitle(title);
        existingTask.setDescription(description);
        existingTask.setDueDate(convertDueDate(DueDateText));
        existingTask.setPriority(priority);

        boolean updated = taskDao.updateTask(existingTask);

        if(!updated) {
            throw new IllegalArgumentException("The selected task not udpated");
        }

        return existingTask;
    }

    public Task getTaskForUser (int userId, int taskId) {
        Task task = taskDao.readOneTaskByUser(userId, taskId);

        if (task==null) {
            throw new IllegalArgumentException("The selected task was not found");
        }
        return task;
    }

    public List<Task> getTasksForUser(int userId, int taskId) {
        Task task = taskDao.readOneTaskByUser(userId, taskId);
        if (task==null) {
            throw new IllegalArgumentException("The selected task not found");
        }
        return taskDao.readByUser(userId);
    }    

    public void deleteTask(int userId, int taskId) {
        
        getTasksForUser(userId, taskId);

        boolean deleted = taskDao.deleteTask(taskId, userId);

        if(!deleted) {
            throw new IllegalArgumentException("The selected task not deleted");
        }
    }
}
