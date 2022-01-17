package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Server {
    /**
     * Map that contains all the Accounts and their data
     */
    private final Map<Integer, Account> accountMap;

    public Server() {
        accountMap = Collections.synchronizedMap(new HashMap<>());
    }

    /**
     * Returns True if the account with the authToken exists,
     * and false if the account doesn't exist.
     *
     * @param authToken The AuthToken of the client
     * @return True if the account with the authToken exists
     */
    public synchronized boolean accountExists(int authToken) {
        return this.accountMap.containsKey(authToken);
    }

    /**
     * This method adds the Account that was passed as an argument in accountMap Map,
     * with the AuthToken of the account as it's key
     *
     * @param account Account object to be added in the accountMap Map
     */
    public synchronized void addAccount(Account account) {
        this.accountMap.put(account.getAuthToken(), account);
    }

    /**
     * This method finds whether a value exists with the same key as the argument that was passed.
     * If it exists it returns the Account object
     *
     * @param key The key of a value in the accountMap Map
     * @return An Account if it exists
     */
    public synchronized Account getAccount(int key) {
        return this.accountMap.get(key);
    }

    /**
     * Returns the Account from the accountMap with the same username as the argument if it exists.
     * If it doesn't exist returns null
     *
     * @param username A String that resembles the username of an Account
     * @return The Account with the same username as the argument, from the accountMap Map, if it exists.
     */
    public synchronized Account getAccount(String username) {
        for (Account account : this.accountMap.values()) {
            if (account.getUsername().equals(username)) {
                return account;
            }
        }
        return null;
    }

    /**
     * Returns true if the Account with the same username as the argument exists
     * If it doesn't exist returns false
     *
     * @param username A String that resembles the username of an Account
     * @return True if an Account with the same username as the argument exists
     */
    public synchronized boolean accountExists(String username) {
        for (Account account : this.accountMap.values()) {
            if (account.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return Arraylist with the usernames from all the Accounts in the accountMap Map
     */
    public synchronized ArrayList<String> getAccountsNames() {
        ArrayList<String> accountNames = new ArrayList<>();
        for (Account account : this.accountMap.values()) {
            accountNames.add(account.getUsername());
        }
        return accountNames;
    }

    public static void main(String[] args) {
        /* Server object */
        Server server = new Server();
        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]))) {
            while (true)
                new MessagingServer(serverSocket.accept(), server).start();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


}
