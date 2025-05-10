package brokermsg.rmi.client.unit;

import brokermsg.rmi.common.BrokerMsg;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 * Cliente que lee mensajes continuamente de una cola hasta que está vacía
 */
public class ContinuousReadClient {
    private static final int PUERTO_RMI = 1099;
    private static final String BROKER_USER_NAME = "BrokerUser";
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        try {
            // Obtener referencia al registro RMI
            Registry registry = LocateRegistry.getRegistry("localhost", PUERTO_RMI);
            
            // Obtener referencia al objeto remoto de usuario
            BrokerMsg brokerUser = (BrokerMsg) registry.lookup(BROKER_USER_NAME);
            
            System.out.println("Cliente de lectura continua conectado.");
            
            // Autenticación
            System.out.println("Introduzca su nombre de usuario:");
            String username = scanner.nextLine();
            System.out.println("Introduzca su contraseña:");
            String password = scanner.nextLine();
            
            String result = brokerUser.auth(username, password);
            
            if (result.equals("AUTH")) {
                System.out.println("Autenticación exitosa.");
                
                // Preguntar por la cola
                System.out.println("Introduzca el nombre de la cola que desea leer:");
                String queueName = scanner.nextLine();
                
                System.out.println("\n=== LECTURA CONTINUA DE LA COLA: " + queueName + " ===");
                int messageCount = 0;
                
                // Leer continuamente hasta que la cola esté vacía
                String message;
                while ((message = brokerUser.readQ(queueName)) != null) {
                    messageCount++;
                    System.out.println("Mensaje #" + messageCount + ": " + message);
                    
                    // Preguntar si quiere continuar después de cada mensaje
                    System.out.println("\n¿Desea continuar leyendo? (S/N)");
                    String continuar = scanner.nextLine();
                    if (!continuar.equalsIgnoreCase("S")) {
                        break;
                    }
                }
                
                if (messageCount == 0) {
                    System.out.println("La cola está vacía o no existe.");
                } else {
                    System.out.println("\nSe han leído " + messageCount + " mensajes de la cola " + queueName);
                }
                
            } else {
                System.out.println("Error de autenticación: " + result);
            }
            
        } catch (RemoteException e) {
            System.out.println("Error en la comunicación RMI: " + e.getMessage());
            e.printStackTrace();
        } catch (NotBoundException e) {
            System.out.println("No se encontró el objeto remoto en el registro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
