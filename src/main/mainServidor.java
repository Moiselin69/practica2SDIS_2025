package main;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import brokermsg.common.BlackListManager;
import brokermsg.rmi.server.LauncherRMI;
import brokermsg.tcp.server.ContadorAddRead;
import brokermsg.tcp.server.ServidorTCP;
import sdis.utils.GestorContra;
import sdis.utils.MultiMap;

public class mainServidor {
	 public static void main(String[] args){
		 	ObjectMapper mapper = new ObjectMapper();
	        ConcurrentHashMap<String, String> usuariosHashMap = new ConcurrentHashMap<>();
	        usuariosHashMap.put("Manoli", GestorContra.cifrarContras("1234"));
	        usuariosHashMap.put("Paqui", GestorContra.cifrarContras("1234"));
	        usuariosHashMap.put("Luisma", GestorContra.cifrarContras("1234"));
	        usuariosHashMap.put("Barajas", GestorContra.cifrarContras("1234"));
	        ConcurrentHashMap<String, String> usuariosAdminHashMap = new ConcurrentHashMap<>();
	        usuariosAdminHashMap.put("cllamas", GestorContra.cifrarContras("qwerty"));
	        usuariosAdminHashMap.put("hector", GestorContra.cifrarContras("lkjlkj"));
	        usuariosAdminHashMap.put("sdis", GestorContra.cifrarContras("987123"));
	        usuariosAdminHashMap.put("admin", GestorContra.cifrarContras("$%&/()="));
	        ConcurrentHashMap<String, String> tokensHashMap = new ConcurrentHashMap<>();
	        ConcurrentHashMap<String, String> tokensAdminHashMap = new ConcurrentHashMap<>();
	        ConcurrentHashMap<String, String> peticionesHashMap = new ConcurrentHashMap<>();
	        ConcurrentHashMap<String, ContadorAddRead> mapaMensajesAddRead = new ConcurrentHashMap<>();
	        mapaMensajesAddRead.put("COLA DEFAULT", new ContadorAddRead());
	        
	        MultiMap multiMapa = new MultiMap();
	        BlackListManager blackList = new BlackListManager(3);
	        try{
	            mapper.writeValue(new File("src/sdis/config/usuariosContras.json"), usuariosHashMap);
	            mapper.writeValue(new File("src/sdis/config/colasMensajeria.json"), multiMapa.obtenerTodasColas());
	            mapper.writeValue(new File("src/sdis/config/depuracionColasMensajeria.json"), mapaMensajesAddRead);
	            ServidorTCP servidorPadre = new ServidorTCP(5, 2000, usuariosHashMap,usuariosAdminHashMap,mapaMensajesAddRead, multiMapa, blackList);
	            LauncherRMI servidorRMI = new LauncherRMI(1099, usuariosHashMap, usuariosAdminHashMap, tokensHashMap, tokensAdminHashMap,peticionesHashMap, mapaMensajesAddRead, multiMapa, blackList);
	            servidorRMI.run();
	            servidorPadre.run();
	            
	        }
	        catch (Exception e){
	            System.out.println(("Ha surgido un error en el main"+e.toString()));
	        }
	    }
}
