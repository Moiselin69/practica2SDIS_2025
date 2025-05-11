package brokermsg.rmi.common;

public class NotAuthException extends Exception{
	private String mensaje = "";
	public NotAuthException() {}
	public NotAuthException(String mensaje) {
		if (mensaje == null) this.mensaje = "";
		this.mensaje = mensaje;
	}
	@Override
	public String toString() {
		return mensaje;
	}
}
