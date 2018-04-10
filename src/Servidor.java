import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import javax.swing.JOptionPane;

/**
 * Servidor de nuestro chat. 
 * Se encarga de permitir que todos los clientes puedan acceder a nuestra sala y que puedan iniciar las comunicaciones
 * 
 * @author romsabryan (Bryan Jesus Romero Santos)
 * @author rgones (Rafael Gonzalez Escobar)
 * @author javigon (Javier Gonzalez Guzman)
 * 
 * @version 1.0
 */
public class Servidor {

	private static ServerSocket serverSocket = null; // Socket servidor
	private static Socket clientSocket = null; // Socket cliente
	private static final int maxClientsCount = 10; // Numero maximo de clientes que se conectaran simultaneamente a nuestro servidor
	private static final ServidorHilo[] threads = new ServidorHilo[maxClientsCount];
        
        private static final int portNumber = 5555; // Puerto que escucha nuestro servidor


	public static void main(String args[]) {
            /*
                Iniciamos nuestro servidor
            */
            System.out.print("a");
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			System.out.println(e);
		}

		while (true) { // Establecemos que siempre repita la conexion para evitar que se cierre y tener que volverlo a activar
			try {
				clientSocket = serverSocket.accept(); // Aceptamos la conexion
				int i = 0;
                                /*
                                    Vamos añadiendo clientes hasta llegar al limite. Hasta entonces se van creando hilos Clientes que se conectan
                                */
				for (i = 0; i < maxClientsCount; i++) {
					if (threads[i] == null) {
						(threads[i] = new ServidorHilo(clientSocket, threads)).start();
						break;
					}
				}
				/*
                                    Si llegamos al maximo de clientes enviamos un mensaje de sala llena
                                */
				if (i == maxClientsCount) {
					PrintStream os = new PrintStream(clientSocket.getOutputStream());
					os.println("Sala llena, por favor intentelo más tarde.");
					os.close();
					clientSocket.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
}
