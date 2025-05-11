package sdis.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MultiMap {
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> multiMapa;

    public MultiMap() {
    	multiMapa = new ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>();
    	multiMapa.put("COLA DEFAULT", new ConcurrentLinkedQueue<String>());
    }
    public MultiMap(ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> multiMapa){
        this.multiMapa = multiMapa;
        if (!this.multiMapa.contains("COLA DEFAULT"))
        	multiMapa.put("COLA DEFAULT", new ConcurrentLinkedQueue<String>());
    }

    /**
     * Metodo para introducir un mensaje a una cola de mensajes, si la cola no existe la crea.
     * @param clave nombre de la cola de mensajes
     * @param mensaje el mensaje que quieres añadir a la cola
     */
    public void push(String clave, String mensaje){
        if (!multiMapa.containsKey(clave))
            multiMapa.put(clave, new ConcurrentLinkedQueue<String>());
        multiMapa.get(clave).add(mensaje);
    }

    /**
     * Metodo para saber el útlimo mensaje de una cola de mensajes, si la cola no existe devuelve null
     * @param clave  Nombre de la cola que deseas extraer el ultimo mensaje
     * @return Devuelve el ultimo mensaje de la cola eliminandolo de la cola de mensajes
     */
    public String pull(String clave){
    	ConcurrentLinkedQueue<String> queue = multiMapa.get(clave);
    	if (queue == null) return null;
    	return queue.poll(); // devuelve null si está vacía
    }
    /**
     * Metodo para obtener el último mensaje de una cola de mensajes (POR DEFECTO), si la cola no existe devuelve null
     * 
     * @return Devuelve el último mensaje de una cola de mensajes (POR DEFECTO), no lo borra, y si la cola no existe devuelve null
     */
    public String peek() {
    	ConcurrentLinkedQueue<String> queue = multiMapa.get("COLA DEFAULT");
    	return queue.peek();
    }
    /**
     * Metodo para obtener el último mensaje de una cola de mensajes, si la cola no existe devuelve null
     * @param Como parametro tienes que pasar el nombre de la cola del que quieras obtener el mensaje
     * @return Devuelve el último mensaje de una cola de mensajes, no lo borra, y si la cola no existe devuelve null
     */
    public String peek(String clave) {
    	ConcurrentLinkedQueue<String> queue = multiMapa.get(clave);
    	if (queue == null) return null;
    	return queue.peek();
    }
    /**
     * Metodo para borrar una cola de mensajes entera
     * @param clave Como parametro hay que pasar la cola de mensajes entera. 
     * @return Devuelve true si se ha podido borrar, false si la cola no existe
     */
    public boolean deleted(String clave) {
    	if (clave == null)return false;
    	if (multiMapa.contains(clave)) {
    		multiMapa.remove(clave);
    		return true;
    	}
    	return false;
    }
    /**
     * Metodo para saber si existe una cola de mensajes
     * @param clave Como parametro pasamos un string que sea el identificador de la cola
     * @return Devuelve true si la cola existe, false si no existe.
     */
    public boolean contains(String clave){
        return multiMapa.containsKey(clave);
    }

    /**
     * Metodo para borrar una cola de mensajes
     * @param clave Como parametro pasamos un string que sea el identificador de la cola
     */
    public void delCola(String clave){
        multiMapa.remove(clave);
    }
    public String getQueueList() {
    	String idColas = "";
    	multiMapa.forEach((clave,valor) -> {
    		idColas.concat(clave+":");
    	});
    	return idColas;
    }
    public ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> obtenerTodasColas() {
    	return multiMapa;
    }
}
