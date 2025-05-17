package brokermsg.rmi.server;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import sdis.utils.GestorContra;
import brokermsg.common.BlackListManager;
import brokermsg.rmi.common.BathAuthException;
import brokermsg.rmi.common.BrokerMsg;
import brokermsg.rmi.common.NotAuthException;
import brokermsg.tcp.server.ContadorAddRead;
import sdis.utils.MultiMap;
public class BrokerMsgImpl extends UnicastRemoteObject implements BrokerMsg {
	private ConcurrentHashMap<String, String> usuariosHashMap;
	private ConcurrentHashMap<String, String> tokensHashMap;
	private ConcurrentHashMap<String, String> peticionesHashMap;
	private ConcurrentHashMap<String, ContadorAddRead> mapaMensajesAddRead;
	private MultiMap multiMapa;
	private BlackListManager blackList;
	public BrokerMsgImpl( ConcurrentHashMap<String, String> usuariosHashMap,
			ConcurrentHashMap<String, String> tokensHashMap,
			ConcurrentHashMap<String, String> peticionesHashMap,
			ConcurrentHashMap<String, ContadorAddRead> mapaMensajesAddRead,
			MultiMap multiMapa,
			BlackListManager blackList) throws RemoteException {
		super();
		
		this.usuariosHashMap = usuariosHashMap;
		this.tokensHashMap = tokensHashMap;
		this.peticionesHashMap = peticionesHashMap;
		this.multiMapa = multiMapa;
		this.blackList = blackList;
	}
	private InetAddress obtenerIp() throws UnknownHostException, ServerNotActiveException{
		String clientHost = getClientHost(); // Heredado de RemoteServer
        InetAddress clientAddress = InetAddress.getByName(clientHost);
        return clientAddress;     	
	}
	@Override
	public String auth(String token, String username, String password) throws RemoteException {
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
		if (queueName == null) throw new NotAuthException("Cola Nula");
		if (message == null || message.equals("")) throw new NotAuthException("Mensaje vacio o nulo");
		if (!multiMapa.contains(queueName)) throw new NotAuthException("Cola no accesible");
		multiMapa.push(queueName, message);
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
		if (message == null) throw new NotAuthException("Mensaje vacio o nulo");
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
		if (mensaje == null) throw new NotAuthException("No existe mensajes en esta cola");
		else {
			mapaMensajesAddRead.get(queueName).sumaUnaRead();
			return mensaje;
		}
	}
	@Override
	public String readQ(String token) throws RemoteException, NotAuthException {
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
		String mensaje = multiMapa.pull("COLA DEFAULT");
		if (mensaje == null)throw new NotAuthException("Cola sin mensajes");
		else {
			mapaMensajesAddRead.get("COLA DEFAULT").sumaUnaRead();
			return mensaje;
		}
	}
	@Override
	public String enter(String nombreUsuario, String contraUsuario) throws RemoteException, NotAuthException, BathAuthException {
		try {if (blackList.superarFallosPermitidos(obtenerIp()))throw new NotAuthException("Acceso Denegado");}catch(Exception e) {}
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

}
