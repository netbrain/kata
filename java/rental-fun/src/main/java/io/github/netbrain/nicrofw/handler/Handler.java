package io.github.netbrain.nicrofw.handler;

import io.github.netbrain.nicrofw.request.RequestContext;

import java.util.LinkedHashMap;

public interface Handler {
    Object invoke(LinkedHashMap<String, String> pathParams, RequestContext requestContext);
}
