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
package com.codenvy.ide.actions;

import com.codenvy.api.analytics.client.logger.AnalyticsEventLogger;
import com.codenvy.api.vfs.gwt.client.VfsServiceClient;
import com.codenvy.api.vfs.shared.dto.ReplacementSet;
import com.codenvy.api.vfs.shared.dto.Variable;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Map;

/**
 * @author Sergii Leschenko
 */
public class FindReplaceAction extends Action {
    private final VfsServiceClient     vfsServiceClient;
    private final DtoFactory           dtoFactory;
    private final AppContext           appContext;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public FindReplaceAction(VfsServiceClient vfsServiceClient,
                             DtoFactory dtoFactory,
                             AppContext appContext,
                             AnalyticsEventLogger eventLogger) {
        this.vfsServiceClient = vfsServiceClient;
        this.dtoFactory = dtoFactory;
        this.appContext = appContext;
        this.eventLogger = eventLogger;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        eventLogger.log(this);

        if (appContext.getCurrentProject() == null || appContext.getCurrentProject().getRootProject() == null) {
            Log.error(getClass(), "Can not run find/replace without opened project\n");
            return;
        }

        if (event.getParameters() == null) {
            Log.error(getClass(), "Can not run find/replace without parameters");
            return;
        }

        final Map<String, String> parameters = event.getParameters();

        String file = parameters.get("in");
        String find = parameters.get("find");
        String replace = parameters.get("replace");
        String mode = parameters.get("replaceMode");

        final ReplacementSet replacementSet = dtoFactory.createDto(ReplacementSet.class).withFiles(Arrays.asList(file))
                                                        .withEntries(Arrays.asList(dtoFactory.createDto(Variable.class)
                                                                                             .withFind(find)
                                                                                             .withReplace(replace)
                                                                                             .withReplacemode(mode)));

        vfsServiceClient.replaceInCurrentWorkspace(appContext.getCurrentProject().getRootProject(),
                                                   Collections.createArray(replacementSet),
                                                   new AsyncRequestCallback<Void>() {
                                                       @Override
                                                       protected void onSuccess(Void result) {
                                                           //TODO Send event described in IDEX-1743
                                                       }

                                                       @Override
                                                       protected void onFailure(Throwable exception) {

                                                       }
                                                   });
    }
}
