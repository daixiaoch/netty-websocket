package org.cola.nettywebsocket.core.route;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.cola.nettywebsocket.core.message.MessageRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Description: a simple httpclient
 * @author: cola
 * @date: 2022年06月01日 22:41
 */
public class HttpClient {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(HttpClient.class);

    public static void sendPost(String url, MessageRequest request)  {
        HttpURLConnection connection = null;
        try {

            connection = (HttpURLConnection) new URL("http://" + url).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            //write header
            connection.setRequestProperty("Content-Type", "application/json");

            //write body
            PrintWriter writer = new PrintWriter(connection.getOutputStream());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(request));
            writer.flush();
            int responseCode = connection.getResponseCode();
            BufferedReader reader;
            if (responseCode == HttpResponseStatus.OK.code()) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                LOGGER.error(">>>>>>>>>>>  http is bad，code={}", responseCode);
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            //read response
            String line;
            while ((line = reader.readLine()) != null) {

            }
            reader.close();

        }catch (IOException ex){
            LOGGER.error(">>>>>>>>>>>  http exception.", ex);
        }finally {
            connection.disconnect();
        }
    }
}