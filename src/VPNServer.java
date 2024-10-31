import java.net.*;
import java.io.*;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PrivateKey;

public class VPNServer {
    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server is listening on port 8080");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                // Generate AES key and RSA key pair
                SecretKey aesKey = EncryptionUtil.generateAESKey();
                KeyPair rsaKeyPair = EncryptionUtil.generateRSAKeyPair();

                // Start a new thread to handle the client connection
                new ServerThread(socket, aesKey, rsaKeyPair.getPrivate()).start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

class ServerThread extends Thread {
    private Socket socket;
    private SecretKey aesKey;
    private PrivateKey rsaPrivateKey;

    public ServerThread(Socket socket, SecretKey aesKey, PrivateKey rsaPrivateKey) {
        this.socket = socket;
        this.aesKey = aesKey;
        this.rsaPrivateKey = rsaPrivateKey;
    }

    public void run() {
        try (ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            // Send AES key to client
            output.writeObject(aesKey.getEncoded());
            output.flush();

            System.out.println("Secure communication established.");

            // Communication loop
            String message;
            while ((message = (String) input.readObject()) != null) {
                System.out.println("Received from client: " + message);
                // Echo the message back to the client
                output.writeObject("Server: " + message);
                output.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
