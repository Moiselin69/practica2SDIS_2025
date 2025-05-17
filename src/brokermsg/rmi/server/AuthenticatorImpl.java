package brokermsg.rmi.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;

import brokermsg.rmi.common.Authenticator;
import brokermsg.rmi.common.BathAuthException;
import brokermsg.rmi.common.NotAuthException;
import sdis.utils.GestorContra;
import sdis.utils.TokenGenerator;
import brokermsg.common.BlackListManager;

public class AuthenticatorImpl extends UnicastRemoteObject implements Authenticator{
	private ConcurrentHashMap<String, String> usuariosHashMap;
	private ConcurrentHashMap<String, String> tokensHashMap;
	private BlackListManager blackList;
	protected AuthenticatorImpl(ConcurrentHashMap<String, String> usuariosHashMap, ConcurrentHashMap<String, String> tokensHashMap, BlackListManager blackList)throws RemoteException {
		this.usuariosHashMap = usuariosHashMap;
		this.tokensHashMap = tokensHashMap;
		this.blackList = blackList;
	}
	private InetAddress obtenerIp() throws UnknownHostException, ServerNotActiveException{
		String clientHost = getClientHost(); // Heredado de RemoteServer
        InetAddress clientAddress = InetAddress.getByName(clientHost);
        return clientAddress;     	
	}
	
	@Override
	public String conect(String nombreUsuario, String password) throws RemoteException, BathAuthException, NotAuthException, NoSuchAlgorithmException {
		try {if (blackList.superarFallosPermitidos(obtenerIp()))return null;}catch(Exception e) {}
		if (nombreUsuario == null) {
			try {
				blackList.sumarUna(obtenerIp());
			}catch(Exception e) {}
			finally {
				throw new BathAuthException("Nombre de usuario null");
			}
		}
		if (password == null){
			try {
				blackList.sumarUna(obtenerIp());
			}catch(Exception e) {}
			finally {
				throw new BathAuthException("Contraseña de usuario null");
			}
		}
		if (!usuariosHashMap.contains(nombreUsuario)) {
			try {
				blackList.sumarUna(obtenerIp());
			}catch(Exception e) {}
			finally {
				throw new NotAuthException("Acceso denegado");
			}
		}
		if (GestorContra.verificarContraseña(password, usuariosHashMap.get(nombreUsuario))){
			try {
				blackList.sumarUna(obtenerIp());
			}catch(Exception e) {}
			finally {
				throw new NotAuthException("Acceso denegado");
			}
		}
		String token = TokenGenerator.generarToken(nombreUsuario, password);
		tokensHashMap.put(token, token);
		return token;
	}
	@Override
	public void disconnect(String token) throws RemoteException, NotAuthException {
		try {if (blackList.superarFallosPermitidos(obtenerIp()))return;}catch(Exception e) {}
		if (token == null)try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso denegado");
		}
		if (!tokensHashMap.contains(token))try {
			blackList.sumarUna(obtenerIp());
		}catch(Exception e) {}
		finally {
			throw new NotAuthException("Acceso denegado");
		} 
		tokensHashMap.remove(token);
	}

}
