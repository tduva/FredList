package io.github.tduva.fredlist.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by tduva on 08.08.2017.
 */

public class MiscUtil {

    public static Integer getKeyFromValue(Map<Integer, String> map, String value) {
        for (Integer key : map.keySet()) {
            if (value.equals(map.get(key))) {
                return key;
            }
        }
        return null;
    }

    public static JSONObject mapToJSONObject(Map<?, ?> map) throws JSONException {
        JSONObject result = new JSONObject();
        for (Object o : map.keySet()) {
            result.put(String.valueOf(o), String.valueOf(map.get(o)));
        }
        return result;
    }

    public static void fillMapFromJSONObject(Map<Integer, String> map, JSONObject data) throws JSONException {
        if (data == null) {
            Helper.debug("null");
            return;
        }
        Iterator<String> it = data.keys();
        while (it.hasNext()) {
            String id = it.next();
            String name = data.getString(id);
            map.put(Integer.valueOf(id), name);
        }
    }

}
