package io.github.netbrain.nicrofw;

import fi.iki.elonen.NanoHTTPD;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.netbrain.nicrofw.handler.ClassFactory;
import io.github.netbrain.nicrofw.handler.DefaultClassFactory;
import io.github.netbrain.nicrofw.handler.DefaultTypeConverter;
import io.github.netbrain.nicrofw.handler.TypeConverter;
import io.github.netbrain.nicrofw.handler.annotation.Endpoint;
import io.github.netbrain.nicrofw.handler.annotation.EndpointAnnotatedHandler;
import io.github.netbrain.nicrofw.request.RequestContext;

import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

public class Router {
    private final static Logger log = Logger.getLogger(Router.class.getName());
    private final ClassFactory classFactory;
    private final TypeConverter typeConverter;

    private final Set<UriMap> routes = new HashSet<>();

    public Router() {
        this(new DefaultClassFactory(), new DefaultTypeConverter());
    }

    public Router(ClassFactory classFactory, TypeConverter typeConverter) {
        this.classFactory = classFactory;
        this.typeConverter = typeConverter;
    }


    public void scanAnnotations(String ... classpaths) {
        List<Class> matchingClasses = new ArrayList<>();
        for(String classpath : classpaths) {
            new FastClasspathScanner(classpath).matchAllStandardClasses(matchingClasses::add).scan();
        }
        for(Class cls : matchingClasses) {
            for(Method handlerMethod : cls.getDeclaredMethods()){
                Endpoint pathAnnotation = handlerMethod.getDeclaredAnnotation(Endpoint.class);
                if(pathAnnotation != null) {
                    EndpointAnnotatedHandler endpointAnnotatedHandler = new EndpointAnnotatedHandler(pathAnnotation, handlerMethod, classFactory, typeConverter);
                    NanoHTTPD.Method requestMethod = pathAnnotation.method();
                    String requestPath = pathAnnotation.value();

                    UriMap uriMap = new UriMap(requestMethod, requestPath, endpointAnnotatedHandler);
                    this.routes.add(uriMap);

                    log.info(String.format("Added route [%s] %s", requestMethod, requestPath));
                }
            }
        }
    }

    public Object handle(NanoHTTPD.IHTTPSession session) throws RouteNotFoundException {
        for(UriMap uriMap : routes){
            LinkedHashMap<String, String> matches = uriMap.matches(session.getMethod(),session.getUri());
            if(matches != null){
                return uriMap.getHandler().invoke(matches,new RequestContext(session));
            }
        }
        throw new RouteNotFoundException(String.format("No routes matched: [%s] %s",session.getMethod(),session.getUri()),routes);
    }

    public Set<UriMap> getRoutes() {
        return routes;
    }
}
