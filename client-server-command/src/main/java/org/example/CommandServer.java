package org.example;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class CommandServer {

    private static final int PORT = 8080;
    private ServerSocket serverSocket;
    private Map<String, String> userDirectories;

    public CommandServer(){
        this.userDirectories = new ConcurrentHashMap<>();
    }
    public void start() throws IOException{
        serverSocket = new ServerSocket(PORT);
        System.out.println("Server started. PORT: " + PORT);
        System.out.println("Client waiting...");
        while (true){
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: "+clientSocket.getInetAddress());
            new Thread(new ClientHandler(clientSocket, this)).start();
        }
    }

    public String createNewUserId(){
        return UUID.randomUUID().toString().substring(0,8);
    }

    public String getUserDirectory(String userId){
        if (!userDirectories.containsKey(userId)){
            String tempDirectory = System.getProperty("java.io.tmpdir")+ File.separator + "user_" + userId;
            new File(tempDirectory).mkdirs();
            userDirectories.put(userId, tempDirectory);
            System.out.println("New directory path: "+ tempDirectory);
        }
        return userDirectories.get(userId);
    }
    public void updateUserDirectory(String userId, String newDirectory){
        userDirectories.put(userId, newDirectory);
    }
    public static void main(String[] main){
        CommandServer server = new CommandServer();
        try {
            server.start();
        } catch (IOException e){
            System.err.println("Server cannot started..." + e.getMessage());
        }
    }



}
