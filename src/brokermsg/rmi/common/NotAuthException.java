package brokermsg.rmi.common;

public class NotAuthException extends Exception{
	public NotAuthException() {}
	public NotAuthException(String mensaje) {
		super(mensaje);
	}
	
}
