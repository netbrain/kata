package io.github.netbrain.rentalfun.test;


import io.github.netbrain.rentalfun.App;
import io.github.netbrain.rentalfun.core.bootstrap.Singletons;
import io.github.netbrain.rentalfun.core.persistence.Repository;
import org.boon.json.ObjectMapper;
import org.junit.After;
import org.junit.BeforeClass;

import java.io.IOException;

public class IntegrationTest {
    protected Singletons singletons = Singletons.getInstance();
    protected ObjectMapper json = singletons.get(ObjectMapper.class);
    private static App app;

    @BeforeClass
    public static void initApplication(){
        //Start the application
        if(app != null) return;
        try {
            app = new App();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void resetRepositories(){
        singletons.getAll()
                .stream()
                .filter(i -> i instanceof Repository)
                .forEach(o -> ((Repository)o).truncate());
    }


}
