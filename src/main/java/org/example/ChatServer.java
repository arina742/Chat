package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private static List<Socket> clients = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        System.out.println("Сервер запущен!");
        ServerSocket serverSocket = new ServerSocket(5555);
        while (true){
            Socket clientSocket = serverSocket.accept();
            clients.add(clientSocket);
            Thread clientThread = new Thread(new ClientHandler(clientSocket));
            clientThread.start();
        }
    }
    public static class ClientHandler implements Runnable{
        private Socket clientSocket;
        private PrintWriter writer;

        public ClientHandler(Socket clientSocket){
            this.clientSocket = clientSocket;
        }
        @Override
        public void run() {

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new PrintWriter(clientSocket.getOutputStream(), true);
                String userName = reader.readLine();
                sendMsgToAllClients("Пользователь " + userName + " присоединился к чату", userName);
                String clientMsg;
                while ((clientMsg = reader.readLine()) != null){
                    sendMsgToAllClients(clientMsg, userName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                    clients.remove(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        private void sendMsgToAllClients(String message, String senderUsername){
            for(Socket client : clients){
                try {
                    PrintWriter clientWriter = new PrintWriter(client.getOutputStream(), true);
                    if(message.contains("Пользователь") && message.contains("присоединился к чату")){
                        clientWriter.println( message);
                    } else {
                        clientWriter.println(senderUsername + ": " + message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}