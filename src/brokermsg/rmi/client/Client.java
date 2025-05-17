package brokermsg.rmi.client;

import java.util.Scanner;

import brokermsg.rmi.common.Authenticator;
import brokermsg.rmi.common.BathAuthException;
import brokermsg.rmi.common.BrokerMsg;
import brokermsg.rmi.common.BrokerAdmMsg;
import brokermsg.rmi.common.NotAuthException;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
public class Client  {
          public static void main (String[] args) throws MalformedURLException, RemoteException, NotBoundException, NoSuchAlgorithmException{
                    Scanner scanner = new Scanner(System.in);
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
