package qwq.arcane.gui.alt.auth;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.sun.net.httpserver.HttpServer;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.util.Session;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import qwq.arcane.gui.alt.elixir.account.MicrosoftAccount;
import qwq.arcane.gui.alt.utils.SystemUtils;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/alt/auth/MicrosoftAuth.class */
public final class MicrosoftAuth {
    public static final RequestConfig REQUEST_CONFIG = RequestConfig.custom().setConnectionRequestTimeout(30000).setConnectTimeout(30000).setSocketTimeout(30000).build();
    public static final String CLIENT_ID = "42a60a84-599d-44b2-a7c6-b00cdef1d6a2";
    public static final int PORT = 25575;

    public static CompletableFuture<String> acquireMSAuthCode(Executor executor) {
        return acquireMSAuthCode(SystemUtils::openWebLink, executor);
    }

    public static CompletableFuture<String> acquireMSAuthCode(Consumer<URI> browserAction, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String state = RandomStringUtils.randomAlphanumeric(8);
                HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
                CountDownLatch latch = new CountDownLatch(1);
                AtomicReference<String> authCode = new AtomicReference<>(null);
                AtomicReference<String> errorMsg = new AtomicReference<>(null);
                server.createContext("/callback", exchange -> {
                    Map<String, String> query = (Map) URLEncodedUtils.parse(exchange.getRequestURI().toString().replaceAll("/callback\\?", ""), StandardCharsets.UTF_8).stream().collect(Collectors.toMap((v0) -> {
                        return v0.getName();
                    }, (v0) -> {
                        return v0.getValue();
                    }));
                    if (!state.equals(query.get("state"))) {
                        errorMsg.set(String.format("State mismatch! Expected '%s' but got '%s'.", state, query.get("state")));
                    } else if (query.containsKey("code")) {
                        authCode.set(query.get("code"));
                    } else if (query.containsKey("error")) {
                        errorMsg.set(String.format("%s: %s", query.get("error"), query.get("error_description")));
                    }
                    InputStream stream = MicrosoftAuth.class.getResourceAsStream("/callback.html");
                    byte[] response = stream != null ? IOUtils.toByteArray(stream) : new byte[0];
                    exchange.getResponseHeaders().add("Content-Type", "text/html");
                    exchange.sendResponseHeaders(200, response.length);
                    exchange.getResponseBody().write(response);
                    exchange.getResponseBody().close();
                    latch.countDown();
                });
                URIBuilder uriBuilder = new URIBuilder("https://login.live.com/oauth20_authorize.srf").addParameter("client_id", CLIENT_ID).addParameter("response_type", "code").addParameter("redirect_uri", String.format("http://localhost:%d/callback", Integer.valueOf(server.getAddress().getPort()))).addParameter("scope", "XboxLive.signin XboxLive.offline_access").addParameter("state", state).addParameter("prompt", "select_account");
                URI uri = uriBuilder.build();
                browserAction.accept(uri);
                try {
                    server.start();
                    latch.await();
                    String str = (String) Optional.ofNullable(authCode.get()).filter(code -> {
                        return !StringUtils.isBlank(code);
                    }).orElseThrow(() -> {
                        return new Exception((String) Optional.ofNullable((String) errorMsg.get()).orElse("There was no auth code or error description present."));
                    });
                    server.stop(2);
                    return str;
                } catch (Throwable th) {
                    server.stop(2);
                    throw th;
                }
            } catch (InterruptedException e) {
                throw new CancellationException("Microsoft auth code acquisition was cancelled!");
            } catch (Exception e2) {
                throw new CompletionException("Unable to acquire Microsoft auth code!", e2);
            }
        }, executor);
    }

    public static CompletableFuture<Map<String, String>> acquireMSAccessTokens(String authCode, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CloseableHttpClient client = HttpClients.createMinimal();
                try {
                    HttpPost request = new HttpPost(URI.create(MicrosoftAccount.XBOX_AUTH_URL));
                    request.setConfig(REQUEST_CONFIG);
                    request.setHeader("Content-Type", "application/x-www-form-urlencoded");
                    request.setEntity(new UrlEncodedFormEntity(Arrays.asList(new BasicNameValuePair("client_id", CLIENT_ID), new BasicNameValuePair("grant_type", "authorization_code"), new BasicNameValuePair("code", authCode), new BasicNameValuePair("redirect_uri", String.format("http://localhost:%d/callback", Integer.valueOf(PORT)))), "UTF-8"));
                    JsonObject json = new JsonParser().parse(EntityUtils.toString(client.execute(request).getEntity())).getAsJsonObject();
                    String accessToken = (String) Optional.ofNullable(json.get("access_token")).map((v0) -> {
                        return v0.getAsString();
                    }).filter(token -> {
                        return !StringUtils.isBlank(token);
                    }).orElseThrow(() -> {
                        String str;
                        if (json.has("error")) {
                            str = String.format("%s: %s", json.get("error").getAsString(), json.get("error_description").getAsString());
                        } else {
                            str = "There was no Microsoft access token or error description present.";
                        }
                        return new Exception(str);
                    });
                    String refreshToken = (String) Optional.ofNullable(json.get("refresh_token")).map((v0) -> {
                        return v0.getAsString();
                    }).filter(token2 -> {
                        return !StringUtils.isBlank(token2);
                    }).orElseThrow(() -> {
                        String str;
                        if (json.has("error")) {
                            str = String.format("%s: %s", json.get("error").getAsString(), json.get("error_description").getAsString());
                        } else {
                            str = "There was no Microsoft refresh token or error description present.";
                        }
                        return new Exception(str);
                    });
                    Map<String, String> result = new HashMap<>();
                    result.put("access_token", accessToken);
                    result.put("refresh_token", refreshToken);
                    if (client != null) {
                        client.close();
                    }
                    return result;
                } catch (Throwable th) {
                    if (client != null) {
                        try {
                            client.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (InterruptedException e) {
                throw new CancellationException("Microsoft access tokens acquisition was cancelled!");
            } catch (Exception e2) {
                throw new CompletionException("Unable to acquire Microsoft access tokens!", e2);
            }
        }, executor);
    }

    public static CompletableFuture<Map<String, String>> refreshMSAccessTokens(String msToken, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CloseableHttpClient client = HttpClients.createMinimal();
                try {
                    HttpPost request = new HttpPost(URI.create(MicrosoftAccount.XBOX_AUTH_URL));
                    request.setConfig(REQUEST_CONFIG);
                    request.setHeader("Content-Type", "application/x-www-form-urlencoded");
                    request.setEntity(new UrlEncodedFormEntity(Arrays.asList(new BasicNameValuePair("client_id", CLIENT_ID), new BasicNameValuePair("grant_type", "refresh_token"), new BasicNameValuePair("refresh_token", msToken), new BasicNameValuePair("redirect_uri", String.format("http://localhost:%d/callback", Integer.valueOf(PORT)))), "UTF-8"));
                    JsonObject json = new JsonParser().parse(EntityUtils.toString(client.execute(request).getEntity())).getAsJsonObject();
                    String accessToken = (String) Optional.ofNullable(json.get("access_token")).map((v0) -> {
                        return v0.getAsString();
                    }).filter(token -> {
                        return !StringUtils.isBlank(token);
                    }).orElseThrow(() -> {
                        String str;
                        if (json.has("error")) {
                            str = String.format("%s: %s", json.get("error").getAsString(), json.get("error_description").getAsString());
                        } else {
                            str = "There was no Microsoft access token or error description present.";
                        }
                        return new Exception(str);
                    });
                    String refreshToken = (String) Optional.ofNullable(json.get("refresh_token")).map((v0) -> {
                        return v0.getAsString();
                    }).filter(token2 -> {
                        return !StringUtils.isBlank(token2);
                    }).orElseThrow(() -> {
                        String str;
                        if (json.has("error")) {
                            str = String.format("%s: %s", json.get("error").getAsString(), json.get("error_description").getAsString());
                        } else {
                            str = "There was no Microsoft refresh token or error description present.";
                        }
                        return new Exception(str);
                    });
                    Map<String, String> result = new HashMap<>();
                    result.put("access_token", accessToken);
                    result.put("refresh_token", refreshToken);
                    if (client != null) {
                        client.close();
                    }
                    return result;
                } catch (Throwable th) {
                    if (client != null) {
                        try {
                            client.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (InterruptedException e) {
                throw new CancellationException("Microsoft access tokens acquisition was cancelled!");
            } catch (Exception e2) {
                throw new CompletionException("Unable to acquire Microsoft access tokens!", e2);
            }
        }, executor);
    }

    public static CompletableFuture<String> acquireXboxAccessToken(String accessToken, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            JsonObject jsonObject;
            try {
                CloseableHttpClient client = HttpClients.createMinimal();
                try {
                    HttpPost request = new HttpPost(URI.create(MicrosoftAccount.XBOX_XBL_URL));
                    JsonObject entity = new JsonObject();
                    JsonObject properties = new JsonObject();
                    properties.addProperty("AuthMethod", "RPS");
                    properties.addProperty("SiteName", "user.auth.xboxlive.com");
                    properties.addProperty("RpsTicket", String.format("d=%s", accessToken));
                    entity.add("Properties", properties);
                    entity.addProperty("RelyingParty", "http://auth.xboxlive.com");
                    entity.addProperty("TokenType", "JWT");
                    request.setConfig(REQUEST_CONFIG);
                    request.setHeader("Content-Type", "application/json");
                    request.setEntity(new StringEntity(entity.toString()));
                    CloseableHttpResponse closeableHttpResponseExecute = client.execute(request);
                    if (closeableHttpResponseExecute.getStatusLine().getStatusCode() == 200) {
                        jsonObject = new JsonParser().parse(EntityUtils.toString(closeableHttpResponseExecute.getEntity())).getAsJsonObject();
                    } else {
                        jsonObject = new JsonObject();
                    }
                    JsonObject json = jsonObject;
                    String str = (String) Optional.ofNullable(json.get("Token")).map((v0) -> {
                        return v0.getAsString();
                    }).filter(token -> {
                        return !StringUtils.isBlank(token);
                    }).orElseThrow(() -> {
                        String str2;
                        if (json.has("XErr")) {
                            str2 = String.format("%s: %s", json.get("XErr").getAsString(), json.get("Message").getAsString());
                        } else {
                            str2 = "There was no access token or error description present.";
                        }
                        return new Exception(str2);
                    });
                    if (client != null) {
                        client.close();
                    }
                    return str;
                } catch (Throwable th) {
                    if (client != null) {
                        try {
                            client.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (InterruptedException e) {
                throw new CancellationException("Xbox Live access token acquisition was cancelled!");
            } catch (Exception e2) {
                throw new CompletionException("Unable to acquire Xbox Live access token!", e2);
            }
        }, executor);
    }

    public static CompletableFuture<Map<String, String>> acquireXboxXstsToken(String accessToken, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            JsonObject jsonObject;
            try {
                CloseableHttpClient client = HttpClients.createMinimal();
                try {
                    HttpPost request = new HttpPost(MicrosoftAccount.XBOX_XSTS_URL);
                    JsonObject entity = new JsonObject();
                    JsonObject properties = new JsonObject();
                    JsonArray userTokens = new JsonArray();
                    userTokens.add(new JsonPrimitive(accessToken));
                    properties.addProperty("SandboxId", "RETAIL");
                    properties.add("UserTokens", userTokens);
                    entity.add("Properties", properties);
                    entity.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
                    entity.addProperty("TokenType", "JWT");
                    request.setConfig(REQUEST_CONFIG);
                    request.setHeader("Content-Type", "application/json");
                    request.setEntity(new StringEntity(entity.toString()));
                    CloseableHttpResponse closeableHttpResponseExecute = client.execute(request);
                    if (closeableHttpResponseExecute.getStatusLine().getStatusCode() == 200) {
                        jsonObject = new JsonParser().parse(EntityUtils.toString(closeableHttpResponseExecute.getEntity())).getAsJsonObject();
                    } else {
                        jsonObject = new JsonObject();
                    }
                    JsonObject json = jsonObject;
                    Map map = (Map) Optional.ofNullable(json.get("Token")).map((v0) -> {
                        return v0.getAsString();
                    }).filter(token -> {
                        return !StringUtils.isBlank(token);
                    }).map(token2 -> {
                        String uhs = json.get("DisplayClaims").getAsJsonObject().get("xui").getAsJsonArray().get(0).getAsJsonObject().get("uhs").getAsString();
                        Map<String, String> result = new HashMap<>();
                        result.put("Token", token2);
                        result.put("uhs", uhs);
                        return result;
                    }).orElseThrow(() -> {
                        String str;
                        if (json.has("XErr")) {
                            str = String.format("%s: %s", json.get("XErr").getAsString(), json.get("Message").getAsString());
                        } else {
                            str = "There was no access token or error description present.";
                        }
                        return new Exception(str);
                    });
                    if (client != null) {
                        client.close();
                    }
                    return map;
                } catch (Throwable th) {
                    if (client != null) {
                        try {
                            client.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (InterruptedException e) {
                throw new CancellationException("Xbox Live XSTS token acquisition was cancelled!");
            } catch (Exception e2) {
                throw new CompletionException("Unable to acquire Xbox Live XSTS token!", e2);
            }
        }, executor);
    }

    public static CompletableFuture<String> acquireMCAccessToken(String xstsToken, String userHash, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CloseableHttpClient client = HttpClients.createMinimal();
                try {
                    HttpPost request = new HttpPost(URI.create(MicrosoftAccount.MC_AUTH_URL));
                    request.setConfig(REQUEST_CONFIG);
                    request.setHeader("Content-Type", "application/json");
                    request.setEntity(new StringEntity(String.format("{\"identityToken\": \"XBL3.0 x=%s;%s\"}", userHash, xstsToken)));
                    JsonObject json = new JsonParser().parse(EntityUtils.toString(client.execute(request).getEntity())).getAsJsonObject();
                    String str = (String) Optional.ofNullable(json.get("access_token")).map((v0) -> {
                        return v0.getAsString();
                    }).filter(token -> {
                        return !StringUtils.isBlank(token);
                    }).orElseThrow(() -> {
                        String str2;
                        if (json.has("error")) {
                            str2 = String.format("%s: %s", json.get("error").getAsString(), json.get("errorMessage").getAsString());
                        } else {
                            str2 = "There was no access token or error description present.";
                        }
                        return new Exception(str2);
                    });
                    if (client != null) {
                        client.close();
                    }
                    return str;
                } finally {
                }
            } catch (InterruptedException e) {
                throw new CancellationException("Minecraft access token acquisition was cancelled!");
            } catch (Exception e2) {
                throw new CompletionException("Unable to acquire Minecraft access token!", e2);
            }
        }, executor);
    }

    public static CompletableFuture<Session> login(String mcToken, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CloseableHttpClient client = HttpClients.createMinimal();
                try {
                    HttpGet request = new HttpGet(URI.create(MicrosoftAccount.MC_PROFILE_URL));
                    request.setConfig(REQUEST_CONFIG);
                    request.setHeader("Authorization", "Bearer " + mcToken);
                    JsonObject json = new JsonParser().parse(EntityUtils.toString(client.execute(request).getEntity())).getAsJsonObject();
                    Session session = (Session) Optional.ofNullable(json.get("id")).map((v0) -> {
                        return v0.getAsString();
                    }).filter(uuid -> {
                        return !StringUtils.isBlank(uuid);
                    }).map(uuid2 -> {
                        return new Session(json.get("name").getAsString(), uuid2, mcToken, Session.Type.MOJANG.toString());
                    }).orElseThrow(() -> {
                        String str;
                        if (json.has("error")) {
                            str = String.format("%s: %s", json.get("error").getAsString(), json.get("errorMessage").getAsString());
                        } else {
                            str = "There was no profile or error description present.";
                        }
                        return new Exception(str);
                    });
                    if (client != null) {
                        client.close();
                    }
                    return session;
                } catch (Throwable th) {
                    if (client != null) {
                        try {
                            client.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (InterruptedException e) {
                throw new CancellationException("Minecraft profile fetching was cancelled!");
            } catch (Exception e2) {
                throw new CompletionException("Unable to fetch Minecraft profile!", e2);
            }
        }, executor);
    }
}
