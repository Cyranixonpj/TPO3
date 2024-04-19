package zad1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DictionaryServer implements Runnable {
    private ServerSocket serverSocket;
    private HashMap<String, String> dictionary;

    public DictionaryServer(int port, HashMap<String, String> dictionary) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.dictionary = dictionary;
    }

    @Override
    public void run() {
        System.out.println("Dictionary Server started on port " + serverSocket.getLocalPort());
        if (serverSocket.getLocalPort()==8081) {
            System.out.println("EN Dictionary Server started");
        } else {
            System.out.println("FR Dictionary Server started");
        }
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
        ExecutorService executor = Executors.newCachedThreadPool();

        HashMap<String, String> enDictionary = new HashMap<>();
        enDictionary.put("dom", "house");
        enDictionary.put("kot", "cat");

        HashMap<String, String> frDictionary = new HashMap<>();
        frDictionary.put("pies", "chien");
        frDictionary.put("kot", "chat");
        frDictionary.put("dom", "maison");

        executor.execute(new DictionaryServer(8081, enDictionary));
        executor.execute(new DictionaryServer(8082, frDictionary));
        executor.shutdown();
    }
}