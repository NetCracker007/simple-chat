
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

public class ChatServer {

    public static void runChat(MessageDispatcher messageDispatcher, Map<String, Socket> clientToSocketMapper,
                               Socket socket) throws IOException {
        System.out.println("Client socket :" + socket.toString());
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String clientName = input.readLine();
        System.out.println(clientName + " just logged in..");
        clientToSocketMapper.put(clientName, socket);
        messageDispatcher.sendPendingMessages(clientName);
        boolean clientLoggedIn = true;
        while (clientLoggedIn) {
            String message = input.readLine();
            if (message.equals("GoodBye")) {
                messageDispatcher.sendMessage(clientName, message);
                clientLoggedIn = false;
            } else {
                String receiver = message.split("->")[0];
                messageDispatcher.sendMessage(receiver, clientName + "<-" + message.split("->")[1]);
            }
        }
        clientToSocketMapper.remove(clientName);
        input.close();
        socket.close();
    }

    public static void main(String args[]) throws IOException {
        BlockingQueue<Runnable> arrayBlockingQueue = new ArrayBlockingQueue<>(10, true);
        ExecutorService executorService = new ThreadPoolExecutor(10, 20, 10000, TimeUnit.MILLISECONDS, arrayBlockingQueue,
                new ThreadPoolExecutor.AbortPolicy());
        Map<String, Socket> clientToSocketMapper = new HashMap<>();
        Map<String, Queue<String>> clientPendingMessage = new HashMap<>();
        MessageDispatcher messageDispatcher = new MessageDispatcher(clientToSocketMapper, clientPendingMessage);
        System.out.println("Starting up chat server ....");
        ServerSocket serverSocket = new ServerSocket(9906);
        System.out.println("Chat server started. Listening on port 9906");
        while (true) {
            Socket clientSocket = serverSocket.accept();
            executorService.submit(() -> {
                try {
                    runChat(messageDispatcher, clientToSocketMapper, clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
