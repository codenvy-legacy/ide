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
package org.eclipse.che.ide.projecttype.wizard.presenter;

import org.eclipse.che.api.project.shared.dto.RunnerEnvironment;
import org.eclipse.che.api.project.shared.dto.RunnerEnvironmentLeaf;
import org.eclipse.che.api.project.shared.dto.RunnerEnvironmentTree;
import org.eclipse.che.api.runner.gwt.client.RunnerServiceClient;
import org.eclipse.che.ide.collections.Collections;
import org.eclipse.che.ide.collections.StringMap;
import org.eclipse.che.ide.json.JsonHelper;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.rest.Unmarshallable;
import org.eclipse.che.ide.util.loging.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;

/**
 * Helps to get description of the runner by it's ID.
 *
 * @author Artem Zatsarynnyy
 */

@Singleton
class RunnersRegistry {
    private final StringMap<String> descriptions;

    @Inject
    RunnersRegistry(RunnerServiceClient runnerServiceClient, DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        descriptions = Collections.createStringMap();

        final Unmarshallable<RunnerEnvironmentTree> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(RunnerEnvironmentTree.class);
        runnerServiceClient.getRunners(new AsyncRequestCallback<RunnerEnvironmentTree>(unmarshaller) {
            @Override
            protected void onSuccess(RunnerEnvironmentTree result) {
                fillDescriptionsRecursively(result);
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(getClass(), JsonHelper.parseJsonMessage(exception.getMessage()));
            }
        });
    }

    private void fillDescriptionsRecursively(RunnerEnvironmentTree tree) {
        for (RunnerEnvironmentTree envTree : tree.getNodes()) {
            for (RunnerEnvironmentLeaf leaf : envTree.getLeaves()) {
                final RunnerEnvironment env = leaf.getEnvironment();
                if (env.getDescription() != null) {
                    descriptions.put(env.getId(), env.getDescription());
                }
            }
            fillDescriptionsRecursively(envTree);
        }
    }

    /** Returns description of the given runner. */
    String getDescription(@Nonnull String runnerId) {
        return descriptions.get(runnerId);
    }
}
