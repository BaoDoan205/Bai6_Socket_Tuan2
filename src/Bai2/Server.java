package Bai2;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 5678;
    private static Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        System.out.println("Chat server started...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void broadcast(String message, ClientHandler excludeClient) {
        synchronized (clientHandlers) {
            for (ClientHandler clientHandler : clientHandlers) {
                if (clientHandler != excludeClient) {
                    clientHandler.sendMessage(message);
                }
            }
        }
    }

    public static void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }
}

class ClientHandler extends Thread {
    private Socket socket;
    private String username;
    private BufferedWriter writer;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {
            this.writer = writer;

            // Request username
            writer.write("Enter your username: ");
            writer.flush();
            this.username = reader.readLine();
            Server.broadcast(username + " has joined the chat!", this);

            String message;
            while ((message = reader.readLine()) != null) {
                Server.broadcast(username + ": " + message, this);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            Server.removeClient(this);
            Server.broadcast(username + " has left the chat.", this);
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}