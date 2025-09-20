package qwq.arcane.gui.alt.elixir.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import net.optifine.http.HttpPipeline;
import net.optifine.http.HttpRequest;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/alt/elixir/utils/HttpUtils.class */
public class HttpUtils {
    public static final String DEFAULT_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";

    public static HttpURLConnection make(String url, String method) throws IOException {
        return make(url, method, "", new HashMap(), DEFAULT_AGENT);
    }

    public static HttpURLConnection make(String url, String method, String data) throws IOException {
        return make(url, method, data, new HashMap(), DEFAULT_AGENT);
    }

    public static HttpURLConnection make(String url, String method, String data, Map<String, String> header) throws IOException {
        return make(url, method, data, header, DEFAULT_AGENT);
    }

    public static HttpURLConnection make(String url, String method, String data, Map<String, String> header, String agent) throws IOException {
        HttpURLConnection httpConnection = (HttpURLConnection) new URL(url).openConnection();
        httpConnection.setRequestMethod(method);
        httpConnection.setConnectTimeout(2000);
        httpConnection.setReadTimeout(10000);
        httpConnection.setRequestProperty(HttpPipeline.HEADER_USER_AGENT, agent);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            httpConnection.setRequestProperty(entry.getKey(), entry.getValue());
        }
        httpConnection.setInstanceFollowRedirects(true);
        httpConnection.setDoOutput(true);
        if (!data.isEmpty()) {
            DataOutputStream dataOutputStream = new DataOutputStream(httpConnection.getOutputStream());
            try {
                dataOutputStream.writeBytes(data);
                dataOutputStream.flush();
                dataOutputStream.close();
            } catch (Throwable th) {
                try {
                    dataOutputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
        httpConnection.connect();
        return httpConnection;
    }

    public static String request(String url, String method, String data, Map<String, String> header, String agent) throws IOException {
        HttpURLConnection connection = make(url, method, data, header, agent);
        InputStream inputStream = connection.getInputStream();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            try {
                StringBuilder response = new StringBuilder();
                char[] buffer = new char[1024];
                while (true) {
                    int bytesRead = inputStreamReader.read(buffer);
                    if (bytesRead == -1) {
                        break;
                    }
                    response.append(buffer, 0, bytesRead);
                }
                String string = response.toString();
                inputStreamReader.close();
                if (inputStream != null) {
                    inputStream.close();
                }
                return string;
            } finally {
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public static String get(String url, Map<String, String> header) throws IOException {
        return request(url, HttpRequest.METHOD_GET, "", header, DEFAULT_AGENT);
    }

    public static String post(String url, String data, Map<String, String> header) throws IOException {
        return request(url, HttpRequest.METHOD_POST, data, header, DEFAULT_AGENT);
    }

    public static String readText(HttpURLConnection connection) throws IOException {
        return readText(toReader(connection));
    }

    public static InputStreamReader toReader(HttpURLConnection urlConnection) throws IOException {
        return new InputStreamReader(urlConnection.getInputStream());
    }

    public static String readText(InputStreamReader reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            int buffer = reader.read();
            if (buffer != -1) {
                stringBuilder.append((char) buffer);
            } else {
                return stringBuilder.toString();
            }
        }
    }
}
