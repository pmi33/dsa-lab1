package ru.ssau.dsa.sockets.proxy;

import ru.ssau.dsa.sockets.common.Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ProxyRunner {

    public static void main(String[] args) {
        try{
            ServerSocket ss = new ServerSocket(54321);
            while (true){
                Socket client = ss.accept();
                new Proxy(new Connection(client), new Connection("localhost", 12345));
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

}
