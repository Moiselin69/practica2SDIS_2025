package brokermsg.rmi.client;

import java.util.Scanner;

import javax.rmi.ssl.SslRMIClientSocketFactory;

import brokermsg.rmi.common.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    private static final String RMI_URL = "rmi://localhost/";
    private static Scanner scanner = new Scanner(System.in);
    private static String token = null;
    private static BrokerMsg broker;
    private static BrokerAdmMsg brokerAdm;
    private static Authenticator autenticador;
    
    public static void main(String[] args) {
    	String tipoUsuario;
    	boolean continuar = true;
        try {
            // Configurar seguridad RMI con SSL
            System.setProperty("javax.net.ssl.trustStore", "src/sdis/config/cliente_truststore.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "000416");
            System.setProperty("java.rmi.server.RMIClientSocketFactory", "javax.rmi.ssl.SslRMIClientSocketFactory");
            
            // Obtener referencias a los objetos remotos
            Registry registry = LocateRegistry.getRegistry("localhost", 1099, new SslRMIClientSocketFactory());

            autenticador = (Authenticator) registry.lookup("AuthenticatorImpl");
            broker = (BrokerMsg) registry.lookup("BrokerMsgImpl");
            brokerAdm = (BrokerAdmMsg) registry.lookup("BrokerAdmMsgImpl");
            System.out.println("Has entrado a la conexión vía RMI :)");
            System.out.println("Escriba 1 si quiere entrar como usuario normal; Escriba 2 si quiere entrar como usuario administrador; Escriba 0 si desea salir");
            while (continuar) {
            	tipoUsuario = scanner.nextLine();
            	if (tipoUsuario.equals("0")) continuar = false;
            	else if (tipoUsuario.equals("1")) {
            		if (autenticarUsuario()) {
            			ejecutarMenu();
            			continuar = false;
            		}
            		else System.out.println("vuelva a elegir opción: ");
            	}
            	else if (tipoUsuario.equals("2")) {
            		if (autenticarUsuarioAdm()) {
            			ejecutarMenuAdm();
            			continuar = false;
            		}else System.out.println("vuelva a elegir opción: ");
            	}
            	else System.out.println("No has seleccionado si uno, dos o 0");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
    private static boolean autenticarUsuarioAdm() {
            try {
                System.out.print("Usuario: ");
                String nombre = scanner.nextLine().trim();
                System.out.print("Contraseña: ");
                String contra = scanner.nextLine().trim();
                
                token = autenticador.conectAdm(nombre, contra);
                System.out.println("Autenticación exitosa");
                return true;
            } catch (BathAuthException | NotAuthException e) {
                System.out.println("Error de autenticación: " + e.getMessage());
                return false;
            } catch (Exception e) {
                System.out.println("Error de conexión: " + e.getMessage());
                return false;
            }
    }
    private static boolean autenticarUsuario() {
            try {
                System.out.print("Usuario: ");
                String nombre = scanner.nextLine().trim();
                System.out.print("Contraseña: ");
                String contra = scanner.nextLine().trim();
                
                token = autenticador.conect(nombre, contra);
                System.out.println("Autenticación exitosa");
                return true;
            } catch (BathAuthException | NotAuthException e) {
                System.out.println("Error de autenticación: " + e.getMessage());
                return false;
            } catch (Exception e) {
                System.out.println("Error de conexión: " + e.getMessage());
                return false;
            } 
    }
    private static void ejecutarMenuAdm() {
        boolean salir = false;
        
        while (!salir) {
            try {
                mostrarMenuAdm();
                int opcion = Integer.parseInt(scanner.nextLine().trim());
                
                switch (opcion) {
                    case 0: // Salir
                        autenticador.disconnect(token);
                        System.out.println("Sesión finalizada");
                        salir = true;
                        break;
                    case 1: // Añadir mensaje a cola específica
                        System.out.print("Cola: ");
                        String cola = scanner.nextLine();
                        System.out.print("Mensaje: ");
                        String msg = scanner.nextLine();
                        brokerAdm.add2Q(token, cola, msg);
                        System.out.println("Mensaje añadido a " + cola);
                        break;
                    case 2: // Añadir mensaje a cola por defecto
                        System.out.print("Mensaje: ");
                        brokerAdm.add2Q(token, scanner.nextLine());
                        System.out.println("Mensaje añadido a cola por defecto");
                        break;
                    case 3: // Leer mensaje de cola
                        System.out.print("Cola: ");
                        String mensaje = brokerAdm.readQ(token, scanner.nextLine());
                        System.out.println(mensaje != null ? "Mensaje: " + mensaje : "Cola vacía");
                        break;
                    case 4: // Leer mensaje de cola por defecto
                        mensaje = brokerAdm.readQ(token);
                        System.out.println(mensaje != null ? "Mensaje: " + mensaje : "Cola vacía");
                        break;
                    case 5: // Eliminar cola
                        System.out.print("Cola a eliminar: ");
                        String result = brokerAdm.deleteQ(token, scanner.nextLine());
                        System.out.println(result.equals("DELETED") ? "Cola eliminada" : "Cola no existente");
                        break;
                    case 6: // Ver mensaje sin eliminar
                        System.out.print("Cola: ");
                        mensaje = brokerAdm.peekQ(token, scanner.nextLine());
                        System.out.println(mensaje != null ? "Primer mensaje: " + mensaje : "Cola vacía");
                        break;
                    case 7: // Ver mensaje de cola por defecto sin eliminar
                        mensaje = brokerAdm.peekQ(token);
                        System.out.println(mensaje != null ? "Primer mensaje: " + mensaje : "Cola vacía");
                        break;
                    case 8: // Ver estado de cola
                        System.out.print("Cola: ");
                        System.out.println(brokerAdm.state(token, scanner.nextLine()));
                        break;
                    case 9: // Ver lista de colas
                        System.out.println(brokerAdm.getQueueList(token));
                        break;
                    case 10: // Ver peticiones de acceso
                        mostrarPeticiones(brokerAdm.verPeticiones(token));
                        break;
                    case 11: // Aceptar todas las peticiones
                        brokerAdm.aceptarNuevosUsuarios(token);
                        System.out.println("Todas las peticiones aceptadas");
                        break;
                    case 12: // Aceptar una petición específica
                        System.out.print("Usuario a aceptar: ");
                        brokerAdm.aceptarNuevosUsuarios(token, scanner.nextLine());
                        System.out.println("Usuario aceptado");
                        break;
                    case 13: // Solicitar acceso
                        System.out.print("Nuevo usuario: ");
                        String usuario = scanner.nextLine();
                        System.out.print("Contraseña: ");
                        System.out.println("Resultado: " + broker.enter(usuario, scanner.nextLine()));
                        break;
                    default:
                        System.out.println("Opción no válida");
                }
            } catch (NotAuthException e) {
                System.out.println(e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage()); 
            }
        }
    }
    
    private static void ejecutarMenu() {
        boolean salir = false;
        
        while (!salir) {
            try {
                mostrarMenu();
                int opcion = Integer.parseInt(scanner.nextLine().trim());
                
                switch (opcion) {
                    case 0: // Salir
                        autenticador.disconnect(token);
                        System.out.println("Sesión finalizada");
                        salir = true;
                        break;
                    case 1: // Añadir mensaje a cola específica
                        System.out.print("Cola: ");
                        String cola = scanner.nextLine();
                        System.out.print("Mensaje: ");
                        String msg = scanner.nextLine();
                        broker.add2Q(token, cola, msg);
                        System.out.println("Mensaje añadido a " + cola);
                        break;
                    case 2: // Añadir mensaje a cola por defecto
                        System.out.print("Mensaje: ");
                        broker.add2Q(token, scanner.nextLine());
                        System.out.println("Mensaje añadido a cola por defecto");
                        break;
                    case 3: // Leer mensaje de cola
                        System.out.print("Cola: ");
                        String mensaje = broker.readQ(token, scanner.nextLine());
                        System.out.println(mensaje != null ? "Mensaje: " + mensaje : "Cola vacía");
                        break;
                    case 4: // Leer mensaje de cola por defecto
                        mensaje = broker.readQ(token);
                        System.out.println(mensaje != null ? "Mensaje: " + mensaje : "Cola vacía");
                        break;
                    case 5: // Solicitar acceso
                        System.out.print("Nuevo usuario: ");
                        String usuario = scanner.nextLine();
                        System.out.print("Contraseña: ");
                        System.out.println("Resultado: " + broker.enter(usuario, scanner.nextLine()));
                        break;
                    default:
                        System.out.println("Opción no válida");
                }
            } catch (NotAuthException e) {
                System.out.println(e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage()); 
            }
        }
    }
    
    private static void mostrarMenu() {
        System.out.println("\n==== BROKER DE MENSAJES RMI ====");
        System.out.println("0. Salir");
        System.out.println("1. Añadir mensaje a cola específica");
        System.out.println("2. Añadir mensaje a cola por defecto");
        System.out.println("3. Leer mensaje de cola");
        System.out.println("4. Leer mensaje de cola por defecto");
        System.out.println("5. Solicitar acceso al sistema");
        System.out.print("Opción: ");
    }
    private static void mostrarMenuAdm() {
        System.out.println("\n==== BROKER DE MENSAJES RMI ====");
        System.out.println("0. Salir");
        System.out.println("1. Añadir mensaje a cola específica");
        System.out.println("2. Añadir mensaje a cola por defecto");
        System.out.println("3. Leer mensaje de cola");
        System.out.println("4. Leer mensaje de cola por defecto");
        System.out.println("5. Eliminar cola");
        System.out.println("6. Ver mensaje sin eliminar");
        System.out.println("7. Ver mensaje de cola por defecto");
        System.out.println("8. Ver estado de cola");
        System.out.println("9. Ver lista de colas");
        System.out.println("10. Ver peticiones de acceso");
        System.out.println("11. Aceptar todas las peticiones");
        System.out.println("12. Aceptar petición específica");
        System.out.println("13. Solicitar acceso al sistema");
        System.out.print("Opción: ");
    }
    private static void mostrarPeticiones(String peticiones) {
        if (peticiones == null || peticiones.isEmpty()) {
            System.out.println("No hay peticiones pendientes");
            return;
        }
        
        System.out.println("Peticiones pendientes:");
        for (String usuario : peticiones.split(":")) {
            if (!usuario.trim().isEmpty()) {
                System.out.println("- " + usuario);
            }
        }
    }
}
