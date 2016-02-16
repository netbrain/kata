package io.github.netbrain.nicrofw;

import java.util.Set;
import java.util.stream.Collectors;

public class RouteNotFoundException extends Exception {

    private final String routes;

    public RouteNotFoundException(String message, Set<UriMap> routes) {
        super(message);
        this.routes = routes.stream()
                .map(UriMap::toString)
                .collect(Collectors.joining("\n"));
    }

    public String getRoutes() {
        return routes;
    }
}
