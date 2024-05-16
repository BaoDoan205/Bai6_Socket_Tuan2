package Bai1;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8888;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            DataInputStream in = new DataInputStream(socket.getInputStream());

            while (true) {
                int number = in.readInt();
                System.out.println("Received number: " + number);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}