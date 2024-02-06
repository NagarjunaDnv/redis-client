package org.example.redisclient;

import org.example.resp.resp3.RESP3Encoder;
import org.example.resp.resp3.types.CommandType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class RedisClient {
    private final String host;
    private final int port;

    public RedisClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private String sendRespEncodedValue(String command) {

        StringBuilder responseBuilder = new StringBuilder();

        try (Socket socket = new Socket(host, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)){


            out.println(command);

            String line;
            responseBuilder = new StringBuilder();
            while ((line = in.readLine()) != null) {
                responseBuilder.append(line).append('\n');
            }

            return responseBuilder.toString();
        } catch (IOException e) {
            return responseBuilder.toString();
        }
    }

    public String set(String key, String value) {
        String command = RESP3Encoder.encode(List.of(CommandType.SET.name(), key, value));
        return sendRespEncodedValue(command);
    }

    public String get(String key) {
        String command = RESP3Encoder.encode(List.of(CommandType.GET.name(), key));
        return sendRespEncodedValue(command);
    }

    public String delete(String key) {
        String command = RESP3Encoder.encode(List.of(CommandType.DEL.name(), key));
        return sendRespEncodedValue(command);
    }

    public String query(List<String> commands) {
        String command = RESP3Encoder.encode(commands);
        return sendRespEncodedValue(command);
    }

    public static void main(String[] args) {
        //
        //        System.out.println(encodeToRESP(List.of("GET", "hello")));
        //        System.out.println(encodeToRESP(List.of("SET", "KEY", "VALUE")));
        //        System.out.println(encodeToRESP(List.of("a", "z")));

        //        String encodedMessage = RESP3Encoder.encode(List.of("GET", "hello"));
        //
        //        Object decodedMessage = RESP3Decoder.decode(new ByteArrayInputStream(encodedMessage.getBytes()));
        //
        //        System.out.println(decodedMessage.toString());

        RedisClient redisClient = new RedisClient("localhost", 6379);

        try {
            String setResult = redisClient.set("key1", "value1");
            System.out.println("SET Result: " + setResult);

            String getResult = redisClient.get("key1");
            System.out.println("GET Result: " + getResult);

            String deleteResult = redisClient.delete("key1");
            System.out.println("DELETE Result: " + deleteResult);

            String zadd = redisClient.query(List.of("ZADD", "subjects", "1", "socialhjhb"));
            System.out.println("QUERY Result: " + zadd);
//
            String zrange = redisClient.query(List.of("ZRANGE", "subjects", "1", "2"));
            System.out.println("QUERY Result: " + zrange);

        } catch (RuntimeException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
