package io.github.netbrain.nicrofw.handler;

/**
 * Returns instances of handler classes
 */
public interface ClassFactory {
    <T> T get(Class<T> cls);
}
