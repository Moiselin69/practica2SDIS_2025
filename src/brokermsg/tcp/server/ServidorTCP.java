package brokermsg.tcp.server;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import brokermsg.tcp.server.Sirviente;
import sdis.utils.MultiMap;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.concurrent.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorTCP implements Runnable {
    private int numHilos; // se utilizará para saber cuantos hilos maximos lanzamos en el pool
    private int puertoAbrir; // se utilizará para saber que puerto abrir
    private ConcurrentHashMap<Integer, Sirviente> mapaSirvientes = new ConcurrentHashMap<Integer, Sirviente>(); // sirve para depuración de hilos lanzados
    private ConcurrentHashMap<String, String> usuariosHashMap; // se utilizará para verificar usuarios con la base de datos
    private ConcurrentHashMap<String, String> usuariosAdminHashMap;
    private ConcurrentHashMap<String, ContadorAddRead> mapaMensajesAddRead;
    private MultiMap multiMap;
    private ObjectMapper mapper = new ObjectMapper(); // se utilizará para verificar usuarios con la base de datos
    private int idSirviente = 0; // se utilizrá para saber que sirviente ha fallado
    private ServerSocket socketServidor;
    private Socket socketParaCliente;
    BlackListManager listaIps = new BlackListManager(3);
    BlackListManager listaLogginsIncorrectos = new BlackListManager(2);
    private ObjectOutputStream oos;

    public ServidorTCP(int numHilos, int puertoAbrir,
    		ConcurrentHashMap<String, String> usuariosHashMap,
    		ConcurrentHashMap<String, String> usuariosAdminHashMap,
    		ConcurrentHashMap<String, ContadorAddRead> mapaMensajesAddRead,
    		MultiMap multiMapa) throws IOException {
        this.numHilos = numHilos;
        this.puertoAbrir = puertoAbrir;
        socketServidor = new ServerSocket(this.puertoAbrir);
        this.usuariosHashMap = usuariosHashMap;
        this.usuariosAdminHashMap = usuariosAdminHashMap;
        this.mapaMensajesAddRead = mapaMensajesAddRead;
        this.multiMap = multiMapa;
    }
    public void run(){ // en las siguientes lineas se lanzará el Pool de hilos
        ExecutorService executor = Executors.newFixedThreadPool(numHilos);
        while (true){
            try {
                System.out.println("----Server Waiting For Client----");
                socketParaCliente = socketServidor.accept(); // aceptamos el socket que nos llega siempre
                Sirviente sirvienteX = new Sirviente(socketParaCliente, idSirviente, usuariosHashMap, usuariosAdminHashMap,listaIps, listaLogginsIncorrectos, numHilos, mapaSirvientes, mapaMensajesAddRead, multiMap, (ThreadPoolExecutor) executor); // crear el nuevo hilo sirviente
                mapaSirvientes.put(idSirviente, sirvienteX); // guardamos el sirviente por mera depuración
                executor.submit(sirvienteX); // lanzamos el hilo
                idSirviente++;

            } catch (Exception e) {
                System.err.println("ERROR SERVIDOR PADRE: "+e.toString());
            }
        }
    }
}