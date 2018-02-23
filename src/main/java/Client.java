import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by 11120 on 2/21/18.
 */
public class Client {

    private class ChatListenerThread extends Thread {
        Socket serverSocket;

        public ChatListenerThread(Socket serverSocket) {
            super();
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            try {
                BufferedReader input =
                        new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
                String message = "";
                do {
                    message = input.readLine();
                    if (!message.equals("GoodBye"))
                        System.out.println(message);
                }
                while (!message.equals("GoodBye"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ChatWriterThread extends Thread {
        Socket serverSocket;

        public ChatWriterThread(Socket serverSocket) {
            super();
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            try {
                BufferedReader input =
                        new BufferedReader(new InputStreamReader(System.in));
                PrintWriter out =
                        new PrintWriter(serverSocket.getOutputStream(), true);
                System.out.println("Enter your user name");
                String userName = input.readLine();
                System.out.println("Logging in as :" + userName);
                out.write(userName+'\n');
                out.flush();
                System.out.println("Logged in as:" + userName);
                String message = "";
                do {
                    message = input.readLine();
                    out.write(message + '\n');
                    out.flush();
                }
                while (!message.equals("GoodBye"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) throws IOException {
        System.out.println("Connecting to server....");
        Socket socket = new Socket("localhost", 9906);
        System.out.println("Connection established");
        new Client().new ChatWriterThread(socket).start();
        new Client().new ChatListenerThread(socket).start();
    }
}
