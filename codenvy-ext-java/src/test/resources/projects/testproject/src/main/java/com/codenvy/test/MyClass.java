package com.codenvy.test;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;

public class MyClass<T> extends ArrayList<T> implements Closeable{

    @Override
    public void close() throws IOException {

    }

    @Override
    public boolean add(T t) {
        return super.add(t);
    }

    public int aadad(String parameterName){
        return 0;
    }
}