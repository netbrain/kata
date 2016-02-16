package io.github.netbrain.nicrofw;

import fi.iki.elonen.NanoHTTPD.Method;
import io.github.netbrain.nicrofw.handler.Handler;

import java.util.LinkedHashMap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UriMap {
    private final String[] namedParameters;
    private final Pattern pattern;
    private final Method method;
    private final String prettyUrl;
    private Handler handler;

    /**
     * Creates a new uri matcher against urls with named parameters. i.e /entities/:page/:max
     *
     * @param url
     */
    public UriMap(String url) {
        this(Method.GET,url);
    }

    public UriMap(Method method, String url) {
        this.prettyUrl = url;
        this.method = method;

        namedParameters = new String[countNamedParams(url)];
        int namedIndex = 0;

        String[] urlParts = url.split("/");
        for (int i = 0; i < urlParts.length; i++) {
            if (urlParts[i].startsWith(":")) {
                namedParameters[namedIndex++] = urlParts[i].substring(1);
                urlParts[i] = "([A-z0-9]+)";
            }
        }

        this.pattern = Pattern.compile(String.join("/", urlParts));
    }

    public UriMap(Method method, String value, Handler handler) {
        this(method,value);
        this.handler = handler;
    }

    private int countNamedParams(String url) {
        int sum = 0;
        for (int i = 0; i < url.length(); i++) {
            if (url.charAt(i) == ':') {
                sum++;
            }
        }
        return sum;
    }

    /**
     * Returns a map with matches if the url matches the pattern
     *
     *
     * @param method
     * @param url
     * @return Map
     */
    public LinkedHashMap<String, String> matches(Method method, String url) {
        if(url == null || !this.method.equals(method)){
            return null;
        }
        Matcher matcher = pattern.matcher(url);
        if (!matcher.matches()) {
            return null;
        }
        LinkedHashMap<String, String> pathParams = new LinkedHashMap<>();
        MatchResult result = matcher.toMatchResult();
        for (int i = 0; i < namedParameters.length; i++) {
            pathParams.put(namedParameters[i], result.group(i + 1));
        }
        return pathParams;
    }

    public Handler getHandler() {
        return handler;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", method, prettyUrl);
    }
}
