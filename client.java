import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class client {

    //declaração das variáveis a ser usadas nesta classe
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username; //representar o usuário

    public client(Socket socket, String username) { //construtor do cliente
        try {
            this.socket = socket;
            this.username = username;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            // Fechar tudo.
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    
    public void sendMessage() {
        try {
            
            bufferedWriter.write(username); //envia o nickname
            bufferedWriter.newLine();   
            bufferedWriter.flush();     //preencher o resto do buffer

            try (// criar um scanner para o input do client.
            Scanner scanner = new Scanner(System.in)) {
                // continuar enquanto há conecção.

                while (socket.isConnected()) {
                    String messageToSend = scanner.nextLine();
                    bufferedWriter.write(username + ": " + messageToSend);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
        } catch (IOException e) {
            // Fechar tudo.
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Esperar pela mensagem bloqueia o programa, por isso convem correr noutro thread.
    //metodo para receber mensagens

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;

                // Continuar enquanto há conecção.
                while (socket.isConnected()) {
                    try {
                        // Mostrar a mensagem.
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);
                    } catch (IOException e) {
                        // Fechar tudo.
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    // Método para fechar tudo.
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
      

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

    // CORRER O PROGRAMA.
    public static void main(String[] args) throws IOException {

        try (// Pedir o NickName e criar o socket.
        Scanner scanner = new Scanner(System.in)) {
            System.out.print("Digita o teu NickName: ");
            String username = scanner.nextLine();
            // Criar o socket para se conectar ao server.
            Socket socket = new Socket("localhost", 1234);

            // dar ao cliente o socket e o NickName.
            client client = new client(socket, username);
            
            // Loop infinito para ler as mesnagens.
            client.listenForMessage();
            client.sendMessage();
        }
    }
}
