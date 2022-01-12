package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket(args[0], Integer.parseInt(args[1]))) {
            BufferedReader echoes = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter stringToEcho = new PrintWriter(socket.getOutputStream(), true);
            String messageToServer = "";
            for (int i = 2; i < args.length; i++) {
                messageToServer += args[i] + " ";
            }
            stringToEcho.println(messageToServer);

            while ((messageToServer = echoes.readLine()) != null) {
                System.out.println(messageToServer);
            }

        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());

        }
    }
}
