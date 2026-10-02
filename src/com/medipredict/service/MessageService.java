package com.medipredict.service;

import com.medipredict.dao.MessageDAO;
import com.medipredict.model.Message;
import com.medipredict.util.ValidationUtil;

import java.util.Collections;
import java.util.List;

public class MessageService {
    private final MessageDAO messageDAO = new MessageDAO();

    public boolean sendMessage(int senderId, int receiverId, String text) {
        if (senderId <= 0 || receiverId <= 0 || !ValidationUtil.isNotEmpty(text)) {
            return false;
        }
        Message m = new Message(senderId, receiverId, text.trim());
        int id = messageDAO.sendMessage(m);
        return id != -1;
    }

    public List<Message> getConversation(int user1Id, int user2Id) {
        if (user1Id <= 0 || user2Id <= 0) {
            return Collections.emptyList();
        }
        return messageDAO.getConversation(user1Id, user2Id);
    }

    public boolean markAsRead(int senderId, int receiverId) {
        return messageDAO.markAsRead(senderId, receiverId);
    }

    public int getUnreadCount(int userId) {
        return messageDAO.getUnreadCount(userId);
    }
}
