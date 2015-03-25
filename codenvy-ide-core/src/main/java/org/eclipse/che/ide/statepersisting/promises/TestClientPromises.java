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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.FunctionException;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.api.promises.client.js.Promises;

/**
 * //
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class TestClientPromises {

    private final ProjectServiceClient projectServiceClient;

    @Inject
    public TestClientPromises(ProjectServiceClient projectServiceClient) {
        this.projectServiceClient = projectServiceClient;
    }

    public void test1() {

        projectServiceClient.createProject("/path", null).then(new Operation<ProjectDescriptor>() {
            @Override
            public void apply(ProjectDescriptor arg) throws OperationException {
                projectServiceClient.createProject("/path", null);
            }
        });
    }

    public void test2() {

        projectServiceClient.createProject("/path", null);


        projectServiceClient.createProject("/path", null).then(new Function<ProjectDescriptor, String>() {
            @Override
            public String apply(ProjectDescriptor arg) throws FunctionException {
                return arg.getPath();
            }
        }).then(new Operation<String>() {
            @Override
            public void apply(String projectPath) throws OperationException {

            }
        }).catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
                // handle error
            }
        });

    }

    public void test3() {
        Promise<JsArrayMixed> promise = Promises.all(projectServiceClient.createProject("/p1", null),
                                                     projectServiceClient.createProject("/p2", null),
                                                     projectServiceClient.createProject("/p3", null));

        promise.then(new Operation<JsArrayMixed>() {
            @Override
            public void apply(JsArrayMixed arg) throws OperationException {
                Promise<ProjectDescriptor> promise1 = arg.getObject(0).cast();
                Promise<ProjectDescriptor> promise2 = arg.getObject(1).cast();
                Promise<ProjectDescriptor> promise3 = arg.getObject(2).cast();
            }
        });
    }
}
