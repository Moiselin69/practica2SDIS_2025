package brokermsg.rmi.common;

public class BathAuthException extends Exception{
	private String mensaje = "";
	public BathAuthException() {}
	public BathAuthException(String mensaje) {
		if (mensaje == null) this.mensaje = "";
		this.mensaje = mensaje;
	}
	@Override
	public String toString() {
		return mensaje;
	}
}

