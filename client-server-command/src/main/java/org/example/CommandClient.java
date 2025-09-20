package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class CommandClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args){
        try {
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            String userId = bufferedReader.readLine();
            System.out.println(userId);

            String connectionMessage = bufferedReader.readLine();
            System.out.println(connectionMessage);
            Thread serverListener = new Thread(() -> {
                try {
                    String line;
                    while ((line = bufferedReader.readLine()) != null){
                        System.out.println(line);
                    }
                }catch (IOException e){
                    System.out.println("Server disconnected");
                }
            });
            serverListener.start();
            String command;
            while (scanner.hasNextLine()){
                command = scanner.nextLine();
                printWriter.println(command);
                if (command.equals("exit")){
                    break;
                }
            }
            socket.close();
            System.err.println("Connection closed");
        } catch (IOException e){
            System.err.println("Cannot connected to server: "+ e.getMessage());
            System.err.println("Make sure the server running");
        }
    }
}
