package brokermsg.rmi.server;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ConcurrentHashMap;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;

import brokermsg.common.BlackListManager;
import brokermsg.tcp.server.ContadorAddRead;
import sdis.utils.MultiMap;
public class LauncherRMI {
	private int puerto;
	private ConcurrentHashMap<String, String> usuariosHashMap;
	private ConcurrentHashMap<String, String> usuariosAdminHashMap;
	private ConcurrentHashMap<String, String> tokensHashMap;
	private ConcurrentHashMap<String, String> tokensAdminHashMap;
	private ConcurrentHashMap<String, String> peticionesHashMap;
	private ConcurrentHashMap<String, ContadorAddRead> mapaMensajesAddRead;
	private MultiMap multiMapa;
	private BlackListManager blackList;
	public LauncherRMI(int puerto, 
			ConcurrentHashMap<String, String> usuariosHashMap,
			ConcurrentHashMap<String, String> usuariosAdminHashMap,
			ConcurrentHashMap<String, String> tokensHashMap,
			ConcurrentHashMap<String, String> tokensAdminHashMap,
			ConcurrentHashMap<String, String> peticionesHashMap,
    		ConcurrentHashMap<String, ContadorAddRead> mapaMensajesAddRead,
    		MultiMap multiMapa,
    		BlackListManager blackList) {
		this.puerto = puerto;
		this.usuariosHashMap = usuariosHashMap;
		this.usuariosAdminHashMap = usuariosAdminHashMap;
		this.tokensHashMap = tokensHashMap;
		this.tokensAdminHashMap = tokensAdminHashMap;
		this.peticionesHashMap = peticionesHashMap;
		this.mapaMensajesAddRead = mapaMensajesAddRead;
		this.multiMapa = multiMapa;
		this.blackList = blackList;
	}
	public void run() {
		System.setProperty("javax.net.ssl.keyStore", "src/sdis/config/servidor_keystore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "000416");
		 try {
	            // Crear el registro RMI en el puerto 1099 (localhost)
			 Registry registry = LocateRegistry.createRegistry(
					    puerto,
					    new SslRMIClientSocketFactory(),
					    new SslRMIServerSocketFactory()
					);

	            // Crear las implementaciones de los objetos
	            AuthenticatorImpl auth = new AuthenticatorImpl(usuariosAdminHashMap, tokensAdminHashMap, usuariosHashMap, tokensHashMap, blackList);
	            BrokerMsgImpl data_user = new BrokerMsgImpl(usuariosHashMap, tokensHashMap, peticionesHashMap, mapaMensajesAddRead,multiMapa, blackList);
	            BrokerAdmMsgImpl data_admin = new BrokerAdmMsgImpl(usuariosAdminHashMap, tokensAdminHashMap, peticionesHashMap, mapaMensajesAddRead, multiMapa, blackList);
	            // Registrar los objetos en el registro con diferentes nombres
	            registry.rebind("AuthenticatorImpl", auth);
	            registry.rebind("BrokerMsgImpl", data_user);
	            registry.rebind("BrokerAdmMsgImpl", data_admin);

	            System.out.println("Servidor RMI levantado.");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	}
}
