/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 * [2012] - [2013] Codenvy, S.A. 
 * All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.tutorial.gin;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.tutorial.gin.annotation.MyClassWithAnnotationParam;
import com.codenvy.ide.tutorial.gin.annotation.SimpleClass;
import com.codenvy.ide.tutorial.gin.annotation.SimpleInterface;
import com.codenvy.ide.tutorial.gin.factory.MyFactory;
import com.codenvy.ide.tutorial.gin.factory.MyFactoryClass;
import com.codenvy.ide.tutorial.gin.factory.assited.SomeInterface;
import com.codenvy.ide.tutorial.gin.named.MyClassWithNamedParam;
import com.codenvy.ide.tutorial.gin.sample.MyClass;
import com.codenvy.ide.tutorial.gin.sample.MyClassWithProvideParam;
import com.codenvy.ide.tutorial.gin.singleton.MySingletonClass;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/** Extension used to demonstrate how to use GIN. */
@Singleton
@Extension(title = "Gin tutorial", version = "1.0.0")
public class GinExtension {

    @Inject
    public GinExtension(final ConsolePart console,
                        MyClass myClass,
                        MySingletonClass mySingletonClass,
                        Provider<MyClass> myClassProvider,
                        Provider<MySingletonClass> mySingletonClassProvider,
                        AsyncProvider<MyClass> myClassAsyncProvider,
                        MyFactory myFactory,
                        MyClassWithNamedParam myClassWithNamedParam,
                        MyClassWithProvideParam myClassWithProvideParam,
                        MyClassWithAnnotationParam myClassWithAnnotationParam,
                        @SimpleClass SimpleInterface simpleInterface) {

        myClass.doSomething();
        mySingletonClass.doSomething();

        MyClass myClass1 = myClassProvider.get();
        MyClass myClass2 = myClassProvider.get();

        assert !myClass1.equals(myClass2);
        assert !myClass.equals(myClass1);
        assert !myClass.equals(myClass2);

        MySingletonClass mySingletonClass1 = mySingletonClassProvider.get();
        MySingletonClass mySingletonClass2 = mySingletonClassProvider.get();

        assert mySingletonClass1.equals(mySingletonClass2);
        assert mySingletonClass.equals(mySingletonClass1);
        assert mySingletonClass.equals(mySingletonClass2);

        myClassAsyncProvider.get(new AsyncCallback<MyClass>() {
            @Override
            public void onSuccess(MyClass result) {
                result.doSomething();
            }

            @Override
            public void onFailure(Throwable caught) {
                console.print(caught.getMessage());
            }
        });

        MyFactoryClass myFactoryClass1 = myFactory.createMyFactoryClass("my factory class 1");
        myFactoryClass1.doSomething();
        MyFactoryClass myFactoryClass2 = myFactory.createMyFactoryClass("my factory class 2");
        myFactoryClass2.doSomething();

        assert !myFactoryClass1.equals(myFactoryClass2);

        SomeInterface someInterface = myFactory.createSomeInterface("some interface 1");
        someInterface.doSomething();
        SomeInterface someInterface2 = myFactory.createSomeInterface("some interface 2");
        someInterface2.doSomething();

        assert !someInterface.equals(someInterface2);

        myClassWithNamedParam.doSomething();
        myClassWithProvideParam.doSomething();
        myClassWithAnnotationParam.doSomething();
        simpleInterface.doSomething();
    }
}