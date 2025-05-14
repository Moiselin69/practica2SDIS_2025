package brokermsg.rmi.server;

import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;

import brokermsg.rmi.common.Authenticator;
import brokermsg.rmi.common.BathAuthException;
import brokermsg.rmi.common.NotAuthException;
import sdis.utils.GestorContra;
import sdis.utils.TokenGenerator;

public class AuthenticatorImpl extends UnicastRemoteObject implements Authenticator{
	private ConcurrentHashMap<String, String> usuariosHashMap;
	private ConcurrentHashMap<String, String> tokensHashMap;
	public AuthenticatorImpl(ConcurrentHashMap<String, String> usuariosHashMap,
			ConcurrentHashMap<String, String> tokensHashMap) {
		this.usuariosHashMap = usuariosHashMap;
		this.tokensHashMap = tokensHashMap;
	}
	private String obtenerIp(){
		String clientHost = getClientHost(); // Heredado de RemoteServer
            	InetAddress clientAddress = InetAddress.getByName(clientHost);
            	String clientIP = clientAddress.getHostAddress();
		return clientIP;
	}
	@Override
	public String conect(String nombreUsuario, String password) throws RemoteException, BathAuthException, NotAuthException, NoSuchAlgorithmException {
		if (nombreUsuario == null)throw new BathAuthException("Nombre de usuario null");
		if (password == null)throw new BathAuthException("Contraseña de usuario null");
		if (!usuariosHashMap.contains(nombreUsuario)) throw new NotAuthException("Acceso denegado");
		if (GestorContra.verificarContraseña(password, usuariosHashMap.get(nombreUsuario)))throw new NotAuthException("Acceso denegado");
		String token = TokenGenerator.generarToken(nombreUsuario, password);
		tokensHashMap.put(token, token);
		return token;
	}
	@Override
	public void disconnect(String token) throws RemoteException, NotAuthException {
		if (token == null)throw new NotAuthException("Acceso denegado");
		if (!tokensHashMap.contains(token)) throw new NotAuthException("Acceso denegado");
		tokensHashMap.remove(token);
	}

}
