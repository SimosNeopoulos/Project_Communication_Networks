package server;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private final String username;
    private final int authToken;
    private final List<Message> messagesBox;

    public Account(String username, int authToken) {
        this.username = username;
        this.authToken = authToken;
        messagesBox = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public int getAuthToken() {
        return authToken;
    }

    public List<Message> getMessagesBox() {
        return messagesBox;
    }

    public void addMessage(Message message) {
        this.messagesBox.add(message);
    }

    public Message getMessage(int messageID) {
        try {
            return this.messagesBox.get(messageID);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public boolean removeMessage(int messageID) {
        try {
            if (this.messagesBox.remove(messageID) != null)
                return true;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return false;
    }
}
