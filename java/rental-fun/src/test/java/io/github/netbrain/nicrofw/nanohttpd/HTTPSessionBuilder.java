package io.github.netbrain.nicrofw.nanohttpd;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import org.boon.json.JsonFactory;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HTTPSessionBuilder {
    private final IHTTPSession session;
    private final Map<String,String> headers = new HashMap<>();

    public HTTPSessionBuilder(String path) {
        this(Method.GET,path);
    }

    public HTTPSessionBuilder(Method method, String path) {
        this.session = mock(IHTTPSession.class);
        when(session.getMethod()).thenReturn(method);
        when(session.getUri()).thenReturn(path);
        when(session.getHeaders()).thenReturn(headers);
    }

    /**
     * Converts an object to json and sets it as the request body
     */
    public HTTPSessionBuilder withBody(Object bodyData){
        String jsonData = JsonFactory.toJson(bodyData);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonData.getBytes());
        when(session.getInputStream()).thenReturn(inputStream);
        headers.put("Content-Length",String.valueOf(inputStream.available()));
        return this;
    }

    public HTTPSessionBuilder withHeaders(Map<String,String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public HTTPSessionBuilder withHeader(String key, String value) {
        headers.put(key,value);
        return this;
    }

    public IHTTPSession build(){
        return session;
    }
}
