package zad1;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class MainServer {
    private ServerSocket serverSocket;
    private ConcurrentHashMap<String, SocketAddress> dictionaryServers;

    public MainServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        dictionaryServers = new ConcurrentHashMap<>();
    }

    public void start() {
        System.out.println("Main Server started");
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleClient(Socket clientSocket) {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {
            CommunicationProtocol request = (CommunicationProtocol) in.readObject();
            if (!dictionaryServers.containsKey(request.languageCode)) {
                out.writeObject(new CommunicationProtocol("Language not supported", null, null));
                return;
            }
            SocketAddress dictionaryAddress = dictionaryServers.get(request.languageCode);
            try (Socket dictSocket = new Socket()) {
                dictSocket.connect(dictionaryAddress);
                try (ObjectOutputStream dictOut = new ObjectOutputStream(dictSocket.getOutputStream());
                     ObjectInputStream dictIn = new ObjectInputStream(dictSocket.getInputStream())) {
                    dictOut.writeObject(request);
                    CommunicationProtocol response = (CommunicationProtocol) dictIn.readObject();
                    out.writeObject(response);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void registerDictionaryServer(String languageCode, SocketAddress address) {
        dictionaryServers.put(languageCode, address);
    }

    public static void main(String[] args) throws IOException {
        MainServer server = new MainServer(8080);
        server.registerDictionaryServer("EN", new InetSocketAddress("localhost", 8081));
        server.registerDictionaryServer("FR", new InetSocketAddress("localhost", 8082));
        server.start();
    }
}