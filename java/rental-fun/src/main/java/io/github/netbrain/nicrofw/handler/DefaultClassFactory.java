package io.github.netbrain.nicrofw.handler;

import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class DefaultClassFactory implements ClassFactory {

    private final Map<Class,Object> classes = new HashMap<>();

    public DefaultClassFactory() {
        register(ObjectMapper.class, JsonFactory.create());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> cls) {
        try {
            T instance = (T)classes.get(cls);
            if (instance == null){
                instance = cls.newInstance();
                classes.put(cls,instance);
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void register(Class<?> cls){
        try {
            register(cls.newInstance());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void register(Class<?> cls, Object instance){
        if(!cls.isAssignableFrom(instance.getClass())){
            throw new IllegalArgumentException("Not an implementation of: "+cls);
        }
        classes.put(cls,instance);
    }

    public void register(Object instance){
        if(instance == null){
            throw new IllegalArgumentException("Cannot register null values");
        }
        register(instance.getClass(),instance);
    }
}
