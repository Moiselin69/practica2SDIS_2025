package brokermsg.rmi.client.unit;

import brokermsg.rmi.common.BrokerAdmMsg;
import brokermsg.rmi.common.BrokerMsg;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 * Cliente específico para listar todas las colas disponibles en el sistema
 */
public class ListQueuesClient {
    private static final int PUERTO_RMI = 1099;
    private static final String BROKER_ADMIN_NAME = "BrokerAdmin";
    private static final String BROKER_USER_NAME = "BrokerUser";
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        try {
            // Obtener referencia al registro RMI
            Registry registry = LocateRegistry.getRegistry("localhost", PUERTO_RMI);
            
            // Obtener referencias a los objetos remotos
            BrokerAdmMsg brokerAdmin = (BrokerAdmMsg) registry.lookup(BROKER_ADMIN_NAME);
            BrokerMsg brokerUser = (BrokerMsg) registry.lookup(BROKER_USER_NAME);
            
            System.out.println("Cliente para listar colas conectado.");
            
            // Autenticación
            System.out.println("Introduzca su nombre de usuario:");
            String username = scanner.nextLine();
            System.out.println("Introduzca su contraseña:");
            String password = scanner.nextLine();
            
            String result = brokerUser.auth(username, password);
            
            if (result.equals("AUTH")) {
                System.out.println("Autenticación exitosa.");
                
                // Listar las colas
                String queueList = brokerAdmin.getQueueList();
                System.out.println("\n=== LISTADO DE COLAS DISPONIBLES ===");
                System.out.println(queueList);
                System.out.println("====================================");
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