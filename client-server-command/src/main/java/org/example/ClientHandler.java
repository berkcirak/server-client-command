package org.example;

import java.net.Socket;

public class ClientHandler implements Runnable{

    private Socket clientSocket;
    private CommandServer commandServer;

    public ClientHandler(Socket clientSocket, CommandServer commandServer){
        this.commandServer=commandServer;
    }


    @Override
    public void run() {

    }
}
