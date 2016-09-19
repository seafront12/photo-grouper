package org.seafront.photo.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceContext {
    private static GuiceContext instance;
    private final Injector injector;

    public static GuiceContext withInjector(Injector injector){
        instance = new GuiceContext(injector);
        return instance;
    }

    public static GuiceContext instance(){
        if (instance == null){
            instance = withInjector(Guice.createInjector(new PhotoGrouperModule()));
        }
        return instance;
    }

    private GuiceContext(Injector injector){
        this.injector = injector;
    }

    public Injector injector() {
        return injector;
    }
}
