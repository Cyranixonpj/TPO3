package zad1;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class Client extends JFrame {
    private JTextField wordField;
    private JComboBox<String> languageSelector;
    private JButton translateButton;
    private JTextArea resultArea;

    public Client() {
        super("Dictionary Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new BorderLayout());

        JPanel northPanel = new JPanel();
        wordField = new JTextField(10);
        languageSelector = new JComboBox<>(new String[]{"EN", "FR"});
        translateButton = new JButton("Translate");
        northPanel.add(wordField);
        northPanel.add(languageSelector);
        northPanel.add(translateButton);

        resultArea = new JTextArea();
        resultArea.setEditable(false);

        add(northPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        translateButton.addActionListener(e -> translate());

        setVisible(true);
    }

    private void translate() {
        String word = wordField.getText();
        String language = (String) languageSelector.getSelectedItem();
        try (Socket socket = new Socket("localhost", 8080);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            out.writeObject(new CommunicationProtocol(word, language, null));
            CommunicationProtocol response = (CommunicationProtocol) in.readObject();
            resultArea.setText(response.word);
        } catch (IOException | ClassNotFoundException ex) {
            resultArea.setText("Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
