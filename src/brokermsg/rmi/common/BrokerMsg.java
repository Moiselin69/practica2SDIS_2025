package brokermsg.rmi.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfaz remota para operaciones básicas de usuario en el broker de mensajes
 */
public interface BrokerMsg extends Remote {
    
    /**
     * Autentica un usuario en el sistema
     * @param username Nombre de usuario
     * @param password Contraseña del usuario
     * @return AUTH si la autenticación es correcta, NOTAUTH en caso contrario
     * @throws RemoteException
     */
    String auth(String username, String password) throws RemoteException;
    
    /**
     * Añade un mensaje a una cola específica
     * @param queueName Nombre de la cola
     * @param message Mensaje a añadir
     * @throws RemoteException
     */
    void add2Q(String queueName, String message) throws RemoteException;
    
    /**
     * Añade un mensaje a la cola por defecto
     * @param message Mensaje a añadir
     * @throws RemoteException
     */
    void add2Q(String message) throws RemoteException;
    
    /**
     * Lee y elimina un mensaje de una cola específica
     * @param queueName Nombre de la cola
     * @return El mensaje leído o null si la cola está vacía
     * @throws RemoteException
     */
    String readQ(String queueName) throws RemoteException;
    
    /**
     * Lee y elimina un mensaje de la cola por defecto
     * @return El mensaje leído o null si la cola está vacía
     * @throws RemoteException
     */
    String readQ() throws RemoteException;
}

