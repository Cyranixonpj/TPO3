package zad1;

import java.net.*;
import java.io.*;
import java.util.HashMap;

public class DictionaryServer {
    private ServerSocket serverSocket;
    private HashMap<String, String> dictionary;

    public DictionaryServer(int port, HashMap<String, String> dictionary) throws IOException {
        serverSocket = new ServerSocket(port);
        this.dictionary = dictionary;
    }

    public void start() {
        System.out.println("Dictionary Server started on port " + serverSocket.getLocalPort());
        while (true) {
            try (Socket clientSocket = serverSocket.accept();
                 ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {
                CommunicationProtocol request = (CommunicationProtocol) in.readObject();
                String translation = dictionary.getOrDefault(request.word, "No translation found");
                out.writeObject(new CommunicationProtocol(translation, null, null));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        HashMap<String, String> enDictionary = new HashMap<>();
        enDictionary.put("dom", "house");
        enDictionary.put("kot", "cat");
        HashMap<String,String>frdictionary = new HashMap<>();
        frdictionary.put("pies", "chien");
        frdictionary.put("kot", "chat");
        frdictionary.put("dom", "maison");
        DictionaryServer enServer = new DictionaryServer(8081, enDictionary);
        DictionaryServer frServer = new DictionaryServer(8082, frdictionary);
        frServer.start();
        enServer.start();
    }
}
