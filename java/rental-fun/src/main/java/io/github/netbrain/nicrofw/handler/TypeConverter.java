package io.github.netbrain.nicrofw.handler;

/**
 * Handles the conversion of uri path parameters to expected method argument types.
 */
public interface TypeConverter {
    Object convert(String value, Class<?> toParamType);
}
