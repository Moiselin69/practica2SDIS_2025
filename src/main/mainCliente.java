package main;
import java.security.KeyManagementException;
import java.util.Scanner;
import brokermsg.tcp.client.Cliente;
import brokermsg.rmi.client.Client;
public class mainCliente {
	public static void main(String[] args) throws KeyManagementException, Exception {
		String opcion;
		boolean continuar = true;
		Scanner scanner = new Scanner(System.in);
		while (continuar) {
			System.out.println("Escriba 1 para conectarte via socket; Escriba 2 para conectarte via RMI");
			opcion = scanner.nextLine();
			if (opcion.equals("1"))
				Cliente.main(args);
			else if (opcion.equals("2"))
				Client.main(args);
			else System.out.println("Opcion no válida");
		}
	}
}
