package com.medipredict.dao;

import com.medipredict.database.DatabaseConnection;
import com.medipredict.model.Message;
import com.medipredict.util.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public int sendMessage(Message m) {
        String sql = "INSERT INTO messages (sender_id, receiver_id, message_text, is_read) VALUES (?, ?, ?, 0);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, m.getSenderId());
            ps.setInt(2, m.getReceiverId());
            ps.setString(3, m.getMessageText());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    m.setMessageId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            Logger.error("Error sending message from user ID: " + m.getSenderId(), e);
        }
        return -1;
    }

    public List<Message> getConversation(int user1Id, int user2Id) {
        List<Message> list = new ArrayList<>();
        String sql = "SELECT m.*, " +
                "u1.full_name AS sender_name, u1.role AS sender_role, " +
                "u2.full_name AS receiver_name " +
                "FROM messages m " +
                "JOIN users u1 ON m.sender_id = u1.user_id " +
                "JOIN users u2 ON m.receiver_id = u2.user_id " +
                "WHERE (m.sender_id = ? AND m.receiver_id = ?) OR (m.sender_id = ? AND m.receiver_id = ?) " +
                "ORDER BY m.sent_at ASC;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user1Id);
            ps.setInt(2, user2Id);
            ps.setInt(3, user2Id);
            ps.setInt(4, user1Id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapMessage(rs));
                }
            }
        } catch (SQLException e) {
            Logger.error("Error fetching conversation between " + user1Id + " and " + user2Id, e);
        }
        return list;
    }

    public boolean markAsRead(int senderId, int receiverId) {
        String sql = "UPDATE messages SET is_read = 1 WHERE sender_id = ? AND receiver_id = ? AND is_read = 0;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("Error marking messages as read", e);
            return false;
        }
    }

    public int getUnreadCount(int userId) {
        String sql = "SELECT COUNT(*) AS cnt FROM messages WHERE receiver_id = ? AND is_read = 0;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        } catch (SQLException e) {
            Logger.error("Error getting unread count for user ID: " + userId, e);
        }
        return 0;
    }

    private Message mapMessage(ResultSet rs) throws SQLException {
        Message m = new Message();
        m.setMessageId(rs.getInt("message_id"));
        m.setSenderId(rs.getInt("sender_id"));
        m.setReceiverId(rs.getInt("receiver_id"));
        m.setMessageText(rs.getString("message_text"));
        m.setRead(rs.getInt("is_read") == 1);
        m.setSentAt(rs.getTimestamp("sent_at"));
        m.setSenderName(rs.getString("sender_name"));
        m.setSenderRole(rs.getString("sender_role"));
        m.setReceiverName(rs.getString("receiver_name"));
        return m;
    }
}
