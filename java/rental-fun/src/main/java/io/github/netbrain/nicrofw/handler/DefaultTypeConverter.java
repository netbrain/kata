package io.github.netbrain.nicrofw.handler;


public class DefaultTypeConverter implements TypeConverter{
    @Override
    public Object convert(String value, Class<?> unknown) {

        if (int.class.isAssignableFrom(unknown) || Integer.class.isAssignableFrom(unknown)) {
            return convertInteger(value);
        }

        return null;
    }

    private int convertInteger(String value) {
        if(value == null) {
            throw new IllegalArgumentException("NaN");
        }

        value = value.trim();
        return Integer.parseInt(value);
    }


}
