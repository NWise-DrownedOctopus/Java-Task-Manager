package dao;

import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Task;
import model.User;
import util.DbConnection;

public class TaskDao {

    private static final String TASK_COLUMNS = "id, user_id, title, description, due_date, status, priority, created_at";

    private final DbConnection connection;

    public TaskDao(DbConnection connection) {
        this.connection = connection;
    }

    public int createTask(Task task) {

        String query = "INSERT INTO tb_task (user_id, title, description, due_date, status, priority)"
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = connection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, task.getUserId());
            ps.setString(2, task.getTitle());
            ps.setString(3, task.getDescription());
            setNullableDate(ps, 4, task.getDueDate());
            ps.setString(5, task.getStatus());
            ps.setString(6, task.getPriority());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new IllegalStateException("Task insert failed: no rows were affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

            throw new IllegalStateException("Task insert failed: generated ID was not returned.");
        } catch (SQLException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }

    private void setNullableDate(PreparedStatement ps, int i, LocalDate dueDate) throws SQLException {
        if (dueDate == null) {
            ps.setDate(i, null);
            return;
        }
        ps.setDate(i, Date.valueOf(dueDate));
    }

    public List<Task> readByUser(int user_id) {
        String query = "SELECT " + TASK_COLUMNS + " FROM tb_task WHERE user_id = ?";

        List<Task> tasks = new ArrayList<Task>();

        try (Connection conn = connection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, user_id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tasks.add((mapRowToTasks(rs)));
                }
            }

            return tasks;
        } catch (SQLException ex) {
            throw new IllegalStateException("Unable to read tasks by user ID.", ex);
        }
    }

    public Task readOneTaskByUser(int userId, int taskId) {
        String query = "SELECT " + TASK_COLUMNS + " FROM tb_task WHERE user_id=? AND id = ?";

        try (Connection conn = connection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ps.setInt(2, taskId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTasks(rs);
                }
            }

            return null;
        } catch (SQLException ex) {
            throw new IllegalStateException("Unable to read task by user ID.", ex);
        }
    }

    private Task mapRowToTasks(ResultSet rs) throws SQLException {
        Timestamp createdAt = rs.getTimestamp("created_at");
        Date dueDate = rs.getDate("due_date");

        return new Task(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("title"),
                rs.getString("description"),
                dueDate == null ? null : dueDate.toLocalDate(),
                rs.getString("status"),
                rs.getString("priority"),
                createdAt == null ? null : createdAt.toLocalDateTime());
    }

    public boolean updateTask(Task task) {

        String query = "UPDATE tb_task SET title=?, description=?, due_date=?, priority=? WHERE id=? AND user_id=?";

        try (Connection conn = connection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            setNullableDate(ps, 3, task.getDueDate());
            ps.setString(4, task.getPriority());
            ps.setInt(5, task.getId());
            ps.setInt(6, task.getUserId());

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    } 

    public boolean deleteTask(int taskId, int userId) {

        String query = "DELETE FROM tb_task WHERE id=? AND user_id=?";

        try (Connection conn = connection.getConnection();
                PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, taskId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    } 
}
