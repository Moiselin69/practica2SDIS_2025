package main;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import brokermsg.tcp.server.ContadorAddRead;
import brokermsg.tcp.server.ServidorTCP;
import sdis.utils.GestorContra;

public class mainServidor {
	 public static void main(String[] args){
	        ConcurrentHashMap<String, String> usuariosHashMap = new ConcurrentHashMap<>();
	        ObjectMapper mapper = new ObjectMapper();
	        usuariosHashMap.put("Manoli", GestorContra.cifrarContraseña("1234"));
	        usuariosHashMap.put("Paqui", GestorContra.cifrarContraseña("1234"));
	        usuariosHashMap.put("Luisma", GestorContra.cifrarContraseña("1234"));
	        usuariosHashMap.put("Barajas", GestorContra.cifrarContraseña("1234"));
	        usuariosHashMap.put("cllamas", GestorContra.cifrarContraseña("qwerty"));
	        usuariosHashMap.put("hector", GestorContra.cifrarContraseña("lkjlkj"));
	        usuariosHashMap.put("sdis", GestorContra.cifrarContraseña("987123"));
	        usuariosHashMap.put("admin", GestorContra.cifrarContraseña("$%&/()="));
	        ConcurrentHashMap<String, ConcurrentLinkedDeque<String>> colasMensajes = new ConcurrentHashMap<>();
	        colasMensajes.put("COLA DEFAULT", new ConcurrentLinkedDeque<String>());
	        ConcurrentHashMap<String, ContadorAddRead> mapaMensajesAddRead = new ConcurrentHashMap<>();
	        mapaMensajesAddRead.put("COLA DEFAULT", new ContadorAddRead());

	        try{
	            mapper.writeValue(new File("src/sdis/config/usuariosContras.json"), usuariosHashMap);
	            mapper.writeValue(new File("src/sdis/config/colasMensajeria.json"), colasMensajes);
	            mapper.writeValue(new File("src/sdis/config/depuracionColasMensajeria.json"), mapaMensajesAddRead);
	            ServidorTCP servidorPadre = new ServidorTCP(5, 2000);
	            servidorPadre.run();
	        }
	        catch (Exception e){
	            System.out.println(("Ha surgido un error en el main"+e.toString()));
	        }
	    }
}
