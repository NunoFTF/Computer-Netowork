import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//o sv será responsável por ouvir clientes que se querem conectar

public class Server {

    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) { //contrutor do server socket
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            // á espera de clientes na porta 1234.
            while (!serverSocket.isClosed()) { //enquanto está aberto
                
                Socket socket = serverSocket.accept(); //aceita clientes enquanto está aberto
                System.out.println("Um novo usuário juntou-se ao chat!"); 
                ClientHandler clientHandler = new ClientHandler(socket); 
                Thread thread = new Thread(clientHandler);
                
                thread.start(); //começar a execução da thread
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    // Função para fechar o socket.
    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // CORRER O PROGRAMA.
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234); //criação do server socket
        Server server = new Server(serverSocket);
        server.startServer(); //para manter o server a correr
    }

}
