package Bai2;

import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5678;
    private String username;

    public static void main(String[] args) {
        new Client().start();
    }

    public void start() {
        try (
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {
            // Read and send username
            System.out.print("Enter your username: ");
            username = consoleReader.readLine();
            writer.write(username);
            writer.newLine();
            writer.flush();

            // Thread to read messages from the server
            new Thread(() -> {
                String message;
                try {
                    while ((message = reader.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();

            // Main thread to send messages to the server
            String message;
            while ((message = consoleReader.readLine()) != null) {
                writer.write(message);
                writer.newLine();
                writer.flush();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}