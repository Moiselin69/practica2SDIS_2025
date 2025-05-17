package brokermsg.rmi.client;

import java.util.Scanner;

import brokermsg.rmi.common.Authenticator;
import brokermsg.rmi.common.BathAuthException;
import brokermsg.rmi.common.NotAuthException;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.security.NoSuchAlgorithmException;

import javax.rmi.ssl.SslRMIClientSocketFactory;

public class Client  {
    public static void main(String[] args) throws RemoteException, NotBoundException, NoSuchAlgorithmException {
        // Configuración SSL
        System.setProperty("javax.net.ssl.trustStore", "src/sdis/config/cliente_truststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "000416");

        Scanner scanner = new Scanner(System.in);
        String nombre, contra, token;
        boolean comprobacion = true;
        boolean accedido = true;
        while (comprobacion) {
            System.out.println("Escribe su nombre de usuario: ");
            nombre = scanner.nextLine();
            System.out.println("Escribe su contraseña: ");
            contra = scanner.nextLine();

            try {
                // Obteniendo el registro con SSL
                Registry registry = LocateRegistry.getRegistry("localhost", 1099, new SslRMIClientSocketFactory());
                Authenticator autenticador = (Authenticator) registry.lookup("AuthenticatorImpl");
                token = autenticador.conect(nombre, contra);
                comprobacion = false;
                System.out.println("Autenticación realizada");
            } catch (BathAuthException bauth) {
                System.out.println(bauth.getMessage());
                comprobacion = true;
            } catch (NotAuthException noAuth) {
                System.out.println(noAuth.getMessage());
                comprobacion = true;
            }
        }
        while (accedido) {
        	
        }
    }
}
