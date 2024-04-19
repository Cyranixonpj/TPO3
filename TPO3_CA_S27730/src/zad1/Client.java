package zad1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client extends JFrame {
    private JTextField wordField;
    private JComboBox<String> languageSelector;
    private JTextField portField; // Nowe pole dla portu
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
        portField = new JTextField(5); // Inicjalizacja pola portu
        translateButton = new JButton("Translate");
        northPanel.add(wordField);
        northPanel.add(languageSelector);
        northPanel.add(portField); // Dodanie pola portu
        northPanel.add(translateButton);

        resultArea = new JTextArea();
        resultArea.setEditable(false);

        add(northPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        translateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                translate();
            }
        });

        setVisible(true);
    }

    private void translate() {
        String word = wordField.getText();
        String language = (String) languageSelector.getSelectedItem();
        int port = Integer.parseInt(portField.getText()); // Pobierz wartość portu

        try (Socket socket = new Socket("localhost", 8080);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            out.writeObject(new CommunicationProtocol(word, language, port)); // Przekazanie portu do protokołu
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