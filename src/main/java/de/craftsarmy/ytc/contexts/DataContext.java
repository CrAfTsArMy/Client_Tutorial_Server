package de.craftsarmy.ytc.contexts;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.craftsarmy.ytc.User;
import de.craftsarmy.ytc.UserManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataContext implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
        parse(exchange, bufferedReader.readLine());
    }

    private void parse(HttpExchange exchange, String request) throws IOException {
        try {
            StringWriter response = new StringWriter();
            JsonWriter writer = new JsonWriter(response);

            System.out.println("[Request -> From: " + "*".repeat(exchange.getRemoteAddress().getAddress().getHostAddress().length()) + "]: " + request);

            JsonObject parsed = JsonParser.parseString(request).getAsJsonObject();

            writer.beginObject();
            switch (RequestType.parse(exchange.getRequestMethod())) {
                case GET -> {
                    User user = UserManager.get(exchange.getRemoteAddress().getAddress());
                    writer.name("data").beginArray();
                    for (String data : UserManager.get(user))
                        writer.value(data);
                    writer.endArray();
                }
                case PATCH -> {
                    JsonArray array = parsed.getAsJsonArray("data");
                    writer.name("data").beginArray();
                    for (JsonElement element : array) {
                        String name = element.getAsString();
                        ConcurrentLinkedQueue<String> data = UserManager.get(name);
                        writer.beginObject();
                        writer.name("name").value(name);
                        if (data == null)
                            writer.name("message").value("user.not.exists");
                        else {
                            writer.name("message").value("user.exists");
                            writer.name("data").beginArray();
                            for (String d : data)
                                writer.value(d);
                            writer.endArray();
                        }
                        writer.endObject();
                    }
                    writer.endArray();
                }
                case PUT -> {
                    ConcurrentLinkedQueue<String> data = new ConcurrentLinkedQueue<>();
                    JsonArray array = parsed.getAsJsonArray("data");
                    for (JsonElement element : array)
                        data.add(element.getAsString());
                    UserManager.update(UserManager.get(exchange.getRemoteAddress().getAddress()), data);
                    writer.name("success").value("update.data.success");
                }
            }
            writer.endObject()
                    .flush();

            exchange.sendResponseHeaders(200, response.toString().length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
