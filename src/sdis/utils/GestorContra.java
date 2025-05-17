package sdis.utils;

import org.mindrot.jbcrypt.BCrypt;

public class GestorContra {
	public static String cifrarContras(String contra) {
        return BCrypt.hashpw(contra, BCrypt.gensalt());
    }
	public static boolean verificarContras(String contraIngresada, String hashAlmacenado) {
        return BCrypt.checkpw(contraIngresada, hashAlmacenado);
    }
}
