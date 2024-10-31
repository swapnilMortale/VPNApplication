import java.net.*;
import java.io.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Scanner;

public class VPNClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080)) {
            System.out.println("Connected to the VPN server");

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            // Receive AES key from server
            byte[] encodedKey = (byte[]) input.readObject();
            SecretKey aesKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

            System.out.println("Secure communication established with server.");

            // Communication loop
            Scanner scanner = new Scanner(System.in);
            String message;
            while (true) {
                System.out.print("Enter message to send (type 'exit' to quit): ");
                message = scanner.nextLine();

                if (message.equalsIgnoreCase("exit")) {
                    break; // Exit the loop
                }

                // Send message to the server
                output.writeObject(message);
                output.flush();

                // Wait for the server's response
                String response = (String) input.readObject();
                System.out.println(response);
            }
            scanner.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
