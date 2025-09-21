package org.example;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class ClientHandler implements Runnable{

    private Socket clientSocket;
    private CommandServer commandServer;
    private String userId;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    public ClientHandler(Socket clientSocket, CommandServer commandServer){
        this.commandServer=commandServer;
        this.userId = commandServer.createNewUserId();
        this.clientSocket=clientSocket;
    }


    @Override
    public void run() {
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            printWriter.println("User ID: " + userId);
            printWriter.println("Connection completed. Send command...");
            String command;
            while ((command = bufferedReader.readLine()) != null){
                if (command.equals("exit")){
                    break;
                }
                String result = executedCommand(command);
                printWriter.println(result);

            }
        }catch (IOException e){
            System.err.println("Client error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client " + userId + " connection disconnected.");
            } catch (IOException e){
                System.err.println("Connection error: " + e.getMessage());
            }
        }
    }
    private String executedCommand(String command){
        try {
            String userDirectory = commandServer.getUserDirectory(userId);
            if (command.startsWith("cd ")){
                return handleCdCommand(command, userDirectory);
            }
            if (command.equals("pwd")){
                return userDirectory;
            }
            return runSystemCommand(command, userDirectory);
        } catch (Exception e){
            return "Error: " + e.getMessage();
        }
    }


    private String handleCdCommand(String command, String currentDirectory){
        String newPath = command.substring(3).trim();
        File newDirectory;
        if (newPath.startsWith("/") || newPath.contains(":")){
            newDirectory = new File(newPath);
        }else {
            File currentDir = new File(currentDirectory);
            if (newPath.equals("..")){
                newDirectory = currentDir.getParentFile();
            } else if (newPath.equals(".")){
                newDirectory = currentDir;
            } else {
                newDirectory = new File(currentDir, newPath);
            }
        }
        if (newDirectory != null && newDirectory.exists() && newDirectory.isDirectory()){
            commandServer.updateUserDirectory(userId, newDirectory.getAbsolutePath());
            return newDirectory.getAbsolutePath();
        } else {
            return "Error: Path not found "+ newPath;
        }
    }

    private String runSystemCommand(String command, String workingDirectory) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            if (System.getProperty("os.name").toLowerCase().contains("windows")){
                processBuilder.command("cmd", "/c", command);
            }else {
                processBuilder.command("sh", "-c", command);
            }
            processBuilder.directory(new File(workingDirectory));
            Process process = processBuilder.start();

            BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = outputReader.readLine()) != null){
                output.append(line).append("\n");
            }
            BufferedReader errorReader  = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder error = new StringBuilder();
            while ((line  = errorReader.readLine()) != null){
                error.append(line).append("\n");
            }
            boolean finished = process.waitFor(30, TimeUnit.SECONDS);
            if (!finished){
                process.destroyForcibly();
                return "Error: Command timed out after 30 seconds";
            }
            String result = output.toString();
            if (!error.toString().isEmpty()){
                result += "ERROR: " + error.toString();
            }
            return result.isEmpty() ? "Command executed" : result.trim();
        } catch (Exception e){
            return "Error: "+ e.getMessage();
        }

    }


}
