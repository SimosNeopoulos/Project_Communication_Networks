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

    /**
     * @param messageID The ID the message
     * @return The Message object with the messageID if it exists.
     *         If it doesn't exist returns null
     */
    public Message getMessage(int messageID) {
        try {
            return this.messagesBox.get(messageID);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     *
     * @param messageID The ID the message
     * @return True if the Message with the messageID exists and was successfully removed from the List
     *         False if the Message with the messageID doesn't exist or wasn't successfully removed from the List
     */
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
