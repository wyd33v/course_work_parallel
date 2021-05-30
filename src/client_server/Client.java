package client_server;
import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * создание клиента со всеми необходимыми утилитами, точка входа в программу в классе Client
 */

class ClientLogic {

    private Socket socket;
    private static BufferedReader inputUser; // нам нужен ридер читающий с консоли, иначе как мы узнаем что хочет сказать клиент?
    private static ObjectInputStream in; // поток чтения из сокета
    private static ObjectOutputStream out; // поток записи в сокет
    private String addr; // ip адрес клиента
    private int port; // порт соединения
    private String nickname; // имя клиента
    private Date time;
    private SimpleDateFormat dt1;

    public ClientLogic(String addr, int port) {
        this.addr = addr;
        this.port = port;
        try {
            this.socket = new Socket(addr, port);
        } catch (IOException e) {
            System.err.println("Socket failed");
        }
        try {
            // потоки чтения из сокета / записи в сокет, и чтения с консоли
            inputUser = new BufferedReader(new InputStreamReader(System.in));
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            new WriteMsg().start(); // нить пишущая сообщения в сокет приходящие с консоли в бесконечном цикле
        } catch (IOException e) {
            // Сокет должен быть закрыт при любой
            // ошибке, кроме ошибки конструктора сокета:
            ClientLogic.this.downService();
        }
        // В противном случае сокет будет закрыт
        // в методе run() нити.
    }

    /**
     * закрытие сокета
     */
    private void downService() {
        try {
            if (!socket.isClosed()) {
                inputUser.close();
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {
            System.out.println("client downService exception");
        }
    }


    public class WriteMsg extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    // send Task to the server
                    System.out.println("Введите номер операции: ");
                    System.out.println("1 - Поиск по одному слову ");
                    System.out.println("2 - Выход ");
                    int operationNumber = Integer.parseInt(inputUser.readLine());
                    if(operationNumber==2){
                        out.writeInt(operationNumber);
                        out.flush();
                        downService();
                        return;
                    }

                    out.writeInt(operationNumber);
                    System.out.print("Введите слово: ");
                    String word = inputUser.readLine();
                    out.writeObject(word);
                    out.flush();

                    // receive answer from server
                    System.out.println("Список документов: ");
                    List<String> filenames = (List)in.readObject();

                    for (int i = 0; i < filenames.size(); i++) {

                        System.out.println(i + ") " + filenames.get(i));
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

public class Client {

    public static String ipAddr = "localhost";
    public static int port = 8000;


    public static void main(String[] args) {
        new ClientLogic(ipAddr, port);
    }
}