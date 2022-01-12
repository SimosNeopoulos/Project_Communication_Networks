package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private final Map<Integer, Account> accountMap;

    public Server() {
        accountMap = Collections.synchronizedMap(new HashMap<>());
    }

    public synchronized boolean accountExists(int authToken) {
        return this.accountMap.containsKey(authToken);
    }

    public synchronized void addAccount(Account account) {
        this.accountMap.put(account.getAuthToken(), account);
    }

    public synchronized Account getAccount(int key) {
        return this.accountMap.get(key);
    }

    public synchronized Account getAccount(String username) {
        for (Account account : this.accountMap.values()) {
            if (account.getUsername().equals(username)) {
                return account;
            }
        }
        return null;
    }

    public synchronized boolean accountExists(String username) {
        for (Account account : this.accountMap.values()) {
            if (account.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public synchronized ArrayList<String> getAccountsNames() {
        ArrayList<String> accountNames = new ArrayList<>();
        for (Account account : this.accountMap.values()) {
            accountNames.add(account.getUsername());
        }
        return accountNames;
    }

    public static void main(String[] args) {
        Server server = new Server();
        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]))) {
            while (true)
                new MessagingServer(serverSocket.accept(), server).start();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


}
