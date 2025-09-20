package qwq.arcane.gui.alt.elixir.compat;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import qwq.arcane.gui.alt.elixir.account.MicrosoftAccount;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/alt/elixir/compat/OAuthServer.class */
public class OAuthServer {
    private final OAuthHandler handler;
    private final MicrosoftAccount.AuthMethod authMethod;
    private final HttpServer httpServer = HttpServer.create(new InetSocketAddress("localhost", 21919), 0);
    private final String context = "/login";
    private final ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

    public OAuthServer(OAuthHandler handler, MicrosoftAccount.AuthMethod authMethod) throws IOException {
        this.handler = handler;
        this.authMethod = authMethod;
    }

    public void start() {
        this.httpServer.createContext(this.context, new OAuthHttpHandler(this, this.authMethod));
        this.httpServer.setExecutor(this.threadPoolExecutor);
        this.httpServer.start();
        this.handler.openUrl(MicrosoftAccount.replaceKeys(this.authMethod, MicrosoftAccount.XBOX_PRE_AUTH_URL));
    }

    public void stop(boolean isInterrupt) {
        this.httpServer.stop(0);
        this.threadPoolExecutor.shutdown();
        if (isInterrupt) {
            this.handler.authError("Has been interrupted");
        }
    }

    /* loaded from: Arcane 8.10.jar:qwq/arcane/gui/alt/elixir/compat/OAuthServer$OAuthHttpHandler.class */
    static class OAuthHttpHandler implements HttpHandler {
        private OAuthServer server;
        private MicrosoftAccount.AuthMethod authMethod;

        OAuthHttpHandler(OAuthServer server, MicrosoftAccount.AuthMethod authMethod) {
            this.server = server;
            this.authMethod = authMethod;
        }

        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> query = getQueryParams(exchange.getRequestURI().getQuery());
            if (query.containsKey("code")) {
                try {
                    this.server.handler.authResult(MicrosoftAccount.buildFromAuthCode(query.get("code"), this.authMethod));
                    response(exchange, "Login Success", 200);
                } catch (Exception e) {
                    e.printStackTrace();
                    this.server.handler.authError(e.toString());
                    response(exchange, "Error: " + e, 500);
                }
            } else {
                this.server.handler.authError("No code in the query");
                response(exchange, "No code in the query", 500);
            }
            this.server.stop(false);
        }

        private Map<String, String> getQueryParams(String query) {
            Map<String, String> params = new HashMap<>();
            if (query != null) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    params.put(pair.substring(0, idx), pair.substring(idx + 1));
                }
            }
            return params;
        }

        private void response(HttpExchange exchange, String message, int code) throws IOException {
            byte[] bytes = message.getBytes();
            exchange.sendResponseHeaders(code, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }
}
