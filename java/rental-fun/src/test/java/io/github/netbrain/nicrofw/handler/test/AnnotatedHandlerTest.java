package io.github.netbrain.nicrofw.handler.test;

import io.github.netbrain.nicrofw.handler.annotation.Endpoint;
import io.github.netbrain.nicrofw.request.RequestHeaders;

import java.util.Map;

import static fi.iki.elonen.NanoHTTPD.Method.POST;

/**
 * Test class for an annotated rest api handler
 */
public class AnnotatedHandlerTest {
    @Endpoint("/test/annotation")
    public String testSimpleAnnotation(){
        return "Hello from simple!";
    }

    @Endpoint(value = "/test/postdata",method = POST)
    public Map<String,String> testPost(Map<String,String> postdata){
        return postdata;
    }

    @Endpoint(value = "/test/headers")
    public RequestHeaders testPost(RequestHeaders headers){
        return headers;
    }
}
