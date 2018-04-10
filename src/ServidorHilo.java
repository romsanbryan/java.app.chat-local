
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import javax.swing.JOptionPane;

/**
 * Clase encargada de la comunicacion de los distintos clientes. 
 * Se ocupa de recibir/enviar los mensajes y de establecer para que nuevos usuarios puedan acceder si alguien abandona
 * 
 * @author romsabryan (Bryan Jesus Romero Santos)
 * @author rgones (Rafael Gonzalez Escobar)
 * @author javigon (Javier Gonzalez Guzman)
 * 
 * @version 1.0
 */
class ServidorHilo extends Thread {

	private DataInputStream is = null; // Recibe datos 
	private PrintStream os = null; // Envia datos
	private Socket clientSocket = null; // Socket del cliente
	private final ServidorHilo[] threads; // Array donde almacenamos todos los clientes conectados
	private int maxClientsCount; // Maximo de clientes permitidos

        /**
         * Constructor 
         * @param clientSocket Identificador del socket
         * @param threads Lista de los usuarios conectados
         */
	public ServidorHilo(Socket clientSocket, ServidorHilo[] threads) {
		this.clientSocket = clientSocket;
		this.threads = threads;
		maxClientsCount = threads.length;
	}
        
        /**
         * Metodo "run" de nuestra clase
         * 
         * @see Thread
         */
        @Override
	public void run() {
		try {
                    /*
                        Inicializa los streams, los conecta con el socket del cliente
                    */
			is = new DataInputStream(clientSocket.getInputStream());
			os = new PrintStream(clientSocket.getOutputStream());
			os.println("Por favor, introduce tu nombre: ");
			String name = is.readLine().trim();
                        //String name = JOptionPane.showInputDialog("Por favor, introduce tu nombre: ");


			os.println("Bienvenido, " + name + " a la sala de chat.\n");
		//	synchronized (this) {

				// Enviamos un mensaje a todos diciendo la nueva conexion
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] != null && threads[i] != this) {
						threads[i].os.println(name + " acaba de entrar.");
                                              //  threads[i].os.println(name);

					}
				}
		//	}
			// Empieza conversacion
			while (true) {
				String line = is.readLine();

				if (line.startsWith("/quit")) { // Si se recibe /quit desde el cliente, se termina la comunicacion
					break;
				}

				synchronized (this) { // Reenviamos mensajes del usuario y su comentario
					for (int i = 0; i < maxClientsCount; i++) {
						if (threads[i] != null) {
							threads[i].os.println("<" + name + "> " + line);
						}
					}
				}
			}

			//synchronized (this) { // Informamos de quien abandono el chat a los demas usuarios
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] != null && threads[i] != this) {
						threads[i].os.println("El usuario " + name + " ha dejado la sala de chat.");
					}
				}
			//}
			os.println("*** chao " + name);

			/*
                            Establecemos la posicion de quien abandono como null para que pueda entrar otro usuario
			 */
			//synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] == this) {
						threads[i] = null;
					}
				}
			//}
			/*
                            Cerramos el envio, recepcion y conexion
                        */
			is.close();
			os.close();
			clientSocket.close();
		} catch (IOException e) {
		}
	}
}
