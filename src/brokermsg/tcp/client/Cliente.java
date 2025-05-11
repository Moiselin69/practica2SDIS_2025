package brokermsg.tcp.client;

import brokermsg.tcp.common.MensajeProtocolo;
import brokermsg.tcp.common.Primitiva;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.Scanner;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class Cliente {
    private static final int PUERTO = 2000;
    private static ObjectInputStream ois;
    private static ObjectOutputStream oos;
    private static MensajeProtocolo mensajeProtocolo;

    public static void main(String[] args) throws KeyManagementException, Exception {
            String mensajeServidor, mensajeEnviar, mensajeEnviarNombre, mensajeEnviarContra;
            
            // Configura el trustStore del cliente
            System.setProperty("javax.net.ssl.trustStore", "src/sdis/config/cliente_truststore.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "000416");

            // Crear SSLContext para la conexión segura
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, getTrustManagers(), null);  // Inicializamos con el TrustManager predeterminado

            // Crear un socket SSL utilizando el SSLContext configurado
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket("localhost", PUERTO);
            
            // Configurar los protocolos habilitados para la comunicación segura
            socket.setEnabledProtocols(new String[] {"TLSv1.2", "TLSv1.3"});

            // Crear los streams de entrada y salida para la comunicación con el servidor
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            Scanner scanner = new Scanner(System.in);
            try{
                mensajeProtocolo = (MensajeProtocolo) ois.readObject();
                System.out.println(mensajeProtocolo.getMensaje());
                if (mensajeProtocolo.getMensaje().equals("Err Max Number of connections reached.") ||
                        mensajeProtocolo.getMensaje().equals("Err Max Number of login attempts reached.")){
                    oos.close();
                    ois.close();
                    socket.close();
                    return;
                }
                while (true){
                    System.out.println("¿Que desea realizar usted ahora?");
                    mensajeEnviar = scanner.nextLine();
                    if (mensajeEnviar.equals("LOGGIN")){
                        System.out.println("Escriba su nombre usuario: ");
                        mensajeEnviarNombre = scanner.nextLine();
                        System.out.println("Escriba su contraseña de usuario: ");
                        mensajeEnviarContra = scanner.nextLine();
                        mensajeProtocolo = new MensajeProtocolo(Primitiva.XAUTH, mensajeEnviarNombre, mensajeEnviarContra);
                        oos.writeObject(mensajeProtocolo);
                        mensajeProtocolo = (MensajeProtocolo) ois.readObject();
                        if (mensajeProtocolo.getPrimitiva() == Primitiva.XAUTH)
                            System.out.println(mensajeProtocolo.getMensaje());
                        else if (mensajeProtocolo.getPrimitiva() == Primitiva.ERROR) {
                            System.out.println(mensajeProtocolo.getMensaje());
                        } else{
                            mensajeProtocolo = new MensajeProtocolo(Primitiva.BADCODE);
                            oos.writeObject(mensajeProtocolo);
                        }
                    }else if (mensajeEnviar.equals("ADDMSG")){
                        System.out.println("Escriba el nombre de la cola a la que quieras enviar un mensaje: ");
                        mensajeEnviarNombre = scanner.nextLine();
                        System.out.println("Escriba el mensaje que quieras enviar: ");
                        mensajeEnviarContra = scanner.nextLine();
                        mensajeProtocolo = new MensajeProtocolo(Primitiva.ADDMSG, mensajeEnviarNombre, mensajeEnviarContra);
                        oos.writeObject(mensajeProtocolo);
                        mensajeProtocolo = (MensajeProtocolo) ois.readObject();
                        if(mensajeProtocolo.getPrimitiva() == Primitiva.ADDED){
                            System.out.println("Mensaje Enviado Correctamente");
                        }else if (mensajeProtocolo.getPrimitiva() == Primitiva.NOTAUTH) {
                            System.out.println(mensajeProtocolo.getMensaje());
                        }else{
                            mensajeProtocolo = new MensajeProtocolo(Primitiva.BADCODE);
                            oos.writeObject(mensajeProtocolo);
                        }
                    } else if (mensajeEnviar.equals("READMSG")) {
                        System.out.println("Escriba el nombre de la cola de la que quieras recibir un mensaje: ");
                        mensajeEnviarNombre = scanner.nextLine();
                        mensajeProtocolo = new MensajeProtocolo(Primitiva.READQ, mensajeEnviarNombre);
                        oos.writeObject(mensajeProtocolo);
                        mensajeProtocolo = (MensajeProtocolo) ois.readObject();
                        if (mensajeProtocolo.getPrimitiva() == Primitiva.MSG){
                            System.out.println("El mensaje recibido es: ");
                            System.out.println(mensajeProtocolo.getIdCola());
                        } else if (mensajeProtocolo.getPrimitiva() == Primitiva.NOTAUTH) {
                            System.out.println(mensajeProtocolo.getMensaje());
                        } else if (mensajeProtocolo.getPrimitiva() == Primitiva.EMPTY) {
                            System.out.println("La cola esta vacia o no existe");
                        }else{
                            mensajeProtocolo = new MensajeProtocolo(Primitiva.BADCODE);
                            oos.writeObject(mensajeProtocolo);
                        }
                    } else if (mensajeEnviar.equals("EXIT")) {
                        mensajeProtocolo = new MensajeProtocolo(Primitiva.EXIT);
                        oos.writeObject(mensajeProtocolo);
                        mensajeProtocolo = (MensajeProtocolo) ois.readObject();
                        System.out.println(mensajeProtocolo.getMensaje());
                        oos.close();
                        ois.close();
                        return;
                    } else if (mensajeEnviar.equals("STATE")) {
                        System.out.println("Cola que deseas ver: ");
                        mensajeEnviarNombre = scanner.nextLine();
                        mensajeProtocolo = new MensajeProtocolo(Primitiva.STATE, mensajeEnviarNombre);
                        oos.writeObject(mensajeProtocolo);
                        mensajeProtocolo = (MensajeProtocolo) ois.readObject();
                        if (mensajeProtocolo.getPrimitiva() == Primitiva.INFO)
                            System.out.println(mensajeProtocolo.getMensaje());
                        else if (mensajeProtocolo.getPrimitiva() == Primitiva.NOTAUTH) {
                            System.out.println(mensajeProtocolo.getMensaje());
                        } else{
                            mensajeProtocolo = new MensajeProtocolo(Primitiva.BADCODE);
                            oos.writeObject(mensajeProtocolo);
                        }
                    } else if (mensajeEnviar.equals("DELETE")) {
                        System.out.println("Cola que deseas borrar: ");
                        mensajeEnviarNombre = scanner.nextLine();
                        mensajeProtocolo = new MensajeProtocolo(Primitiva.DELETEQ, mensajeEnviarNombre);
                        oos.writeObject(mensajeProtocolo);
                        mensajeProtocolo = (MensajeProtocolo) ois.readObject();
                        if (mensajeProtocolo.getPrimitiva() == Primitiva.DELETED)
                            System.out.println("Cola borrada con exito");
                        else if (mensajeProtocolo.getPrimitiva() == Primitiva.EMPTY)
                            System.out.println("La cola no existe");
                        else if (mensajeProtocolo.getPrimitiva() == Primitiva.NOTAUTH)
                            System.out.println(mensajeProtocolo.getMensaje());
                         else{
                            mensajeProtocolo = new MensajeProtocolo(Primitiva.BADCODE);
                            oos.writeObject(mensajeProtocolo);
                        }
                    }else{
                        System.out.println("No se ha entendido el comando, vuelva a escribir el comando");
                    }
                }
            }catch (Exception e){
                System.out.println("Ha surgido un error raro en el cliente");
                System.out.println(e.getLocalizedMessage());
            }
    }
    private static TrustManager[] getTrustManagers() throws Exception {
        // Aquí podemos usar el TrustManager predeterminado
        TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        factory.init((KeyStore) null);  // Utiliza el trustStore predeterminado
        return factory.getTrustManagers();
    }
}
