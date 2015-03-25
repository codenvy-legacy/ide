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
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.ProjectReference;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.api.promises.client.callback.CallbackPromiseHelper;
import org.eclipse.che.api.promises.client.callback.CallbackPromiseHelper.Call;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.rest.Unmarshallable;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.util.loging.Log;

/**
 * //
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class TestSeveralPromises {

    private final ProjectServiceClient   projectServiceClient;
    private final DialogFactory          dialogFactory;
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private final EventBus               eventBus;

    @Inject
    public TestSeveralPromises(ProjectServiceClient projectServiceClient,
                               DialogFactory dialogFactory,
                               DtoUnmarshallerFactory dtoUnmarshallerFactory,
                               EventBus eventBus) {
        this.projectServiceClient = projectServiceClient;
        this.dialogFactory = dialogFactory;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.eventBus = eventBus;
    }

    public void testPromises() {
        final Promise<JsArrayMixed> all = Promises.all(getPromise1(), getPromise2());

        all.then(new Operation<JsArrayMixed>() {
            @Override
            public void apply(JsArrayMixed arg) throws OperationException {
                Log.info(TestSeveralPromises.class, "all promises resolved");
            }
        });

        all.catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
                Log.info(TestSeveralPromises.class, "all promises rejected: " + arg.toString());
            }
        });
    }

    private Promise<Array<ProjectReference>> getPromise1() {
        final Call<Array<ProjectReference>, Throwable> projectsCall = new Call<Array<ProjectReference>, Throwable>() {
            @Override
            public void makeCall(final Callback<Array<ProjectReference>, Throwable> callback) {
                new Timer() {
                    @Override
                    public void run() {
                        Log.info(this.getClass(), "promise 1");
                        getProjects(callback);
                    }
                }.schedule(2000);
            }
        };

        final Promise<Array<ProjectReference>> promise = CallbackPromiseHelper.createFromCallback(projectsCall);

        promise.then(new Operation<Array<ProjectReference>>() {
            @Override
            public void apply(Array<ProjectReference> arg) throws OperationException {
                Log.info(this.getClass(), "promise 1: then 1");
            }
        });

        promise.then(new Operation<Array<ProjectReference>>() {
            @Override
            public void apply(Array<ProjectReference> arg) throws OperationException {
                Log.info(this.getClass(), "promise 1: then 2");
            }
        });

        return promise;
    }

    private Promise<Array<ProjectReference>> getPromise2() {
        final Call<Array<ProjectReference>, Throwable> projectsCall = new Call<Array<ProjectReference>, Throwable>() {
            @Override
            public void makeCall(final Callback<Array<ProjectReference>, Throwable> callback) {
                Log.info(this.getClass(), "promise 2");
                getProjects(callback);
            }
        };

        final Promise<Array<ProjectReference>> promise = CallbackPromiseHelper.createFromCallback(projectsCall);

        promise.then(new Operation<Array<ProjectReference>>() {
            @Override
            public void apply(Array<ProjectReference> arg) throws OperationException {
                Log.info(this.getClass(), "promise 2: then 1");
            }
        });

        promise.then(new Operation<Array<ProjectReference>>() {
            @Override
            public void apply(Array<ProjectReference> arg) throws OperationException {
                Log.info(this.getClass(), "promise 2: then 2");
            }
        });

        return promise;
    }

    private void getProjects(final Callback<Array<ProjectReference>, Throwable> callback) {
//        callback.onFailure(new Exception("eee"));

        Unmarshallable<Array<ProjectReference>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(ProjectReference.class);
        projectServiceClient.getProjects(new AsyncRequestCallback<Array<ProjectReference>>(unmarshaller) {
            @Override
            protected void onSuccess(Array<ProjectReference> result) {
                callback.onSuccess(result);
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }
}
