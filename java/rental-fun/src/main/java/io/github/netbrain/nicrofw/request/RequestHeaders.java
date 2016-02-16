package io.github.netbrain.nicrofw.request;


import java.util.HashMap;
import java.util.Map;

public class RequestHeaders extends HashMap<String,String> {

    public RequestHeaders(Map<String, String> headers) {
        this.putAll(headers);
    }

    @Override
    public String get(Object key) {
        return super.getOrDefault(
                key,
                super.get(((String) key).toLowerCase())
        );
    }

    @Override
    public String getOrDefault(Object key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }
}
