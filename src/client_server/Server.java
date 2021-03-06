package client_server;
import index.InvertedIndexParallel;
import parser.RegexParser;
import source.FileDocumentSource;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


class ServerLogic extends Thread {

    private Socket socket; // сокет, через который сервер общается с клиентом,
    // кроме него - клиент и сервер никак не связаны
    private static ObjectInputStream in; // поток чтения из сокета
    private static ObjectOutputStream out; // поток записи в сокет
    InvertedIndexParallel invertedIndex;


    public ServerLogic(Socket socket, InvertedIndexParallel invertedIndex) throws IOException {
        this.socket = socket;
        // если потоку ввода/вывода приведут к генерированию искдючения, оно проброситься дальше
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        this.invertedIndex = invertedIndex;
        start(); // вызываем run()
    }


    @Override
    public void run() {
        try {
            while (true) {
                int operationNumber = in.readInt();
                if(operationNumber==2){
                    System.out.println("server stop listening client");
                    downService();
                    return;
                }
                System.out.println("operationNumber = " + operationNumber);
                String word = (String) in.readObject();
                System.out.println("try to find word " + word + " in docs");
                Set<Path> paths = invertedIndex.search(word);
                List<String> filenames = paths.stream().map(x->x.toString()).
                        collect(Collectors.toList());
                System.out.println("Result: ");
                System.out.println(filenames);
                out.writeObject(filenames);
                out.flush(); // выталкиваем все из буфера
            }
        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            this.downService();
        }
    }


    private void downService() {
        try {
            if(!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                Server.serverList.remove(this);
                interrupt();
            }
        } catch (IOException ignored) {}
    }
}



public class Server {

    public static final int PORT = 8000;
    public static LinkedList<ServerLogic> serverList = new LinkedList<>(); // список всех нитей - экземпляров сервера, слушающих каждый своего клиента
    private static InvertedIndexParallel invertedIndex;

    public static void createIndex() throws IOException {
        FileDocumentSource source = new FileDocumentSource();
        String mainFolder = "C:\\course_work_parallel_computing\\aclImdb";
        long startTime = System.currentTimeMillis();
        source.add(mainFolder + "\\test\\neg", 8250, 8500);
        source.add(mainFolder + "\\test\\pos", 8250, 8500);
        source.add(mainFolder + "\\train\\neg", 8250, 8500);
        source.add(mainFolder + "\\train\\pos", 8250, 8500);
        source.add(mainFolder + "\\train\\unsup", 33000, 34000); // 33000, 34000
        invertedIndex = new InvertedIndexParallel();
        invertedIndex.createInvertedIndex(4, source, new RegexParser());
        System.out.println("time of Create index " + (System.currentTimeMillis() - startTime) + " ms");
    }

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Server Started");
        createIndex();
        try {
            while (true) {
                // Блокируется до возникновения нового соединения:
                Socket socket = server.accept();
                System.out.println("Client was accepted");
                try {
                    serverList.add(new ServerLogic(socket,invertedIndex)); // добавить новое соединенние в список
                    System.out.println("Client added to serverList");
                } catch (IOException e) {
                    // Если завершится неудачей, закрывается сокет,
                    // в противном случае, нить закроет его:
                    socket.close();
                }
                System.out.println("number of clients = " + serverList.size());
            }
        } finally {
            server.close();
        }
    }

}