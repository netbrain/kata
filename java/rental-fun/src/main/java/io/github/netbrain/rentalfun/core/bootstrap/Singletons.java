package io.github.netbrain.rentalfun.core.bootstrap;


import io.github.netbrain.nicrofw.handler.ClassFactory;
import io.github.netbrain.rentalfun.customer.CustomerHandler;
import io.github.netbrain.rentalfun.customer.CustomerRepository;
import io.github.netbrain.rentalfun.film.FilmHandler;
import io.github.netbrain.rentalfun.film.FilmRepository;
import io.github.netbrain.rentalfun.rental.RentalHandler;
import io.github.netbrain.rentalfun.rental.RentalRepository;
import org.boon.json.JsonFactory;
import org.boon.json.JsonParserFactory;
import org.boon.json.JsonSerializerFactory;
import org.boon.json.ObjectMapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Singletons implements ClassFactory {

    private static Map<String, Object> singletons = new HashMap<>();
    private static Singletons singleton;

    /**
     * Bootstrap's the application with it's dependencies
     * and makes them available through this singleton
     */
    private Singletons() {
        /**
         * Configures the json object mapper and registers an instance with the singleton resource
         */
        JsonSerializerFactory serializerFactory = new JsonSerializerFactory();
        JsonParserFactory parserFactory = new JsonParserFactory();

        serializerFactory
                .usePropertiesFirst()
                .useAnnotations()
                .useJsonFormatForDates()
                .includeDefaultValues()
                .includeNulls()
                .includeEmpty();

        register(ObjectMapper.class,JsonFactory.create(parserFactory, serializerFactory));

        /**
         * Configures the repositories and registers an instance with the singleton resource
         */
        register(new CustomerRepository(get(ObjectMapper.class)));
        register(new FilmRepository(get(ObjectMapper.class)));
        register(new RentalRepository(get(ObjectMapper.class)));

        /**
         * Configures the handlers and registers an instance with the singleton resource
         */
        register(new CustomerHandler(get(CustomerRepository.class)));
        register(new FilmHandler(get(FilmRepository.class)));
        register(new RentalHandler(
                get(CustomerRepository.class),
                get(RentalRepository.class),
                get(FilmRepository.class)
        ));

    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> cls){
        return (T)singletons.get(cls.getCanonicalName());
    }


    private void register(Class<?> cls){
        try {
            register(cls.newInstance());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void register(Class<?> cls, Object instance){
        if(!cls.isAssignableFrom(instance.getClass())){
            throw new IllegalArgumentException("Not an implementation of: "+cls);
        }
        singletons.put(cls.getCanonicalName(),instance);
    }

    private void register(Object instance){
        if(instance == null){
            throw new IllegalArgumentException("Cannot register null values");
        }
        register(instance.getClass(),instance);
    }

    public Collection<Object> getAll() {
        return singletons.values();
    }

    @SuppressWarnings("unchecked")
    public static Singletons getInstance() {
        if(singleton == null){
            singleton = new Singletons();
        }
        return singleton;
    }
}
