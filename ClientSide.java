package ChatApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSide {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientSide() throws IOException {
        System.out.println("Client started...");
        System.out.println("Sending request to server...");
        socket = new Socket("localhost", 7777);
        System.out.println("Connected to server.");

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true); // auto-flush enabled

        startRead();
        startWrite();
    }

    public void startRead() {
        Runnable readTask = () -> {
            System.out.println("Reader started...");
            try {
                String msg;
                while ((msg = reader.readLine()) != null) {
                    if ("bye".equalsIgnoreCase(msg.trim())) {
                        System.out.println("Server has left the chat.");
                        closeResources();
                        break;
                    }
                    System.out.println("Server: " + msg);
                }
            } catch (IOException e) {
                System.out.println("Error while reading: " + e.getMessage());
            }
        };
        new Thread(readTask).start();
    }

    public void startWrite() {
        Runnable writeTask = () -> {
            System.out.println("Writer started...");
            try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
                String msgSend;
                while ((msgSend = consoleReader.readLine()) != null) {
                    writer.println(msgSend);
                    if ("bye".equalsIgnoreCase(msgSend.trim())) {
                        closeResources();
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error while writing: " + e.getMessage());
            }
        };
        new Thread(writeTask).start();
    }

    private void closeResources() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Client resources closed.");
        } catch (IOException e) {
            System.out.println("Error while closing resources: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("Client is connecting to the server...");
        try {
            new ClientSide();
        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
}
