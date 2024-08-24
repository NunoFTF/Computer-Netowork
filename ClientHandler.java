import java.io.*;
import java.net.Socket;
import java.util.ArrayList;



// é uma classe runnable que permite que diferentes operações sejam feitas em diferentes threads.
public class ClientHandler implements Runnable {

    // Vetor com todos os clientes conectados (todas as funções a executar).
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket; //criação do Socket para a conecção
    private BufferedReader bufferedReader; //Para receber(ler) data
    private BufferedWriter bufferedWriter; //para enviar data
    private String clientUsername;

    // criação do clientHandler.
    public ClientHandler(Socket socket) { //há a passagem do objeto socket do servidor
        try {
            //propriedades da classe ClientHandler
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            
            this.clientUsername = bufferedReader.readLine(); //Quando o cliente se conecta, o seu Nickname é enviado (o que está no sv).
            
            clientHandlers.add(this); //Adicionar o novo cliente ao vetor de clientes.

            broadcastMessage("SERVER: " + clientUsername + " entrou no chat!");

        } catch (IOException e) {
            // Fechar tudo.
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Tudo neste método corre num thread diferente porque a operação (bufferedReader.readLine()) bloqueia o programa enquanto não receber uma msg.
    @Override
    public void run() {

        String messageFromClient;
        // Continua á escuta enquanto o socket está em aberto.
        while (socket.isConnected()) {
            try {
                // Lê a mensagem e envia a todos os outros clientes.
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);

            } catch (IOException e) {
                // Fechar tudo.
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

     //Para enviar mensagens a todos os clientes
    public void broadcastMessage(String messageToSend) { //criação do método Broadcast acima usado
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                // Para não enviar a mensagem ao cliente que a escreveu.
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                // Fechar tudo.
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    // Método para tirar o cliente do vetor caso se desconecte.
    public void removeClientHandler() {
        clientHandlers.remove(this); //remover o cliente em que estamos no vetor
        broadcastMessage("SERVER: " + clientUsername + " Saiu do chat!");
    }

    // Método para fechar tudo.
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
       

        // Remover clientes desconectados da Lista.
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
