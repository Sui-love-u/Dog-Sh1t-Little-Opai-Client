/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class GsonUtil {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static JsonObject fromJson(String s) {
        return gson.fromJson(s, JsonObject.class);
    }

    public static <T> String toJson(T obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String s, Class<T> c) {
        return gson.fromJson(s, c);
    }
}

