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
     */
    String deleteQ(String queueName) throws RemoteException;
    
    /**
     * Lee sin eliminar un mensaje de una cola específica
     * @param queueName Nombre de la cola
     * @return El primer mensaje en la cola sin eliminarlo
     * @throws RemoteException
     */
    String peekQ(String queueName) throws RemoteException;
    
    /**
     * Lee sin eliminar un mensaje de la cola por defecto
     * @return El primer mensaje en la cola por defecto
     * @throws RemoteException
     */
    String peekQ() throws RemoteException;
    
    /**
     * Obtiene información sobre el estado de una cola específica
     * @param queueName Nombre de la cola
     * @return Información del estado de la cola
     * @throws RemoteException
     */
    String state(String queueName) throws RemoteException;
    
    /**
     * Obtiene la lista de todas las colas disponibles en el sistema
     * @return String con la lista de colas disponibles
     * @throws RemoteException
     */
    String getQueueList() throws RemoteException;
}

