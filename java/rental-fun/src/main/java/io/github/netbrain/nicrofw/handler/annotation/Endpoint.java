package io.github.netbrain.nicrofw.handler.annotation;

import fi.iki.elonen.NanoHTTPD.Method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface Endpoint {
    String value();
    Method method() default Method.GET;
}


