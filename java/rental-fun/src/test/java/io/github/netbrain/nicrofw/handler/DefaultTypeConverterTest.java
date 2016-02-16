package io.github.netbrain.nicrofw.handler;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DefaultTypeConverterTest {

    @Test
    public void testConvertInt() throws Exception {
        DefaultTypeConverter defaultTypeConverter = new DefaultTypeConverter();
        Class<?> cls = Integer.class;
        Object result = defaultTypeConverter.convert("1", cls);
        assertTrue(result instanceof Integer);
    }
}