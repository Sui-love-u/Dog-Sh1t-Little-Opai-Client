package qwq.arcane.gui.alt.auth;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/alt/auth/Account.class */
public class Account {
    private String refreshToken;
    private String accessToken;
    private String username;
    private long timestamp;
    private String uuid;

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getUsername() {
        return this.username;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public Account(String refreshToken, String accessToken, String username, long timestamp, String uuid) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.username = username;
        this.timestamp = timestamp;
        this.uuid = uuid;
    }

    public String getUUID() {
        return this.uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }
}
