package sdis.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class TokenGenerator {

	    /**
	     * Metodo para hashear una contraseña
	     * @param input Como parametro el string que queremos hashear
	     * @return Devuelve el string hasheado
	     * @throws NoSuchAlgorithmException
	     */
	    public static String hashear(String input) throws NoSuchAlgorithmException {
	        MessageDigest md = MessageDigest.getInstance("SHA-256");
	        byte[] hashBytes = md.digest(input.getBytes());
	        StringBuilder sb = new StringBuilder();
	        for (byte b : hashBytes) {
	            sb.append(String.format("%02x", b)); // Convierte a hex
	        }
	        return sb.toString();
	    }
	    /**
	     * Genera un token a partir del usuario y de su contraseña
	     * @param usuario Como parametro tienes que pasar un string que sea el nombre de usuario
	     * @param contrasena Como parametro tienes que pasar la contraseña del usuario para generar el token
	     * @return Devuelve el token unico de ese usuario
	     * @throws NoSuchAlgorithmException
	     */
	    public static String generarToken(String usuario, String contrasena) throws NoSuchAlgorithmException {
	        String hashUsuario = hashear(usuario);
	        String hashContrasena = hashear(contrasena);
	        String combinado = hashUsuario + hashContrasena;
	        return hashear(combinado);
	    }
}
