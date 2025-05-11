package brokermsg.rmi.server;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import sdis.utils.GestorContra;
import brokermsg.rmi.common.BathAuthException;
import brokermsg.rmi.common.BrokerMsg;
import brokermsg.rmi.common.NotAuthException;
import sdis.utils.MultiMap;
public class BrokerMsgImpl extends UnicastRemoteObject implements BrokerMsg {
	private MultiMap multiMapa;
	private ConcurrentHashMap<String, String> usuariosHashMap;
	private ConcurrentHashMap<String, String> peticionesHashMap;
	private ConcurrentHashMap<String, String> tokensHashMap;
	public BrokerMsgImpl( ConcurrentHashMap<String, String> usuariosHashMap,
			ConcurrentHashMap<String, String> tokensHashMap,
			ConcurrentHashMap<String, String> peticionesHashMap,
			MultiMap multiMapa) throws RemoteException {
		super();
		
		this.usuariosHashMap = usuariosHashMap;
		this.tokensHashMap = tokensHashMap;
		this.peticionesHashMap = peticionesHashMap;
		this.multiMapa = multiMapa;
	}
	@Override
	public String auth(String token, String username, String password) throws RemoteException {
		if (username == null)return "NOTAUTH";
		if (password == null)return "NOTAUTH";
		if (token == null)return "NOTAUTH";
		if (!tokensHashMap.contains(token))return "NOTAUTH";
		if (!usuariosHashMap.contains(username)) return "NOTAUTH";
		if (!GestorContra.verificarContraseña(password, usuariosHashMap.get(username))) return "NOTAUTH";
		return "AUTH";
	}
	@Override
	public void add2Q(String token, String queueName, String message) throws RemoteException, NotAuthException {
		if (token == null)throw new NotAuthException("Acceso Denegado");
		if (!tokensHashMap.contains(token))throw new NotAuthException("Acceso Denegado");
		if (queueName == null) throw new NotAuthException("La cola no puede ser null");
		if (message == null || message.equals("")) throw new NotAuthException("El mensaje no puede ser null o vacío");
		if (!multiMapa.contains(queueName)) throw new NotAuthException("La cola de mensajes no existe");
		multiMapa.push(queueName, message);
	}
	@Override
	public void add2Q(String token, String message) throws RemoteException, NotAuthException {
		if (token == null)throw new NotAuthException("Acceso Denegado");
		if (!tokensHashMap.contains(token))throw new NotAuthException("Acceso Denegado");
		if (message == null)throw new NotAuthException("El mensaje es nulo");
		multiMapa.push("COLA DEFAULT", message);
		
	}
	@Override
	public String readQ(String token, String queueName) throws RemoteException, NotAuthException {
		if (token == null)throw new NotAuthException("Acceso Denegado");
		if (!tokensHashMap.contains(token))throw new NotAuthException("Acceso Denegado");
		if (queueName == null) throw new NotAuthException("Cola no accesible");
		String mensaje = multiMapa.pull(queueName);
		if (mensaje == null)throw new NotAuthException("Cola sin mensajes");
		else return mensaje;
	}
	@Override
	public String readQ(String token) throws RemoteException, NotAuthException {
		if (token == null)throw new NotAuthException("Acceso Denegado");
		if (!tokensHashMap.contains(token))throw new NotAuthException("Acceso Denegado");
		String mensaje = multiMapa.pull("COLA DEFAULT");
		if (mensaje == null)throw new NotAuthException("Cola sin mensajes");
		else return mensaje;
	}
	@Override
	public String enter(String token,String nombreUsuario, String contraUsuario) throws RemoteException, NotAuthException, BathAuthException {
		if (token == null)throw new NotAuthException("Acceso Denegado");
		if (!tokensHashMap.contains(token))throw new NotAuthException("Acceso Denegado");
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
		contraCifrada = GestorContra.cifrarContraseña(contraUsuario);
		peticionesHashMap.put(nombreUsuario, contraCifrada);
		return "VALID";
	}

}
