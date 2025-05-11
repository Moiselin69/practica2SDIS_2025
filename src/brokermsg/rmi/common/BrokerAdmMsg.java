package brokermsg.rmi.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfaz remota para operaciones administrativas en el broker de mensajes
 */
public interface BrokerAdmMsg extends Remote, BrokerMsg {

    /**
     * Elimina una cola completa
     * @param queueName Nombre de la cola a eliminar
     * @return DELETED si se eliminó correctamente, EMPTY si la cola no existía
     * @throws RemoteException
     * @throws NotAuthException
     */
    String deleteQ(String token, String queueName) throws RemoteException, NotAuthException;
    
    /**
     * Lee sin eliminar un mensaje de una cola específica
     * @param queueName Nombre de la cola
     * @return El primer mensaje en la cola sin eliminarlo
     * @throws RemoteException
     */
    String peekQ(String token, String queueName) throws RemoteException, NotAuthException;
    
    /**
     * Lee sin eliminar un mensaje de la cola por defecto
     * @return El primer mensaje en la cola por defecto
     * @throws RemoteException
     */
    String peekQ(String token) throws RemoteException, NotAuthException;
    
    /**
     * Obtiene información sobre el estado de una cola específica
     * @param queueName Nombre de la cola
     * @return Información del estado de la cola
     * @throws RemoteException
     */
    String state(String token, String queueName) throws RemoteException, NotAuthException;
    
    /**
     * Obtiene la lista de todas las colas disponibles en el sistema
     * @return String con la lista de colas disponibles
     * @throws RemoteException
     */
    String getQueueList(String token) throws RemoteException, NotAuthException;
    
    /**
     * Obtiene separado por ":" las peticiones de acceso a nuevos usuarios
     * @return Devuelve el nombre de los diferentes usuarios separados por ":"
     * @throws RemoteException
     */
    String verPeticiones(String token) throws RemoteException, NotAuthException;
    
    /**
     * Acepta toda la lista de peticiones de acceso a nuevos usuarios
     * @throws RemoteException
     */
    void aceptarNuevosUsuarios(String token) throws RemoteException, NotAuthException;
    
    /**
     * Acepta a un usuario en concreto de la lista de peticiones de acceso a nuevos usuarios
     * @param usuario El nombre de usuario de la lista de peticiones de acceso.
     * @throws RemoteException
     */
    void aceptarNuevosUsuarios(String token, String usuario) throws RemoteException, NotAuthException;
}

