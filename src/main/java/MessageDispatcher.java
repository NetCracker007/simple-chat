import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

/**
 * Created by 11120 on 2/21/18.
 */
public class MessageDispatcher {
    Map<String, Socket> clientToSocketMapper;
    Map<String, Queue<String>> clientPendingMessage;

    public MessageDispatcher(Map<String, Socket> clientToSocketMapper, Map<String, Queue<String>> clientPendingMessage) {
        this.clientPendingMessage = clientPendingMessage;
        this.clientToSocketMapper = clientToSocketMapper;
    }

    public void sendPendingMessages(String client) throws IOException {
        if (clientToSocketMapper.containsKey(client) && clientPendingMessage.containsKey(client) &&
                clientPendingMessage.get(client).size() > 0) {
            Socket clientSocket = clientToSocketMapper.get(client);
            PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            while (clientPendingMessage.get(client).size() >0) {
                printWriter.write(clientPendingMessage.get(client).remove() + '\n');
            }
            printWriter.flush();
            clientPendingMessage.remove(client);
        }
    }

    public void sendMessage(String client, String message) throws IOException {
        if (clientToSocketMapper.containsKey(client))
        {
            System.out.println("Outbond message for "+ client + " :" + message);
            Socket clientSocket = clientToSocketMapper.get(client);
            PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            printWriter.write(message + '\n');
            printWriter.flush();
        }
        else {
            if (clientPendingMessage.containsKey(client))
            {
                clientPendingMessage.get(client).add(message);
            }
            else {
                clientPendingMessage.put(client, new LinkedList<String>(){{
                    add(message);
                }});
            }
        }
    }
}
