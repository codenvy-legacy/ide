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
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.callback.CallbackPromiseHelper;
import org.eclipse.che.api.promises.client.callback.CallbackPromiseHelper.Call;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.ide.actions.OpenFileAction;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.Presentation;
import org.eclipse.che.ide.toolbar.PresentationFactory;
import org.eclipse.che.ide.util.loging.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * //
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class TestChainingPromises {

    private final PresentationFactory presentationFactory;
    private final ActionManager       actionManager;
    private final OpenFileAction      action;

    @Inject
    public TestChainingPromises(PresentationFactory presentationFactory,
                                ActionManager actionManager,
                                OpenFileAction action) {
        this.presentationFactory = presentationFactory;
        this.actionManager = actionManager;
        this.action = action;
    }

    public void testPromises() {
        final Map<String, String> p1 = new HashMap<>();
        p1.put("file", "pom.xml");

        final Map<String, String> p2 = new HashMap<>();
        p2.put("file", "src/main/webapp/index.jsp");

        final Map<String, String> p3 = new HashMap<>();
        p3.put("file", "src/main/java/com/codenvy/example/spring/GreetingController.java");

        final Map<String, String> p4 = new HashMap<>();
        p4.put("file", "src/main/webapp/WEB-INF/web.xml");

        Promise<Void> promise = Promises.resolve(null);

        promise.then(performAction(action, p2,
                                   performAction(action, p3,
                                                 performAction(action, p4))));
    }

    private Operation<Void> performAction(final Action action, final Map<String, String> parameters) {
        return performAction(action, parameters, null);
    }

    private Operation<Void> performAction(final Action action,
                                          final Map<String, String> parameters,
                                          final Operation<Void> nextOperation) {
        return new Operation<Void>() {
            @Override
            public void apply(Void arg) throws OperationException {
                if (nextOperation == null) {
                    performActionInternal(action, parameters);
                } else {
                    performActionInternal(action, parameters).then(nextOperation);
                }
            }
        };
    }

    private Promise<Void> performActionInternal(final Action action, final Map<String, String> parameters) {
        Log.info(TestChainingPromises.class, "ready for " + parameters.get("file"));

        final Call<Void, Throwable> call = new Call<Void, Throwable>() {
            @Override
            public void makeCall(final Callback<Void, Throwable> callback) {
                final Presentation presentation = presentationFactory.getPresentation(action);
                ActionEvent e = new ActionEvent("", presentation, actionManager, 0, parameters);
                action.actionPerformed(e);

                callback.onSuccess(null);
            }
        };

        return CallbackPromiseHelper.createFromCallback(call);
    }
}
