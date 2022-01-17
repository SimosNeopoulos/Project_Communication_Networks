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
            /* Initiating the BufferedReader and PrintWriter objects that are going to be used to communicate with the client */
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            /* Reading a message from the client */
            String echoString = input.readLine();
            /* Splitting the message (String) from the client and storing it in a String array */
            String[] clientOutput = echoString.split(" ");

            /* Depending on the argument provided the corresponding method is called. */
            switch (clientOutput[0]) {
                case "1":
                    // Creating an account
                    creteAccount(clientOutput[1], output);
                    break;

                case "2":
                    // Showing the usernames from all the accounts stored
                    showAccounts(Integer.parseInt(clientOutput[1]), output);
                    break;

                case "3":
                    // A client sending another client a message
                    sendMessage(Integer.parseInt(clientOutput[1]), clientOutput[2], getMessageBody(clientOutput), output);
                    break;

                case "4":
                    // Printing the inbox (all the messages) of an Account
                    showInbox(Integer.parseInt(clientOutput[1]), output);
                    break;

                case "5":
                    // Printing a message from an Account with a specific message ID
                    readMessage(Integer.parseInt(clientOutput[1]), Integer.parseInt(clientOutput[2]), output);
                    break;

                case "6":
                    // Deleting a message from an Account with a specific message ID
                    deleteMessage(Integer.parseInt(clientOutput[1]), Integer.parseInt(clientOutput[2]), output);
                    break;
            }

        } catch (IOException e) {
            System.out.println("IOException was raised: " + e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Null pointer exception");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Socket could not be closed");
            }
        }
    }

    /**
     * Creating an account with the given username and outputs the AuthToken of the account to the client
     * If the username is not valid or the account already exists appropriate error messages are sent to the client
     *
     * @param username String that represents a username
     * @param output   The stream (PrintWriter) that outputs the server's message to the client
     */
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

    /**
     * Printing the usernames of all the accounts.
     * If the AuthToken is wrong appropriate error messages are sent to the client
     *
     * @param authToken An integer that represents the AuthToken of an account
     * @param output    The stream (PrintWriter) that outputs the server's message to the client
     */
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

    /**
     * Creates a Message object with the sender's message body and stores it in the recipients account inbox
     * If the AuthToken is wrong or the recipients' account doesn't exist appropriate error messages are sent to the client
     *
     * @param authToken An integer that represents the AuthToken of an account
     * @param recipient String that contains the username of the recipient of the message
     * @param message   String that contains the body of the
     * @param output    The stream (PrintWriter) that outputs the server's message to the client
     */
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

    /**
     * Prints the message inbox of the account to the client that the AuthToken belongs to
     * If the AuthToken is wrong appropriate error messages are sent to the client
     *
     * @param authToken An integer that represents the AuthToken of an account
     * @param output    The stream (PrintWriter) that outputs the server's message to the client
     */
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

    /**
     * Prints a message from the inbox, from the account that contains the authToken, that the messageID corresponds to.
     * If the AuthToken is wrong, or the messageID doesn't correspond to a message,
     * appropriate error messages are sent to the client.
     *
     * @param authToken An integer that represents the AuthToken of an account
     * @param messageID An integer that corresponds to a specific message in an account's inbox
     * @param output    The stream (PrintWriter) that outputs the server's message to the client
     */
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

    /**
     * Deletes a message from the inbox, from the account that contains the authToken, that the messageID corresponds to.
     * If the AuthToken is wrong, or the messageID doesn't correspond to a message,
     * appropriate error messages are sent to the client.
     *
     * @param authToken An integer that represents the AuthToken of an account
     * @param messageID An integer that corresponds to a specific message in an account's inbox
     * @param output    The stream (PrintWriter) that outputs the server's message to the client
     */
    private void deleteMessage(int authToken, int messageID, PrintWriter output) {
        if (!this.server.accountExists(authToken)) {
            output.println("Invalid Auth Token");
            return;
        }

        Account account = this.server.getAccount(authToken);
        if (!account.removeMessage(messageID)) {
            output.println("Message does not exist");
            return;
        }
        output.println("OK");
    }

    /**
     * @param username String that represents a username
     * @return True if the username is not invalid
     */
    private boolean isValidUserName(String username) {
        Pattern usernamePattern = Pattern.compile("^([a-zA-Z])+([\\w]{2,})+$");
        Matcher matcher = usernamePattern.matcher(username);
        return matcher.matches();
    }

    /**
     * Converts the String array to a String by appending all the blocks of the array to a String.
     * It then returns the String.
     *
     * @param bodyArray String array that contains the body of the message that was provided
     * @return A String that contains the body of a Message
     */
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

    /**
     * @return A random integer in range of: 1000 to 9999
     */
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
