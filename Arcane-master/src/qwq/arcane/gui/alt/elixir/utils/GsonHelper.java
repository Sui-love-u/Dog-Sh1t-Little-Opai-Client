package qwq.arcane.gui.alt.elixir.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/* loaded from: Arcane 8.10.jar:qwq/arcane/gui/alt/elixir/utils/GsonHelper.class */
public final class GsonHelper {
    public static void set(JsonObject object, String key, JsonElement value) {
        object.add(key, value);
    }

    public static void set(JsonObject object, String key, char value) {
        object.addProperty(key, Character.valueOf(value));
    }

    public static void set(JsonObject object, String key, Number value) {
        object.addProperty(key, value);
    }

    public static void set(JsonObject object, String key, String value) {
        object.addProperty(key, value);
    }

    public static void set(JsonObject object, String key, boolean value) {
        object.addProperty(key, Boolean.valueOf(value));
    }

    public static String string(JsonObject object, String key) {
        if (object.has(key)) {
            return object.get(key).getAsString();
        }
        return null;
    }

    public static JsonObject obj(JsonObject object, String key) {
        if (object.has(key)) {
            return object.get(key).getAsJsonObject();
        }
        return null;
    }

    public static JsonArray array(JsonObject object, String key) {
        if (object.has(key)) {
            return object.get(key).getAsJsonArray();
        }
        return null;
    }

    public static String string(JsonArray array, int index) {
        if (array.size() > index) {
            return array.get(index).getAsString();
        }
        return null;
    }

    public static JsonObject obj(JsonArray array, int index) {
        if (array.size() > index) {
            return array.get(index).getAsJsonObject();
        }
        return null;
    }

    public static JsonArray array(JsonArray array, int index) {
        if (array.size() > index) {
            return array.get(index).getAsJsonArray();
        }
        return null;
    }
}
