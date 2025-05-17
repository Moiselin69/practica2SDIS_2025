package brokermsg.rmi.server;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import brokermsg.common.BlackListManager;
import brokermsg.rmi.common.BathAuthException;
import brokermsg.rmi.common.BrokerAdmMsg;
import brokermsg.rmi.common.BrokerMsg;
import brokermsg.rmi.common.NotAuthException;
import brokermsg.tcp.server.ContadorAddRead;
import sdis.utils.GestorContra;
import sdis.utils.MultiMap;
public class BrokerAdmMsgImpl extends UnicastRemoteObject implements BrokerAdmMsg {
	private ConcurrentHashMap<String, String> usuariosHashMap;
	private ConcurrentHashMap<String, String> tokensHashMap;
	private ConcurrentHashMap<String, String> peticionesHashMap;
	private ConcurrentHashMap<String, ContadorAddRead> mapaMensajesAddRead;
	private MultiMap multiMapa;
	private BlackListManager blackList;
	public BrokerAdmMsgImpl( ConcurrentHashMap<String, String> usuariosHashMap,
			ConcurrentHashMap<String, String> tokensHashMap,
			ConcurrentHashMap<String, String> peticionesHashMap,
			ConcurrentHashMap<String, ContadorAddRead> mapaMensajesAddRead,
			MultiMap multiMapa,
			BlackListManager blackList) throws RemoteException {
		super();
		this.usuariosHashMap = usuariosHashMap;
		this.tokensHashMap = tokensHashMap;
		this.peticionesHashMap = peticionesHashMap;
		this.mapaMensajesAddRead = mapaMensajesAddRead;
		this.multiMapa = multiMapa;
		this.blackList = blackList;
	}
	private InetAddress obtenerIp() throws UnknownHostException, ServerNotActiveException{
		String clientHost = getClientHost(); // Heredado de RemoteServer
        InetAddress clientAddress = InetAddress.getByName(clientHost);
        return clientAddress;     	
	}
	@Override
	public String auth(String token, String username, String password) throws RemoteException, BathAuthException {
		try {if (blackList.superarFallosPermitidos(obtenerIp()))throw new NotAuthException("Acceso Denegado");}catch(Exception e) {}
		if (username == null)try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			return "NOTAUTH";
		}
		if (password == null)try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			return "NOTAUTH";
		}
		if (token == null)try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			return "NOTAUTH";
		}
		if (!tokensHashMap.contains(token))try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			return "NOTAUTH";
		}
		if (!usuariosHashMap.contains(username)) try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			return "NOTAUTH";
		}
		if (!GestorContra.verificarContras(password, usuariosHashMap.get(username)))try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			return "NOTAUTH";
		}
		return "AUTH";
	}
	@Override
	public void add2Q(String token, String queueName, String message) throws RemoteException, NotAuthException {
		try {if (blackList.superarFallosPermitidos(obtenerIp()))throw new NotAuthException("Acceso Denegado");}catch(Exception e) {}
		if (token == null)try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso Denegado");
		}
		if (!tokensHashMap.contains(token))try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso Denegado");
		}
		if (queueName == null) throw new NotAuthException("El nombre de la cola es nulo");
		if (message == null) throw new NotAuthException("El mensaje es nulo");
		multiMapa.push(queueName, message);
		if (!mapaMensajesAddRead.contains(queueName))mapaMensajesAddRead.put(queueName, new ContadorAddRead());
		mapaMensajesAddRead.get(queueName).sumaUnaAdd();
	}
	@Override
	public void add2Q(String token, String message) throws RemoteException, NotAuthException {
		try {if (blackList.superarFallosPermitidos(obtenerIp()))throw new NotAuthException("Acceso Denegado");}catch(Exception e) {}
		if (token == null)try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso Denegado");
		}
		if (!tokensHashMap.contains(token))try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso Denegado");
		}
		if (message == null)throw new NotAuthException("El mensaje es nulo");
		multiMapa.push("COLA DEFAULT", message);
		mapaMensajesAddRead.get("COLA DEFAULT").sumaUnaAdd();
	}
	@Override
	public String readQ(String token, String queueName) throws RemoteException, NotAuthException {
		try {if (blackList.superarFallosPermitidos(obtenerIp()))throw new NotAuthException("Acceso Denegado");}catch(Exception e) {}
		if (token == null)try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso Denegado");
		}
		if (!tokensHashMap.contains(token))try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso Denegado");
		}
		if (queueName == null) throw new NotAuthException("Cola no accesible");
		String mensaje = multiMapa.pull(queueName);
		if (mensaje == null)throw new NotAuthException("Cola sin mensajes");
		else {
			mapaMensajesAddRead.get(queueName).sumaUnaRead();
			return mensaje;
		}
	}
	@Override
	public String readQ(String token) throws RemoteException, NotAuthException {
		if (token == null)throw new NotAuthException("Acceso Denegado");
		if (!tokensHashMap.contains(token))throw new NotAuthException("Acceso Denegado");
		String mensaje = multiMapa.pull("COLA DEFAULT");
		if (mensaje == null)throw new NotAuthException("Cola sin mensajes");
		else { 
			mapaMensajesAddRead.get("COLA DEFAULT").sumaUnaRead();
			return mensaje;
		}
	}
	@Override
	public String deleteQ(String token, String queueName) throws RemoteException, NotAuthException {
		try {if (blackList.superarFallosPermitidos(obtenerIp()))throw new NotAuthException("Acceso Denegado");}catch(Exception e) {}
		if (token == null)try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso Denegado");
		}
		if (!tokensHashMap.contains(token))try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso Denegado");
		}
		if (queueName == null)throw new NotAuthException("El nombre de la cola no puede ser nulo");
		boolean saBorrado = multiMapa.deleted(queueName);
		if (saBorrado) {
			mapaMensajesAddRead.remove(queueName);
			return "DELETED";
		}
		else throw new NotAuthException("La cola no existe");
	}
	@Override
	public String peekQ(String token, String queueName) throws RemoteException, NotAuthException {
		try {if (blackList.superarFallosPermitidos(obtenerIp()))throw new NotAuthException("Acceso Denegado");}catch(Exception e) {}
		if (token == null)try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso Denegado");
		}
		if (!tokensHashMap.contains(token))try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso Denegado");
		}
		if (queueName == null) return null;
		String mensaje = multiMapa.peek(queueName);
		if (mensaje == null)
			return "EMPTY";
		else 
			return mensaje;
	}
	@Override
	public String peekQ(String token) throws RemoteException, NotAuthException {
		try {if (blackList.superarFallosPermitidos(obtenerIp()))throw new NotAuthException("Acceso Denegado");}catch(Exception e) {}
		if (token == null)try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso Denegado");
		}
		if (!tokensHashMap.contains(token))try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso Denegado");
		}
		String mensaje = multiMapa.peek();
		if (mensaje == null)
			return "EMPTY";
		else 
			return mensaje;
	}
	@Override
	public String getQueueList(String token) throws RemoteException, NotAuthException {
		try {if (blackList.superarFallosPermitidos(obtenerIp()))throw new NotAuthException("Acceso Denegado");}catch(Exception e) {}
		if (token == null)try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso Denegado");
		}
		if (!tokensHashMap.contains(token))try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso Denegado");
		}
		return multiMapa.getQueueList();
	}
	@Override
	public String enter(String nombreUsuario, String contraUsuario) throws RemoteException, NotAuthException, BathAuthException {
		String contraCifrada;
		if (nombreUsuario == null) throw new BathAuthException("Nombre no válido");
		if (contraUsuario == null) throw new BathAuthException("Contraseña no válida");
		if (nombreUsuario.length() < 1) throw new BathAuthException("Nombre no válido");
		if (contraUsuario.length() < 8) throw new BathAuthException("Contraseña no válida. Tiene que tener 8 caracteres o más");;
		if (!contraUsuario.contains("0123456789")) throw new BathAuthException("Contraseña no válida. No contiene dígitos");;
		if (!contraUsuario.contains("@#!%*ñç&?¿¡+-ºª$")) throw new BathAuthException("Contraseña no válida. No contiene caracteres especiales: '@#!%*ñç&?¿¡+-ºª$'");;
		if (!contraUsuario.contains("abcdefghijklmnñopqrstuvwxyz")) throw new BathAuthException("Contraseña no válida. No contiene una minúscula");;
		if (!contraUsuario.contains("ABCDEFGHIJKLMNÑOPQRSTUVWXYZ")) throw new BathAuthException("Contraseña no válida. No contiene una mayúscula");;
		if (usuariosHashMap.contains(nombreUsuario)) throw new BathAuthException("Nombre no válido. Ya existe en el sistema");
		if (peticionesHashMap.contains(nombreUsuario)) throw new BathAuthException("Nombre no válido. Ya existe en el sistema");
		contraCifrada = GestorContra.cifrarContras(contraUsuario);
		peticionesHashMap.put(nombreUsuario, contraCifrada);
		return "VALID";
	}
	
	@Override
	public void aceptarNuevosUsuarios(String token) throws RemoteException, NotAuthException {
		try {if (blackList.superarFallosPermitidos(obtenerIp()))throw new NotAuthException("Acceso Denegado");}catch(Exception e) {}
		if (token == null)try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso Denegado");
		}
		if (!tokensHashMap.contains(token))try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso Denegado");
		}
		this.peticionesHashMap.forEach((clave, valor)->{
			usuariosHashMap.put(clave, valor);
		});
	}
	@Override
	public void aceptarNuevosUsuarios(String token, String usuario) throws RemoteException, NotAuthException {
		try {if (blackList.superarFallosPermitidos(obtenerIp()))throw new NotAuthException("Acceso Denegado");}catch(Exception e) {}
		if (token == null)try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso Denegado");
		}
		if (!tokensHashMap.contains(token))try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso Denegado");
		}
		if (usuario == null)return;
		if (!peticionesHashMap.contains(usuario)) return;
		usuariosHashMap.put(usuario,peticionesHashMap.get(usuario));
	}
	@Override
	public String verPeticiones(String token) throws RemoteException, NotAuthException {
		try {if (blackList.superarFallosPermitidos(obtenerIp()))throw new NotAuthException("Acceso Denegado");}catch(Exception e) {}
		if (token == null)try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso Denegado");
		}
		if (!tokensHashMap.contains(token))try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso Denegado");
		}
		String mensajeDevolver = "";
		peticionesHashMap.forEach((clave,valor)->{
			mensajeDevolver.concat(":"+clave);
		});
		return mensajeDevolver;
	}
	@Override
	public String state(String token, String queueName) throws RemoteException, NotAuthException {
		try {if (blackList.superarFallosPermitidos(obtenerIp()))throw new NotAuthException("Acceso Denegado");}catch(Exception e) {}
		if (token == null)try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso Denegado");
		}
		if (!tokensHashMap.contains(token))try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso Denegado");
		}
		if  (!multiMapa.contains(queueName))throw new NotAuthException("Acceso Denegado");
		return queueName+":"+mapaMensajesAddRead.get(queueName).getContadorAdd()+":"+mapaMensajesAddRead.get(queueName).getContadorRead();
	}

}
