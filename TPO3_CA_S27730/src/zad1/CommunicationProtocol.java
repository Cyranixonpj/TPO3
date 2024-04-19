package zad1;

import java.io.Serializable;

public class CommunicationProtocol implements Serializable {
    String word;
    String languageCode;
    Integer port;

    public CommunicationProtocol(String word, String languageCode, Integer port) {
        this.word = word;
        this.languageCode = languageCode;
        this.port = port;
    }
}