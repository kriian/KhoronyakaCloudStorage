package com.hehnev.client.io;

import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Client {

    private Path root;
    private byte[] buff;
    private DataInputStream dis;
    private DataOutputStream dos;


    public Client() throws IOException {
        Socket socket = new Socket("localhost", 8189);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        root = Paths.get("root");
        if (!Files.exists(root)) {
            try {
                Files.createDirectory(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        buff = new byte[1024];
    }

    public DataInputStream getDis() {
        return dis;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    public void showFilesName(ListView<String> listView, TextField input) throws IOException {
        List<String> listFiles = Files.list(root)
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toList());
        listView.getItems().addAll(listFiles);
        setMouseClicked(listView, input);
    }

    private void setMouseClicked(ListView<String> listView, TextField input) {
        listView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                String fileName = listView.getSelectionModel().getSelectedItem();
                if (!Files.isDirectory(root.resolve(fileName))) {
                    input.setText(fileName);
                    input.requestFocus();
                } else {
                    input.setText("Select file! Not directory.");
                }
            }
        });
    }

    public void sendBytesStream(TextField input) throws IOException {
        String fileName = input.getText();
        input.clear();
        Path filePath = root.resolve(fileName);
        if (Files.exists(filePath)) {
            dos.writeUTF(fileName);
            dos.writeLong(Files.size(filePath));
            try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
                int read = 0;
                while ((read = fis.read(buff)) != -1) {
                    dos.write(buff, 0, read);
                }
            }
            dos.flush();
        }
    }
}
