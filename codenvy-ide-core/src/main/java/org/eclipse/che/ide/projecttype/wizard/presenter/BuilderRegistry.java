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

import org.eclipse.che.api.builder.dto.BuilderDescriptor;
import org.eclipse.che.api.builder.dto.BuilderEnvironment;
import org.eclipse.che.api.builder.gwt.client.BuilderServiceClient;
import org.eclipse.che.ide.collections.Array;
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
 * Helps to get name of the builder's default environment by builder name.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
class BuilderRegistry {
    private final StringMap<String> environments;

    @Inject
    BuilderRegistry(BuilderServiceClient builderServiceClient, DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        environments = Collections.createStringMap();

        final Unmarshallable<Array<BuilderDescriptor>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(BuilderDescriptor.class);
        builderServiceClient.getRegisteredServers(new AsyncRequestCallback<Array<BuilderDescriptor>>(unmarshaller) {
            @Override
            protected void onSuccess(Array<BuilderDescriptor> result) {
                for (BuilderDescriptor builderDescriptor : result.asIterable()) {
                    for (BuilderEnvironment environment : builderDescriptor.getEnvironments().values()) {
                        if (environment.getIsDefault()) {
                            environments.put(builderDescriptor.getName(), environment.getDisplayName());
                            break;
                        }
                    }
                }
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(getClass(), JsonHelper.parseJsonMessage(exception.getMessage()));
            }
        });
    }

    /** Returns display name of the default environment for the given builder. */
    String getDefaultEnvironmentName(@Nonnull String builderName) {
        return environments.get(builderName);
    }
}
