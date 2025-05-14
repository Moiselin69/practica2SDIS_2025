package brokermsg.rmi.client;
package brokermsg.rmi.common.*;
import java.util.Scanner;
import java.rmi.Naming
import java.rmi.RemoteException;
public class Client implements Authenticator, BrokerMsg, BrokerAddmsg {
          public static void main (String[] args){
                    Scanner in = new Scanner(System.in);
                    String nombre, contra, token;
                    boolean comprobacion = true;
                    while (comprobacion){
                      System.out.println("Escribe su nombre de usuario: ");
                      nombre = scanner.nextLine();
                      System.out.println("Escribe su contraseña: ");
                      contra = scanner.nextLine();
                      try{
                                Authenticator autenticador = (Authenticator) Naming.lookup("rmi://localhost/AuthenticatorImpl");
                                token = autenticador.conect(nombre,contra);
                                comprobacion = false;
                      }catch(BathAuthException bauth){
                                System.out.println("El nombre o la contraseña, por algun motivo no existe, vuelve a intentarlo");
                                comprobacion = true;
                      }catch(NotAuthException noAuth){
                                System.out.println("Las credenciales no son correctas");
                                comprobacion = true;
                      }
                    }
                    try{
                              
                    }catch (Exception e){
                              System.out.println(e);
                    }
          }
}
