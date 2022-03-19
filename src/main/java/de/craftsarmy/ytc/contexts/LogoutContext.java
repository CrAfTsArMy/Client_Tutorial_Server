package de.craftsarmy.ytc.contexts;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.craftsarmy.ytc.User;
import de.craftsarmy.ytc.UserManager;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class LogoutContext implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
        parse(exchange, bufferedReader.readLine());
    }

    private void parse(HttpExchange exchange, String request) throws IOException {
        try {
            StringWriter response = new StringWriter();
            JsonWriter writer = new JsonWriter(response);

            JsonObject parsed = JsonParser.parseString(request).getAsJsonObject();

            System.out.println("[Logout -> From: " + "*".repeat(exchange.getRemoteAddress().getAddress().getHostAddress().length()) + "]: " + request);

            if (!RequestType.parse(exchange.getRequestMethod()).equals(RequestType.DELETE))
                writer.beginObject()
                        .name("error").value("use.request.delete")
                        .endObject()
                        .flush();
            else {
                String username = parsed.get("name").getAsString();
                UserManager.logout(username, exchange.getRemoteAddress().getAddress());
                writer.beginObject()
                        .name("success").value("logout.success")
                        .endObject()
                        .flush();
            }

            exchange.sendResponseHeaders(200, response.toString().length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
