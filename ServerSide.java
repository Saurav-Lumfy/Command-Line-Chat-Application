package ChatApplication;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSide {

    private ServerSocket server;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ServerSide() throws IOException {
        server = new ServerSocket(7777);
        System.out.println("Server is ready to accept the request.");
        System.out.println("Waiting for client...");
        socket = server.accept();

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
                        System.out.println("Client has left the chat.");
                        closeResources();
                        break;
                    }
                    System.out.println("Client: " + msg);
                }
            } catch (IOException e) {
                System.out.println("Connection closed or error occurred: " + e.getMessage());
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
                System.out.println("Error in writing: " + e.getMessage());
            }
        };
        new Thread(writeTask).start();
    }

    private void closeResources() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null && !socket.isClosed()) socket.close();
            if (server != null && !server.isClosed()) server.close();
            System.out.println("Server resources closed.");
        } catch (IOException e) {
            System.out.println("Error while closing resources: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("Server is starting...");
        try {
            new ServerSide();
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}
