package brokermsg.rmi.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;

public interface Authenticator extends Remote{
	/**
	 * Metodo para autenticar al usuario
	 * @param nombreUsuario Nombre de usuario que se desea acceder 
	 * @param password Contraseña del usuario del que se desea acceder
	 * @return Devuelve la contraseña el token especifico del usuario
	 * @throws RemoteException
	 * @throws BathAuthException 
	 * @throws NotAuthException
	 * @throws NoSuchAlgorithmException
	 */
	String conect(String nombreUsuario, String password)throws RemoteException, BathAuthException, NotAuthException, NoSuchAlgorithmException;
	
	/**
	 * Metodo para eliminar el token del usuario
	 * @param token El token del usuario del que se desea acceder 
	 * @throws RemoteException
	 * @throws BathAuthException 
	 * @throws NotAuthException
	 * @throws NoSuchAlgorithmException
	 */
	void disconnect(String token)throws RemoteException, BathAuthException, NotAuthException, NoSuchAlgorithmException;
}
