package io.github.netbrain.nicrofw.request;


import java.util.HashMap;
import java.util.Map;

public class QueryParameters extends HashMap<String,String> {
    public QueryParameters(Map<? extends String, ? extends String> m) {
        super(m);
    }
}
