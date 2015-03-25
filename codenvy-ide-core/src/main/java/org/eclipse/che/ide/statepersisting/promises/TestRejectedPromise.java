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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.api.promises.client.js.JsPromiseError;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.util.loging.Log;

/**
 * //
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class TestRejectedPromise {

    private final ProjectServiceClient   projectServiceClient;
    private final DialogFactory          dialogFactory;
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private final EventBus               eventBus;

    @Inject
    public TestRejectedPromise(ProjectServiceClient projectServiceClient,
                               DialogFactory dialogFactory,
                               DtoUnmarshallerFactory dtoUnmarshallerFactory,
                               EventBus eventBus) {
        this.projectServiceClient = projectServiceClient;
        this.dialogFactory = dialogFactory;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.eventBus = eventBus;
    }

    // test rejected promise
    public void test0() {
        final Promise<Void> rejectedPromise = Promises.reject(JsPromiseError.create("promise rejected"));

        rejectedPromise.then(new Operation<Void>() {
            @Override
            public void apply(Void arg) throws OperationException {
                Log.info(TestRejectedPromise.class, "apply");
            }
        }, new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
                Log.info(TestRejectedPromise.class, "rejected 0" + arg.toString());
            }
        });

        rejectedPromise.then(new Operation<Void>() {
            @Override
            public void apply(Void arg) throws OperationException {
                Log.info(TestRejectedPromise.class, "apply");
            }
        }, new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
                Log.info(TestRejectedPromise.class, "rejected 1" + arg.toString());
            }
        });

        rejectedPromise.catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
                Log.info(TestRejectedPromise.class, "rejected 2" + arg.toString());
            }
        });
    }
}
