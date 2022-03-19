package de.craftsarmy.ytc;

import com.sun.net.httpserver.HttpServer;
import de.craftsarmy.ytc.contexts.DataContext;
import de.craftsarmy.ytc.contexts.LoginContext;
import de.craftsarmy.ytc.contexts.LogoutContext;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(25565), 0);

        httpServer.createContext("/account/login", new LoginContext());
        httpServer.createContext("/account/logout", new LogoutContext());
        httpServer.createContext("/data", new DataContext());

        httpServer.setExecutor(null);
        httpServer.start();
    }

}
