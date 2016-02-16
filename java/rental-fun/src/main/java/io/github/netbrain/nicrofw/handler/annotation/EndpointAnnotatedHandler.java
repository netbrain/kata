package io.github.netbrain.nicrofw.handler.annotation;


import io.github.netbrain.nicrofw.handler.Handler;
import io.github.netbrain.nicrofw.handler.ClassFactory;
import io.github.netbrain.nicrofw.handler.TypeConverter;
import io.github.netbrain.nicrofw.request.QueryParameters;
import io.github.netbrain.nicrofw.request.RequestContext;
import io.github.netbrain.nicrofw.request.RequestHeaders;
import org.apache.commons.collections4.IterableUtils;
import org.boon.json.ObjectMapper;
import sun.misc.IOUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;

public class EndpointAnnotatedHandler implements Handler {
    private final Endpoint annotation;
    private final Method method;
    private final ClassFactory classFactory;
    private final TypeConverter typeConverter;

    public EndpointAnnotatedHandler(Endpoint annotation, Method method, ClassFactory classFactory, TypeConverter typeConverter) {
        this.annotation = annotation;
        this.method = method;
        this.classFactory = classFactory;
        this.typeConverter = typeConverter;
    }

    @Override
    public Object invoke(LinkedHashMap<String, String> pathParams, RequestContext requestContext) {
        try {
            Parameter[] parameters = method.getParameters();
            Object[] invocationParameters = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++){
                Parameter parameter = parameters[i];

                //Inject based on parameter class type
                if(parameter.getType().equals(QueryParameters.class)){
                    invocationParameters[i] = requestContext.getQueryParameters();
                }else if(parameter.getType().equals(RequestHeaders.class)){
                    invocationParameters[i] = requestContext.getHeaders();
                }else if(parameter.getType().equals(RequestContext.class)){
                    invocationParameters[i] = requestContext;
                }else if(pathParams.size() > i){
                    invocationParameters[i] = typeConverter.convert(
                            IterableUtils.get(
                                    pathParams.values(),
                                    i
                            ),
                            parameter.getType()
                    );
                }else if (i+1 == parameters.length){
                    //Assume the last unmapped parameter to be the request data to be
                    //converted from json and explicitly casted to the proper type.
                    Class cls = parameter.getType().asSubclass(Object.class);
                    int contentLength = Integer.parseInt(requestContext.getHeaders().getOrDefault("Content-Length","0"));
                    if(contentLength > 0) {
                        byte[] data = IOUtils.readFully(requestContext.getBody(), contentLength, true);
                        ObjectMapper json = classFactory.get(ObjectMapper.class);
                        invocationParameters[i] = json.readValue(data, cls);
                    }
                }
            }

            Object handler = classFactory.get(method.getDeclaringClass());
            return method.invoke(handler,invocationParameters);

        } catch (IllegalAccessException e) {
            throw new RuntimeException("You seem to have annotated an inaccessible method!",e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Can't invoke method on handler class",e.getTargetException());
        } catch (IOException e) {
            throw new RuntimeException("Could not read request body.",e);
        }
    }
}
