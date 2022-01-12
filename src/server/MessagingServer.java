package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessagingServer extends Thread {

    private final Socket socket;
    private final Server server;

    public MessagingServer(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            String echoString = input.readLine();
            String[] clientOutput = echoString.split(" ");

            switch (clientOutput[0]) {
                case "1":
                    creteAccount(clientOutput[1], output);
                    break;

                case "2":
                    showAccounts(Integer.parseInt(clientOutput[1]), output);
                    break;

                case "3":
                    sendMessage(Integer.parseInt(clientOutput[1]), clientOutput[2], getMessageBody(clientOutput), output);
                    break;

                case "4":
                    showInbox(Integer.parseInt(clientOutput[1]), output);
                    break;

                case "5":
                    readMessage(Integer.parseInt(clientOutput[1]), Integer.parseInt(clientOutput[2]), output);
                    break;

                case "6":
                    deleteMessage(Integer.parseInt(clientOutput[1]), Integer.parseInt(clientOutput[2]), output);
            }

        } catch (IOException e) {
            System.out.println("Oops: " + e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Null pointer exception");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // Oh, well!
            }
        }
    }

    private void creteAccount(String username, PrintWriter output) {
        if (!isValidUserName(username)) {
            output.println("Invalid Username");
            return;
        }

        if (this.server.accountExists(username)) {
            output.println("Sorry, the user already exists");
            return;
        }

        int authToken = getAuthToken();
        this.server.addAccount(new Account(username, authToken));
        output.println(authToken);
    }

    private void showAccounts(int authToken, PrintWriter output) {
        if (!this.server.accountExists(authToken)) {
            output.println("Invalid Auth Token");
            return;
        }

        ArrayList<String> accountsName = this.server.getAccountsNames();
        String stringToEcho = "";
        int nameCounter = 1;
        for (String name : accountsName) {
            stringToEcho += nameCounter + ". " + name + "\n";
            nameCounter++;
        }
        output.println(stringToEcho);
    }

    private void sendMessage(int authToken, String recipient, String message, PrintWriter output) {
        if (!this.server.accountExists(authToken)) {
            output.println("Invalid Auth Token");
            return;
        }

        Account receiverAccount = this.server.getAccount(recipient);
        if (receiverAccount == null) {
            output.println("User does not exist");
            return;
        }

        receiverAccount.addMessage(new Message(this.server.getAccount(authToken).getUsername(), receiverAccount.getUsername(), message));
        output.println("OK");
    }

    private void showInbox(int authToken, PrintWriter output) {
        if (!this.server.accountExists(authToken)) {
            output.println("Invalid Auth Token");
            return;
        }

        Account accountUser = this.server.getAccount(authToken);
        List<Message> messages = accountUser.getMessagesBox();
        String echoString = "";
        for (int i = 0; i < messages.size(); i++) {
            echoString += i + ". from: " + messages.get(i).getSender();
            if (messages.get(i).isRead()) {
                echoString += "\n";
            } else {
                echoString += "*\n";
            }
        }

        output.println(echoString);

    }

    private void readMessage(int authToken, int messageID, PrintWriter output) {
        if (!this.server.accountExists(authToken)) {
            output.println("Invalid Auth Token");
            return;
        }

        Account account = this.server.getAccount(authToken);
        Message message = account.getMessage(messageID);
        if (message == null) {
            output.println("Message ID does not exist");
            return;
        }
        message.setRead(true);
        output.println("(" + message.getSender() + ")" + message.getBody());
    }

    private void deleteMessage(int authToken, int messageID, PrintWriter output) {
        if (!this.server.accountExists(authToken)) {
            output.println("Invalid Auth Token");
            return;
        }

        Account account = this.server.getAccount(authToken);
        if(!account.removeMessage(messageID)){
            output.println("Message does not exist");
            return;
        }
        output.println("OK");
    }

    private boolean isValidUserName (String username) {
        Pattern usernamePattern = Pattern.compile("^([a-zA-Z])+([\\w]{2,})+$");
        Matcher matcher = usernamePattern.matcher(username);
        return matcher.matches();
    }

    private String getMessageBody(String[] bodyArray) {
        String messageBody = "";
        for (int i = 3; i < bodyArray.length; i++) {
            messageBody += bodyArray[i];
            if (i + 1 != bodyArray.length) {
                messageBody += " ";
            }
        }
        return messageBody;
    }

    private int getAuthToken() {
        int min = 1000;
        int max = 10000;
        int randomToken;
        do {
            randomToken = (int) Math.floor((Math.random() * (max - min + 1) + min));
        } while (this.server.accountExists(randomToken));
        return randomToken;
    }
}
