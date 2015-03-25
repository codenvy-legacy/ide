/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.statepersisting.promises;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.ProjectReference;
import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.FunctionException;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.api.promises.client.callback.CallbackPromiseHelper;
import org.eclipse.che.api.promises.client.callback.CallbackPromiseHelper.Call;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.util.loging.Log;

/**
 * //
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class TestPromises {

    private final ProjectServiceClient   projectServiceClient;
    private final DialogFactory          dialogFactory;
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private final EventBus               eventBus;

    @Inject
    public TestPromises(ProjectServiceClient projectServiceClient,
                        DialogFactory dialogFactory,
                        DtoUnmarshallerFactory dtoUnmarshallerFactory,
                        EventBus eventBus) {
        this.projectServiceClient = projectServiceClient;
        this.dialogFactory = dialogFactory;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.eventBus = eventBus;
    }

    private Promise<String> getPromise1() {
        final Call<String, Throwable> projectsCall = new Call<String, Throwable>() {
            @Override
            public void makeCall(final Callback<String, Throwable> callback) {
                Log.info(this.getClass(), "promise 1");
                new Timer() {
                    @Override
                    public void run() {
                        callback.onSuccess("Hello");
                    }
                }.schedule(2000);
//                callback.onFailure(new Exception("e1"));
            }
        };

        final Promise<String> promise = CallbackPromiseHelper.createFromCallback(projectsCall);

        promise.then(new Operation<String>() {
            @Override
            public void apply(String arg) throws OperationException {
                Log.info(TestPromises.class, "proj-ref promise operation");
            }
        });

        return promise;
    }

    public void test1() {
        final Promise<Void> promise = Promises.resolve(null);
        promise.chain(new Function<Void, Promise<String>>() {
            @Override
            public Promise<String> apply(Void arg) throws FunctionException {
                Log.info(TestPromises.class, "in apply");
                return getPromise1();
            }
        }).chain(new Function<String, Promise<String>>() {
            @Override
            public Promise<String> apply(String arg) throws FunctionException {
                Log.info(TestPromises.class, "in apply 2");
                return getPromise1();
            }
        }).then(new Operation<String>() {
            @Override
            public void apply(String arg) throws OperationException {
                Log.info(TestPromises.class, "op performed");
            }
        }).catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
                Log.info(TestPromises.class, "ex performed");
            }
        });
    }

    // add Promises to chain
    public void test2() {
        final Promise<Void> promise = Promises.resolve(null);

//        promise.then(getPromise1());

        final Promise<String> childPromise = promise.then(new Function<Void, String>() {
            @Override
            public String apply(Void arg) throws FunctionException {
                Log.info(TestPromises.class, "function 1 performed");
                return "string to return";
            }
        });

        childPromise.then(new Operation<String>() {
            @Override
            public void apply(String arg) throws OperationException {
                Log.info(TestPromises.class, "p operation performed: " + arg);
            }
        });

        promise.then(new Operation<Void>() {
            @Override
            public void apply(Void arg) throws OperationException {
                throw new OperationException("operation exception");
//                dialogFactory.createMessageDialog("", "operation 1 performed", null).show();
            }
        });

        promise.then(new Operation<Void>() {
            @Override
            public void apply(Void arg) throws OperationException {
                Log.info(TestPromises.class, "operation 2 performed");
            }
        });

        promise.catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
//                Log.info(TestPromises.class, "error handler 2: " + arg.toString());
            }
        });
    }
}
