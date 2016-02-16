package io.github.netbrain.nicrofw.request;


import fi.iki.elonen.NanoHTTPD;

import java.io.InputStream;
import java.util.Map;

public class RequestContext {

    private final NanoHTTPD.Method method;
    private final RequestHeaders headers;
    private final InputStream body;
    private final String uri;
    private final QueryParameters queryParameters;

    //Enhancement: create abstraction layer between NanoHTTPD and this micro framework
    public RequestContext(NanoHTTPD.IHTTPSession session) {
        headers = new RequestHeaders(session.getHeaders());
        queryParameters = new QueryParameters(session.getParms());
        method = session.getMethod();
        body = session.getInputStream();
        uri = session.getUri();
    }

    public NanoHTTPD.Method getMethod() {
        return method;
    }

    public RequestHeaders getHeaders() {
        return headers;
    }

    public InputStream getBody() {
        return body;
    }

    public String getUri() {
        return uri;
    }

    public Map<String,String> getQueryParameters() {
        return queryParameters;
    }
}
