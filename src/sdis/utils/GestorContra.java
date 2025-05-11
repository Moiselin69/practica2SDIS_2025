package sdis.utils;

import org.mindrot.jbcrypt.BCrypt;

public class GestorContra {
	public static String cifrarContraseña(String contraseña) {
        return BCrypt.hashpw(contraseña, BCrypt.gensalt());
    }
	public static boolean verificarContraseña(String contraseñaIngresada, String hashAlmacenado) {
        return BCrypt.checkpw(contraseñaIngresada, hashAlmacenado);
    }
}
