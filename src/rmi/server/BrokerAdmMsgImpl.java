package rmi.server;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import brokermsg.rmi.common.BrokerAdmMsg;
import brokermsg.rmi.common.BrokerMsg;
import sdis.utils.MultiMap;
public class BrokerAdmMsgImpl extends UnicastRemoteObject implements BrokerAdmMsg {
	private MultiMap multiMapa;
	private ConcurrentHashMap<String, String> usuariosHashMap;
	private ConcurrentHashMap<String, Boolean> usuariosAutorizadosMap = new ConcurrentHashMap<>();
	protected BrokerAdmMsgImpl(ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> multiMapa, ConcurrentHashMap<String, String> usuariosHashMap) throws RemoteException {
		super();
		this.multiMapa = new MultiMap(multiMapa);
		this.usuariosHashMap = usuariosHashMap;
		usuariosHashMap.forEach((user, password) -> {
			usuariosAutorizadosMap.put(password, false);
		});
	}
	@Override
	public String auth(String username, String password) throws RemoteException {
		if (username == null)return "NOTAUTH";
		if (password == null)return "NOTAUTH";
		if (!usuariosHashMap.contains(username)) return "NOTAUTH";
		if (!usuariosHashMap.get(username).equals(password)) return "NOTAUTH";
		usuariosAutorizadosMap.put(username, true);
		return "AUTH";
	}
	@Override
	public void add2Q(String queueName, String message) throws RemoteException {
		if (queueName == null) return;
		if (message == null) return;
		multiMapa.push(queueName, message);
	}
	@Override
	public void add2Q(String message) throws RemoteException {
		if (message == null)return;
		multiMapa.push("COLA DEFAULT", message);
		
	}
	@Override
	public String readQ(String queueName) throws RemoteException {
		if (queueName == null) return "EMPTY";
		String mensaje = multiMapa.pull(queueName);
		if (mensaje == null)
			return "EMPTY";
		else 
			return mensaje;
	}
	@Override
	public String readQ() throws RemoteException {
		String mensaje = multiMapa.pull("COLA DEFAULT");
		if (mensaje == null)
			return "EMPTY";
		else 
			return mensaje;
	}
	@Override
	public String deleteQ(String queueName) throws RemoteException {
		if (queueName == null) return null;
		boolean saBorrado = multiMapa.deleted(queueName);
		if (saBorrado)
			return "DELETED";
		else
			return "EMPTY";
	}
	@Override
	public String peekQ(String queueName) throws RemoteException {
		if (queueName == null) return null;
		String mensaje = multiMapa.peek(queueName);
		if (mensaje == null)
			return "EMPTY";
		else 
			return mensaje;
	}
	@Override
	public String peekQ() throws RemoteException {
		String mensaje = multiMapa.peek();
		if (mensaje == null)
			return "EMPTY";
		else 
			return mensaje;
	}
	@Override
	public String state(String queueName) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getQueueList() throws RemoteException {
		return multiMapa.getQueueList();
	}

}