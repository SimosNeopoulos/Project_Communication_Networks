package server;

public class Message {

    private boolean isRead;
    private final String sender;
    private final String receiver;
    private final String body;

    public Message(String sender, String receiver, String body) {
        this.sender = sender;
        this.receiver = receiver;
        this.body = body;
        this.isRead = false;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getSender() {
        return sender;
    }

    public String getBody() {
        return body;
    }
}
